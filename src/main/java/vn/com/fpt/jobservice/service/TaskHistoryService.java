package vn.com.fpt.jobservice.service;

import java.util.List;

import vn.com.fpt.jobservice.entity.TaskHistory;

public interface TaskHistoryService {
  List<TaskHistory> readAllHistoryOfTask(String taskId);

  TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history);

  TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history);
}
