package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.utils.AutomationTaskType;
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
    public Optional<Task> readTaskById(String id) throws Exception {
        log.debug("readTaskById - START");
        Optional<Task> entity = taskRepository.findById(id);
        log.debug("readTaskById - END");
        return entity;
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
    public Optional<Task> readTaskByJobUUID(String jobUUID) {
        log.debug("readTaskByJobUUID - START");
        Optional<Task> entity = taskRepository.findByJobUUID(jobUUID);
        log.debug("readTaskByJobUUID - END");
        return entity;
    }

    @Override
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
    public boolean scheduleJob(Task task, TaskType taskType) {
        try {
            if (taskType.getType() == TaskTypeType.SYSTEM) { // Job mặc định của hệ thống
                String jobClassName = "vn.com.fpt.jobservice.jobs." + taskType.getClassName();
                Class<?> jobClass = Class.forName(jobClassName);

                if (QuartzJobBean.class.isAssignableFrom(jobClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends QuartzJobBean> quartzJobClass = (Class<? extends QuartzJobBean>) jobClass;

                    _jobService.scheduleCronJob(
                            task.getJobUUID(),
                            quartzJobClass,
                            task.getNextInvocation(),
                            task.getCronExpression());
                } else {
                    throw new Exception("Class is not a subclass of QuartzJobBean: " +
                            jobClassName);
                }
            } else if (taskType.getType() == TaskTypeType.CUSTOM) { // Job tự định nghĩa

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
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
    public Task updateTaskById(String id, TaskModel taskDetails) {
        log.debug("updateTaskById - START");
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        // Copy non-null properties from taskDetails to task
        BeanUtils.copyProperties(taskDetails, task, Utils.getNullPropertyNames(taskDetails));
        task = taskRepository.save(task);

        String jobUUID = task.getJobUUID();
        Date nextInvocation = task.getNextInvocation();
        String cronExpression = task.getCronExpression();

        _jobService.updateCronJob(jobUUID, nextInvocation, cronExpression);
        if (task.canScheduleJob()) {
            _jobService.resumeJob(jobUUID);
        } else {
            _jobService.pauseJob(jobUUID);
        }

        // if (_jobService.isJobWithNamePresent(jobUUID)) {
        // _jobService.unscheduleJob(jobUUID);
        // _jobService.deleteJob(jobUUID);
        // }
        // if (task.canScheduleJob())
        // scheduleJob(task, task.getTaskType());

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
