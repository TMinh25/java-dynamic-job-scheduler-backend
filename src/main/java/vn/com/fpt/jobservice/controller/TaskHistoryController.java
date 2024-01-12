package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.service.TaskHistoryService;

@RestController
@RequestMapping("/task-histories")
public class TaskHistoryController {
  @Autowired
  TaskHistoryService taskHistoryService;

  @GetMapping("/{id}")
  public PagedResponse<TaskHistory> readAllTasks(@PathVariable(value = "id") String taskId, Pageable pageable) {
    return taskHistoryService.readAllHistoryOfTask(pageable, taskId);
  }

  @PostMapping("/{id}")
  public TaskHistory insertHistoryOfTask(@PathVariable(value = "id") String taskId, TaskHistory taskHistory) throws Exception {
    return taskHistoryService.insertNewHistoryOfTask(taskId, taskHistory);
  }
}
