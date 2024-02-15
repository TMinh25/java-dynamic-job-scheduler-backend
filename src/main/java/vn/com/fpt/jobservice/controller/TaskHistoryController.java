package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.service.TaskHistoryService;

import java.util.List;

@RestController
@RequestMapping("/task-histories")
public class TaskHistoryController {
    @Autowired
    TaskHistoryService taskHistoryService;

    @GetMapping()
    public PagedResponse<TaskHistoryModel> readAll(@RequestParam(value = "page", defaultValue = "0") int pageIndex, @RequestParam(value = "size", defaultValue = "20") int pageSize, @RequestParam(value = "search", defaultValue = "") String searchQuery) {
        return taskHistoryService.readAll(PageRequest.of(pageIndex, pageSize), searchQuery);
    }

    @GetMapping("/{id}")
    public TaskHistoryModel readById(@PathVariable(value = "id") Long id) {
        return taskHistoryService.findById(id);
    }

    @GetMapping("/task/{taskId}")
    public List<TaskHistoryModel> readAllHistoryOfTask(@PathVariable(value = "taskId") String taskId) {
        return taskHistoryService.readAllHistoryOfTask(taskId);
    }
}
