package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import vn.com.fpt.jobservice.entity.TaskEntity;
import vn.com.fpt.jobservice.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  TaskService taskService;

  @GetMapping()
  public Page<TaskEntity> readAllTasks(Pageable pageable) {
    return taskService.readAllTasks(pageable);
  }

  @GetMapping("/{id}")
  public TaskEntity readTaskById(@PathVariable(value = "id") Long id) throws Exception {
    return taskService.readTaskById(id);
  }

  @PostMapping()
  public TaskEntity createTask(@Validated @RequestBody TaskEntity taskEntity) throws Exception {
    return taskService.createTask(taskEntity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") Long id) throws Exception {
    return taskService.deleteTaskById(id);
  }
}
