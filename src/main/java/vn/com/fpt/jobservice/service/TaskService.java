package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.com.fpt.jobservice.entity.TaskEntity;

public interface TaskService {
  Page<TaskEntity> readAllTasks(Pageable pageable);

  TaskEntity readTaskById(Long id) throws Exception;

  TaskEntity createTask(TaskEntity task) throws Exception;

  ResponseEntity<?> deleteTaskById(Long id) throws Exception;
}
