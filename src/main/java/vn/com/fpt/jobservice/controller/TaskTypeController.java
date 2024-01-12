package vn.com.fpt.jobservice.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.TaskTypeModel;
import vn.com.fpt.jobservice.repository.TaskTypeRepository;

@RestController
@RequestMapping("/task-types")
public class TaskTypeController {
  @Autowired
  TaskTypeRepository taskTypeRepo;

  @GetMapping()
  public Page<TaskType> readAllTasks(Pageable pageable) {
    return taskTypeRepo.findAll(pageable);
  }

  @GetMapping("/{id}")
  public TaskType readTaskById(@PathVariable(value = "id") Long id) throws Exception {
    Optional<TaskType> entity = taskTypeRepo.findById(id);
    if (entity.isEmpty()) {
      throw new Exception("Task not found!");
    }
    return entity.get();
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public TaskType createTask(@Validated @RequestBody TaskTypeModel taskModel) throws Exception {
    TaskType taskType = taskModel.toEntity();
    return taskTypeRepo.save(taskType);
  }

  @PatchMapping("/{id}")
  public TaskType updateTask(
      @PathVariable(value = "id") Long taskId,
      @Valid @RequestBody TaskTypeModel taskTypeDetails) throws Exception {
    TaskType task = taskTypeRepo.findById(taskId)
        .orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", taskId));

    task.setName(taskTypeDetails.getName());
    TaskType updatedTask = taskTypeRepo.save(task);
    return updatedTask;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") Long id) throws Exception {
    TaskType taskType = taskTypeRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", id));

    taskTypeRepo.delete(taskType);
    return ResponseEntity.ok().build();
  }
}
