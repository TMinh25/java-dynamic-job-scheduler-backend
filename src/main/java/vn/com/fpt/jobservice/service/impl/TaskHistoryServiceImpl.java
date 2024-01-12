package vn.com.fpt.jobservice.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.repository.TaskHistoryRepository;
import vn.com.fpt.jobservice.repository.TaskRepository;
import vn.com.fpt.jobservice.service.TaskHistoryService;

@Service
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
  @Autowired
  private TaskRepository tasks;
  @Autowired
  private TaskHistoryRepository taskHistories;

  @Override
  public PagedResponse<TaskHistory> readAllHistoryOfTask(Pageable pageable, String taskId) {
    log.debug("readAllHistoryOfTask - START");
    Page<TaskHistory> histories = taskHistories.findByTaskId(pageable, taskId);
    log.debug("readAllHistoryOfTask - END");
    return new PagedResponse<>(histories);
  }

  @Override
  public TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history) {
    log.debug("insertNewHistoryOfTask - START");
    Optional<Task> task = tasks.findById(taskId);
    if (taskId == null || taskId.isEmpty() || !task.isPresent()) {
      throw new ResourceNotFoundException("Task", "taskId", taskId);
    }
    history.setTask(task.get());
    TaskHistory taskHistory = taskHistories.save(history);
    log.debug("insertNewHistoryOfTask - END");
    return taskHistory;
  }
}
