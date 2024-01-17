package vn.com.fpt.jobservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.service.TaskHistoryService;

@RestController
@RequestMapping("/task-histories")
public class TaskHistoryController {
	@Autowired
	TaskHistoryService taskHistoryService;

	@GetMapping("/{id}")
	public List<TaskHistory> readAllTasks(@PathVariable(value = "id") String taskId) {
		return taskHistoryService.readAllHistoryOfTask(taskId);
	}

	@PostMapping("/{id}")
	public TaskHistory insertHistoryOfTask(@PathVariable(value = "id") String taskId, TaskHistory taskHistory)
			throws Exception {
		return taskHistoryService.insertNewHistoryOfTask(taskId, taskHistory);
	}
}
