package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;

public interface TaskService {
  PagedResponse<Task> searchTasks(Pageable pageable, String searchQuery);

  PagedResponse<Task> readAllTasks(Pageable pageable);

  Task readTaskById(String id) throws Exception;

  Task createTask(Task task) throws Exception;

  ResponseEntity<Object> deleteTaskById(String id) throws RuntimeException;

  Task updateTaskById(String id, TaskModel task) throws Exception;
}
