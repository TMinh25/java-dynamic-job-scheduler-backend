package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.utils.TaskTypeType;

import java.util.List;

@RestController
@RequestMapping("/task-types")
public class TaskTypeController {
    @Autowired
    TaskTypeRepository taskTypeRepo;

    @GetMapping()
    public List<TaskType> readAllTaskTypes(@RequestParam(value = "type", defaultValue = "MANUAL") TaskTypeType type) {
        return taskTypeRepo.findByType(type);
    }

    @GetMapping("/{id}")
    public TaskType readTaskTypeById(@PathVariable(value = "id") Long id) throws Exception {
        return taskTypeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", id));
    }
}
