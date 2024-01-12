package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  @Autowired
  TaskService taskService;

  @GetMapping()
  public PagedResponse<Task> searchTasks(Pageable pageable,
      @RequestParam(value = "search", required = false, defaultValue = "") String searchQuery) {
    return taskService.searchTasks(pageable, searchQuery);
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public Task createTask(@Validated @RequestBody TaskModel taskModel) throws Exception {
    Task task = taskModel.toEntity();
    return taskService.createTask(task);
  }

  @GetMapping("/{id}")
  public Task readTaskById(@PathVariable(value = "id") String id) throws Exception {
    return taskService.readTaskById(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") String id) throws Exception {
    return taskService.deleteTaskById(id);
  }

  @PatchMapping("/{id}")
  public Task patchActivateTask(@PathVariable(value = "id") String id, @Validated @RequestBody TaskModel taskModel)
      throws Exception {
    return taskService.updateTaskById(id, taskModel);
  }
}
