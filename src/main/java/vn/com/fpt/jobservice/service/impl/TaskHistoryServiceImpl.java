package vn.com.fpt.jobservice.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repositories.TaskHistoryRepository;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.service.TaskHistoryService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

@Service
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
	@Autowired
	private TaskRepository taskRepo;
	@Autowired
	private TaskHistoryRepository taskHistoryRepo;

	@Override
	public List<TaskHistory> readAllHistoryOfTask(String taskId) {
		log.info("readAllHistoryOfTask - START");
		List<TaskHistory> histories = taskHistoryRepo.findByTaskId(taskId);
		log.info("readAllHistoryOfTask - END");
		return histories;
	}

	@Override
	public TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history) {
		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task", "taskId", taskId));
		history.setTask(task);
		log.info("insertNewHistoryOfTask - END");
		return taskHistoryRepo.save(history);
	}

	@Override
	public TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history) {
		log.info("updateHistoryOfTask - START");
		TaskHistory taskHistory = taskHistoryRepo.findFirstByTaskIdAndStatus(taskId, TaskStatus.PROCESSING)
				.orElseThrow(() -> new ResourceNotFoundException("TaskHistories", "taskId", taskId));

		BeanUtils.copyProperties(history, taskHistory, Utils.getNullPropertyNames(history));
		log.info("updateHistoryOfTask - END");
		return taskHistoryRepo.save(taskHistory);
	}
}
