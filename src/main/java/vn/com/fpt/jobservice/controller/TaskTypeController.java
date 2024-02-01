package vn.com.fpt.jobservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskTypeModel;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;

@RestController
@RequestMapping("/task-types")
public class TaskTypeController {
    @Autowired
    TaskTypeRepository taskTypeRepo;

    @GetMapping()
    public PagedResponse<TaskType> readAllTasks(@RequestParam(value = "page", defaultValue = "0") int pageIndex, @RequestParam(value = "size", defaultValue = "10") int pageSize) {
        return new PagedResponse<>(taskTypeRepo.findAll(PageRequest.of(pageIndex, pageSize)));
    }

    @GetMapping("/{id}")
    public TaskType readTaskById(@PathVariable(value = "id") Long id) throws Exception {
        return taskTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public TaskType createTaskType(@Validated @RequestBody TaskTypeModel taskModel) throws Exception {
        TaskType taskType = taskModel.toEntity();
        return taskTypeRepo.save(taskType);
    }

    @PutMapping("/{id}")
    public TaskType updateTask(@PathVariable(value = "id") Long taskTypeId, @Valid @RequestBody TaskTypeModel taskTypeDetails) throws Exception {
        TaskType task = taskTypeRepo.findById(taskTypeId).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", taskTypeId));

        return taskTypeRepo.save(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") Long id) throws Exception {
        TaskType taskType = taskTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", id));

        taskTypeRepo.delete(taskType);
        return ResponseEntity.ok().build();
    }
}
