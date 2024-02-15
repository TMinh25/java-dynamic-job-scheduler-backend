package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Pageable;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.List;
import java.util.Optional;

public interface TaskHistoryService {
    TaskHistoryModel findById(Long id);

    PagedResponse<TaskHistoryModel> readAll(Pageable pageable, String searchQuery);

    List<TaskHistoryModel> readAllHistoryOfTask(String taskId);

    TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history);

    TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history);

    void deleteAllHistoriesOfTask(String taskId);
}
