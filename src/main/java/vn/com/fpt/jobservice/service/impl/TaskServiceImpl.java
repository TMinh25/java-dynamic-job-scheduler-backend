package vn.com.fpt.jobservice.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repository.TaskRepository;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

  @Autowired
  private TaskRepository taskRepository;

  // @Override
  // public PagedResponse<Task> searchTasks(
  // String id,
  // String name,
  // String status,
  // String ticketId,
  // Pageable pageable) {
  // log.debug("searchTasks - START");
  // Page<Task> entityPage = taskRepository.findByIdOrNameOrStatusOrTicketId(id,
  // name, status, ticketId, pageable);
  // log.debug("searchTasks - END");
  // return new PagedResponse<>(entityPage);
  // }

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
  public Task readTaskById(String id) throws Exception {
    log.debug("readTaskById - START");
    Optional<Task> entity = taskRepository.findById(id);
    if (entity.isEmpty()) {
      throw new Exception("Task not found!");
    }
    log.debug("readTaskById - END");
    return entity.get();
  }

  @Override
  public Task createTask(Task task) throws Exception {
    log.debug("createTask - START");
    Optional<Task> taskEntity = taskRepository.findById(task.getId());
    if (taskEntity.isPresent()) {
      throw new Exception("Task existed with id " + task.getId());
    }
    task.setId(null);
    task.setStatus(TaskStatus.PENDING);
    task.setRetryCount(0);
    if (task.getStartStep() == null) {
      task.setStartStep(0);
    }
    Task newTaskEntity = taskRepository.save(task);
    log.debug("createTask - END");
    return newTaskEntity;
  }

  @Override
  public ResponseEntity<Object> deleteTaskById(String id) throws RuntimeException {
    log.debug("deleteTaskById - START");
    Task taskEntity = taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", id));

    taskRepository.delete(taskEntity);

    log.debug("deleteTaskById - END");
    return ResponseEntity.ok().build();
  }

  @Override
  @Transactional
  public Task updateTaskById(String id, TaskModel taskDetails) throws Exception {
    Task task = taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

    // Copy non-null properties from taskDetails to task
    BeanUtils.copyProperties(taskDetails, task, Utils.getNullPropertyNames(taskDetails));

    return taskRepository.save(task);
  }
}
