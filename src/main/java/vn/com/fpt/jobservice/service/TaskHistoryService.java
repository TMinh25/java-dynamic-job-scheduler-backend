package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Pageable;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskHistoryModel;

import java.util.List;

public interface TaskHistoryService {
    TaskHistoryModel findById(Long id);

    TaskHistory findLatestByTaskId(String taskId);

    PagedResponse<TaskHistoryModel> readAll(Pageable pageable, String tenantId, String searchQuery);

    PagedResponse<TaskHistoryModel> readAllHistoryOfTask(Pageable pageable, String taskId);

    TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history);

    TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history);

    TaskHistory updateLatestHistoryOfTask(String taskId, TaskHistory history);

    void deleteAllHistoriesOfTask(String taskId);
}
