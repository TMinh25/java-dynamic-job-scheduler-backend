package vn.com.fpt.jobservice.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.quartz.SchedulerException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
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
import vn.com.fpt.jobservice.utils.Utils;

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
    log.info("readAllTasks - START");
    Page<Task> entityPage = taskRepository.searchByString(pageable, searchQuery);
    // Page<Task> entityPage = taskRepository.findAll(pageable);
    log.info("readAllTasks - END");
    return new PagedResponse<>(entityPage);
  }

  @Override
  public PagedResponse<Task> readAllTasks(Pageable pageable) {
    log.info("readAllTasks - START");
    Page<Task> entityPage = taskRepository.findAll(pageable);
    // Page<Task> entityPage = taskRepository.findAll(pageable);
    log.info("readAllTasks - END");
    return new PagedResponse<>(entityPage);
  }

  @Override
  public Optional<Task> readTaskById(String id) throws Exception {
    log.info("readTaskById - START");
    Optional<Task> entity = taskRepository.findById(id);
    log.info("readTaskById - END");
    return entity;
  }

  @Override
  public Optional<Task> readTaskByJobUUID(String jobUUID) {
    log.info("readTaskByJobUUID - START");
    Optional<Task> entity = taskRepository.findByJobUUID(jobUUID);
    log.info("readTaskByJobUUID - END");
    return entity;
  }

  @Override
  public Task createTask(final Task task) throws Exception {
    log.info("createTask - START");
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

    TaskType taskType = taskTypeRepository.findByName(task.getTaskType().getName())
        .orElseThrow(() -> new ResourceNotFoundException("Task type", "name", task.getTaskType().getName()));

    task.setTaskType(taskType);

    Task newTaskEntity = taskRepository.save(task);
    if (newTaskEntity.canScheduleJob())
      scheduleJob(newTaskEntity, taskType);
    log.info("createTask - END");
    return newTaskEntity;
  }

  @Override
  public boolean scheduleJob(Task task, TaskType taskType) {
    try {
      String jobClassName = "vn.com.fpt.jobservice.jobs."
          + AutomationTaskType.valueOf(AutomationTaskType.class, taskType.getName());

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
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public ResponseEntity<Object> deleteTaskById(String id) {
    log.info("deleteTaskById - START");
    Task taskEntity = taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", id));
    String jobUUID = taskEntity.getJobUUID();

    taskRepository.delete(taskEntity);
    if (_jobService.isJobWithNamePresent(jobUUID)) {
      _jobService.unscheduleJob(jobUUID);
      _jobService.deleteJob(jobUUID);
    }
    log.info("deleteTaskById - END");
    return ResponseEntity.ok().build();
  }

  @Override
  public Task updateTaskById(String id, TaskModel taskDetails) {
    log.info("updateTaskById - START");
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

    log.info("updateTaskById - END");
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
