package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.service.TaskHistoryService;

import java.util.List;

@RestController
@RequestMapping("/task-histories")
public class TaskHistoryController {
    @Autowired
    TaskHistoryService taskHistoryService;

    @GetMapping("/{id}")
    public List<TaskHistoryModel> readAllTasks(@PathVariable(value = "id") String taskId) {
        return taskHistoryService.readAllHistoryOfTask(taskId);
    }

    @PostMapping("/{id}")
    public TaskHistory insertHistoryOfTask(@PathVariable(value = "id") String taskId, TaskHistory taskHistory) throws Exception {
        return taskHistoryService.insertNewHistoryOfTask(taskId, taskHistory);
    }
}
