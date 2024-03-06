package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskHistoryService;
import vn.com.fpt.jobservice.service.TaskSchedulerService;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.TaskTypeType;
import vn.com.fpt.jobservice.utils.Utils;

import java.text.ParseException;
import java.util.*;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskTypeRepository taskTypeRepository;

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Autowired
    private JobService jobService;

    @Override
    public List<Task> getPendingTasks() {
        return taskRepository.findByStatusAndNextInvocationBefore(TaskStatus.PENDING, new Date());
    }

    @Override
    public PagedResponse<Task> searchTasks(Pageable pageable, String searchQuery) {
        log.debug("readAllTasks - START");
        Page<Task> entityPage = taskRepository.searchByString(pageable, searchQuery);
        // Page<Task> entityPage = taskRepository.findAll(pageable);
        log.debug("readAllTasks - END");
        return new PagedResponse<>(entityPage);
    }

    @Override
    public PagedResponse<Task> readAllTasks(Pageable pageable) {
        log.debug("readAllTasks - START");
        Page<Task> entityPage = taskRepository.findAll(pageable);
        // Page<Task> entityPage = taskRepository.findAll(pageable);
        log.debug("readAllTasks - END");
        return new PagedResponse<>(entityPage);
    }

    @Override
    public Task readTaskById(String id) {
        log.debug("readTaskById - START");
        Task entity = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        log.debug("readTaskById - END");
        return entity;
    }

    @Override
    public Task readTaskByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception {
        log.debug("readTaskById - START");
        Task entity = taskRepository.findFirstByTicketIdAndPhaseIdOrderByCreatedAtDesc(ticketId, phaseId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task not found with ticketId = '%s' and phaseId = '%s'", ticketId, phaseId)));
        log.debug("readTaskById - END");
        return entity;
    }

    @Override
    public Boolean readActiveByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception {
        log.debug("readTaskStatusByTicketIdAndPhaseId - START");
        Task task = readTaskByTicketIdAndPhaseId(ticketId, phaseId);
        log.debug("readTaskStatusByTicketIdAndPhaseId - END");
        return task.getActive();
    }

    @Override
    public Task readTaskByJobUUID(String jobUUID) {
        log.debug("readTaskByJobUUID - START");
        Task entity = taskRepository.findByJobUUID(jobUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "jobUUID", jobUUID));
        ;
        log.debug("readTaskByJobUUID - END");
        return entity;
    }

    @Override
    @Transactional
    public Task createTask(final Task task) throws Exception {
        log.debug("createTask - START");
        Optional<Task> taskExisted = taskRepository.findById(task.getId());
        TaskType taskType = null;
        if (taskExisted.isPresent()) {
            throw new Exception("Task existed with id " + task.getId());
        }
        task.setId(null);
        task.setRetryCount(0);
        if (task.getStartStep() == null) {
            task.setStartStep(0);
        }

        if (task.getTaskType() != null && task.getTaskType().getId() != null && task.getTaskType().getId() != 0) {
            taskType = taskTypeRepository.findById(task.getTaskType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task type", "id", task.getTaskType().getId()));
            if (task.getName() == null || task.getName().isEmpty()) {
                task.setName(taskType.getName());
                if (task.getTicketId() != null) {
                    task.setName(String.format("%s - ID Ticket %s", task.getName(), task.getTicketId()));
                }
            }
            task.setTaskType(taskType);
        }

        Task newTaskEntity = taskRepository.save(task);
        if (newTaskEntity.canScheduleJob()) {
            scheduleTask(newTaskEntity);
        }
        log.debug("createTask - END");
        return newTaskEntity;
    }

    @Override
    public Boolean scheduleTask(Task task) {
        try {
            task.generateNextInvocation();
            TaskType taskType = task.getTaskType();
            String jobClassName = "vn.com.fpt.jobservice.jobs." + taskType.getClassName();
            List<TaskTypeType> taskTypes = Arrays.asList(
                    TaskTypeType.SYSTEM,
                    TaskTypeType.INTEGRATION,
                    TaskTypeType.MANUAL);
            if (taskTypes.contains(taskType.getType())) { // Job của hệ thống
                Class<?> jobClass = Class.forName(jobClassName);

                if (BaseJob.class.isAssignableFrom(jobClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends BaseJob> systemJob = (Class<? extends BaseJob>) jobClass;

                    jobService.scheduleCronJob(
                            task.getJobUUID(),
                            systemJob,
                            task.getNextInvocation(),
                            task.getCronExpression());
                } else {
                    throw new Exception(jobClassName + " is not a subclass of BaseJob!");
                }
            } else if (taskType.getType() == TaskTypeType.CUSTOM) { // Job tự định nghĩa
                log.info("Custom job");
                throw new Error("Job is not defined!");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), (Object) e.getStackTrace());
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean unscheduleTask(Task task, Boolean isUpdate) {
        Set<TaskStatus> cannotUnscheduleTaskStatus = new HashSet<>(Set.of(TaskStatus.PROCESSING, TaskStatus.SUCCESS));
        if (cannotUnscheduleTaskStatus.contains(task.getStatus())) {
            log.error(String.format("Error unscheduling job with ID: %s. The job status is %s. ", task.getId(), task.getStatus()));
            return false;
        } else {
            if (isUpdate) {
                task.setStatus(TaskStatus.CANCELED);
                task.setActive(false);
                updateTask(task.getId(), task.toModel());
            }
            return true;
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Object> deleteTaskById(String id) {
        log.debug("deleteTaskById - START");
        Task taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", id));
        String jobUUID = taskEntity.getJobUUID();
        taskHistoryService.deleteAllHistoriesOfTask(taskEntity.getId());
        taskRepository.delete(taskEntity);
        if (jobService.isJobWithNamePresent(jobUUID)) {
            jobService.unscheduleJob(jobUUID);
            jobService.deleteJob(jobUUID);
        }
        log.debug("deleteTaskById - END");
        return ResponseEntity.ok().build();
    }

    @Override
    public Task updateTaskById(String id, TaskModel taskDetails) {
        log.debug("updateTaskById - START");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (!task.canUpdateTask()) {
            throw new IllegalStateException("Task is not updatable");
        }

        updateTask(id, taskDetails);
        log.debug("updateTaskById - END");
        return task;
    }

    @Override
    @Transactional
    public Task updateTask(String id, TaskModel taskDetails) {
        log.debug("updateTask - START");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (taskDetails != null) {
            BeanUtils.copyProperties(taskDetails, task, Utils.getNullPropertyNames(taskDetails));
            task = taskRepository.save(task);
        }

        String jobUUID = task.getJobUUID();
        if (task.canScheduleJob()) {
            if (!jobService.isJobWithNamePresent(task.getJobUUID())) {
                scheduleTask(task);
            } else {
                String cronExpression = task.getCronExpression();
                Date nextInvocation = null;
                try {
                    nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                jobService.updateCronJob(jobUUID, nextInvocation, cronExpression);
            }
        } else {
            task.setNextInvocation(null);
            task = taskRepository.save(task);
            jobService.unscheduleJob(jobUUID);
            jobService.deleteJob(jobUUID);
        }

        log.debug("updateTask - END");
        return task;
    }

    @Override
    public Boolean triggerJob(String taskId) throws Exception {
        try {
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

            if (jobService.isJobWithNamePresent(task.getJobUUID())) {
                boolean isJobRunning = jobService.isJobRunning(task.getJobUUID());

                if (isJobRunning) {
                    throw new Exception("Job already in processing state");
                }
            } else {
                throw new ResourceNotFoundException("Job", "jobName", task.getJobUUID());
            }

            if (task.getMaxRetries() != null && task.getRetryCount() >= task.getMaxRetries()) {
                throw new SchedulerException("The job has run out of reruns.");
            }

            jobService.triggerJob(task.getJobUUID());

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), (Object) e.getStackTrace());
            return false;
        }
    }

    @Override
    public ResponseEntity<Object> interuptJob(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        jobService.interuptJob(task.getJobUUID());

        return ResponseEntity.ok().build();
    }
}
