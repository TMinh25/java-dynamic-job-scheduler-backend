package vn.com.fpt.jobservice.service;

import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.TaskHistoryModel;

import java.util.List;

public interface TaskHistoryService {
    List<TaskHistoryModel> readAllHistoryOfTask(String taskId);

    TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history);

    TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history);
}
