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
import vn.com.fpt.jobservice.jobs.base.IntegrationJob;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskHistoryRepository;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.TaskTypeType;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskHistoryRepository taskHistoryRepository;
    @Autowired
    private TaskTypeRepository taskTypeRepository;
    @Autowired
    private JobService _jobService;

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
    public TaskModel readTaskById(String id) throws Exception {
        log.debug("readTaskById - START");
        Task entity = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        log.debug("readTaskById - END");
        return entity.toModel();
    }

    @Override
    public Task readTaskByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception {
        log.debug("readTaskById - START");
        Task entity = taskRepository.findByTicketIdAndPhaseId(ticketId, phaseId)
                .orElseThrow(() -> new Exception(
                        String.format("Task not found with ticketId = '%s' and phaseId = '%s'", ticketId, phaseId)));
        log.debug("readTaskById - END");
        return entity;
    }

    @Override
    public Task readTaskByJobUUID(String jobUUID) {
        log.debug("readTaskByJobUUID - START");
        Task entity = taskRepository.findByJobUUID(jobUUID).orElseThrow(() -> new ResourceNotFoundException("Task", "jobUUID", jobUUID));;
        log.debug("readTaskByJobUUID - END");
        return entity;
    }

    @Override
    @Transactional
    public Task createTask(final Task task) throws Exception {
        log.debug("createTask - START");
        Optional<Task> taskExisted = taskRepository.findById(task.getId());
        if (taskExisted.isPresent()) {
            throw new Exception("Task existed with id " + task.getId());
        }
        task.setId(null);
        task.setStatus(TaskStatus.PENDING);
        task.setRetryCount(0);
        if (task.getStartStep() == null) {
            task.setStartStep(0);
        }

        TaskType taskType = taskTypeRepository.findById(task.getTaskType().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Task type", "id", task.getTaskType().getId()));

        task.setTaskType(taskType);
        task.setName(taskType.getName());

        Task newTaskEntity = taskRepository.save(task);
        if (newTaskEntity.canScheduleJob())
            scheduleJob(newTaskEntity, taskType);
        log.debug("createTask - END");
        return newTaskEntity;
    }

    @Override
    @Transactional
    public boolean scheduleJob(Task task, TaskType taskType) {
        try {
            String jobClassName = "vn.com.fpt.jobservice.jobs." + taskType.getClassName();
            if (taskType.getType() == TaskTypeType.SYSTEM) { // Job mặc định của hệ thống
                Class<?> jobClass = Class.forName(jobClassName);

                if (SystemJob.class.isAssignableFrom(jobClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends SystemJob> systemJob = (Class<? extends SystemJob>) jobClass;

                    _jobService.scheduleCronJob(
                            task.getJobUUID(),
                            systemJob,
                            task.getNextInvocation(),
                            task.getCronExpression());
                } else {
                    throw new Exception(jobClassName + " is not a subclass of SystemJob!");
                }
            } else if (taskType.getType() == TaskTypeType.INTEGRATION) { // Job tích hợp
                Class<?> jobClass = Class.forName(jobClassName);
                if (IntegrationJob.class.isAssignableFrom(jobClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends IntegrationJob> integrationJob = (Class<? extends IntegrationJob>) jobClass;

                    _jobService.scheduleCronJob(
                            task.getJobUUID(),
                            integrationJob,
                            task.getNextInvocation(),
                            task.getCronExpression());
                } else {
                    throw new Exception(jobClassName + " is not a subclass of IntegrationJob!");
                }
            } else if (taskType.getType() == TaskTypeType.CUSTOM) { // Job tự định nghĩa
                log.info("Custom job");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    @Transactional
    public ResponseEntity<Object> deleteTaskById(String id) {
        log.debug("deleteTaskById - START");
        Task taskEntity = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", id));
        String jobUUID = taskEntity.getJobUUID();

        taskRepository.delete(taskEntity);
        if (_jobService.isJobWithNamePresent(jobUUID)) {
            _jobService.unscheduleJob(jobUUID);
            _jobService.deleteJob(jobUUID);
        }
        log.debug("deleteTaskById - END");
        return ResponseEntity.ok().build();
    }

    @Override
    @Transactional
    public Task updateTaskById(String id, TaskModel taskDetails) {
        log.debug("updateTaskById - START");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (!task.canUpdateTask()) {
            throw new IllegalStateException("Task is not updatable");
        }

        // String[] nullProps = Utils.getNullPropertyNames(taskDetails);
        // OriginalAndUpdatedData historyData = Utils.getOriginalAndUpdatedData(task,
        // taskDetails);
        if (taskDetails != null) {
            BeanUtils.copyProperties(taskDetails, task, Utils.getNullPropertyNames(taskDetails));
            task = taskRepository.save(task);
        }

        String jobUUID = task.getJobUUID();
        Date nextInvocation = task.getNextInvocation();
        String cronExpression = task.getCronExpression();

        // TaskHistory history = new TaskHistory();
        // ObjectMapper objectMapper = new ObjectMapper();
        // try {
        // history.setOldData(objectMapper.writeValueAsString(historyData.getOldData()));
        // history.setNewData(objectMapper.writeValueAsString(historyData.getNewData()));
        // } catch (JsonProcessingException e) {
        // log.error("Failed to convert to json string: " + e.getMessage());
        // }
        // history.setTask(task);
        // history.setStartedAt(new Date());
        // taskHistoryRepository.save(history);

        _jobService.updateCronJob(jobUUID, nextInvocation, cronExpression);
        if (task.canScheduleJob()) {
            _jobService.resumeJob(jobUUID);
        } else {
            _jobService.pauseJob(jobUUID);
        }

        log.debug("updateTaskById - END");
        return task;
    }

    @Override
    public ResponseEntity<Object> triggerJob(String id) throws Exception {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        if (_jobService.isJobWithNamePresent(task.getJobUUID())) {
            boolean isJobRunning = _jobService.isJobRunning(task.getJobUUID());

            if (isJobRunning) {
                throw new Exception("Job already in processing state");
            }
        } else {
            throw new ResourceNotFoundException("Job", "jobName", task.getJobUUID());
        }

        if (task.getRetryCount() >= task.getMaxRetries()) {
            throw new SchedulerException("The job has run out of reruns.");
        }

        _jobService.triggerJob(task.getJobUUID());

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Object> interuptJob(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        _jobService.interuptJob(task.getJobUUID());

        return ResponseEntity.ok().build();
    }
}
