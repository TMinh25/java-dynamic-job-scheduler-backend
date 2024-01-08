package vn.com.fpt.jobservice.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.TaskEntity;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repository.TaskRepository;
import vn.com.fpt.jobservice.service.TaskService;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

  @Autowired
  private TaskRepository taskRepository;

  @Override
  public Page<TaskEntity> readAllTasks(Pageable pageable) {
    log.debug("readAllTasks - START");
    Page<TaskEntity> entityPage = taskRepository.findAll(pageable);
    log.debug("readAllTasks - END");
    return entityPage;
  }

  @Override
  public TaskEntity readTaskById(Long id) throws Exception {
    log.debug("readTaskById - START");
    Optional<TaskEntity> entity = taskRepository.findById(id);
    if (entity.isEmpty()) {
      throw new Exception("Task not found!");
    }
    log.debug("readTaskById - END");
    return entity.get();
  }

  @Override
  public TaskEntity createTask(TaskEntity task) throws Exception {
    log.debug("createTask - START");
    Optional<TaskEntity> taskEntity = taskRepository.findById(task.getId());
    if (taskEntity.isPresent()) {
      throw new Exception("Task existed with id " + task.getId());
    }
    task.setId(null);
    System.out.println(task);
    TaskEntity newTaskEntity = taskRepository.save(task);
    log.debug("createTask - END");
    return newTaskEntity;
  }

  @Override
  public ResponseEntity<?> deleteTaskById(Long id) throws Exception {
    log.debug("deleteTaskById - START");
    TaskEntity taskEntity = taskRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Tasks", "id", id));

    taskRepository.delete(taskEntity);

    taskRepository.deleteById(id);
    log.debug("deleteTaskById - END");
    return ResponseEntity.ok().build();
  }
}
