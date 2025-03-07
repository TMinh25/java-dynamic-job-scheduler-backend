package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.service.TaskHistoryService;

import java.util.List;

@RestController
@RequestMapping("/task-histories")
public class TaskHistoryController {
    @Autowired
    TaskHistoryService taskHistoryService;

    final String TENANT_ID_HEADER = "Tenant-Id";

    @GetMapping()
    public PagedResponse<TaskHistoryModel> readAll(@RequestHeader(TENANT_ID_HEADER) String tenantId, @RequestParam(value = "page", defaultValue = "1") int pageIndex, @RequestParam(value = "size", defaultValue = "20") int pageSize, @RequestParam(value = "search", defaultValue = "") String searchQuery) {
        return taskHistoryService.readAll(PageRequest.of(pageIndex - 1, pageSize), tenantId, searchQuery);
    }

    @GetMapping("/{id}")
    public TaskHistoryModel readById(@PathVariable(value = "id") Long id) {
        return taskHistoryService.findById(id);
    }

    @GetMapping("/task/{taskId}")
    public PagedResponse<TaskHistoryModel> readAllHistoryOfTask(@RequestParam(value = "page", defaultValue = "1") int pageIndex, @RequestParam(value = "size", defaultValue = "20") int pageSize, @PathVariable(value = "taskId") String taskId) {
        return taskHistoryService.readAllHistoryOfTask(PageRequest.of(pageIndex - 1, pageSize), taskId);
    }
}
