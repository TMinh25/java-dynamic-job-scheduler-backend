package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Pageable;

import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.PagedResponse;

public interface TaskHistoryService {
  PagedResponse<TaskHistory> readAllHistoryOfTask(Pageable pageable, String taskId);

  TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history) throws Exception;
}
