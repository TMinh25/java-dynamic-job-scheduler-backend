package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.repositories.TaskHistoryRepository;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.service.StepHistoryService;
import vn.com.fpt.jobservice.service.TaskHistoryService;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.List;

@Service
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private TaskHistoryRepository taskHistoryRepo;

    @Autowired
    private StepHistoryService stepHistoryService;

    @Override
    public TaskHistoryModel findById(Long id) {
        TaskHistory history = taskHistoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("TaskHistory", "id", id));
        TaskHistoryModel historyModel = history.toModel();
        historyModel.setStepHistories(stepHistoryService.readAllStepOfTaskHistory(historyModel.getId()));
        return historyModel;
    }

    @Override
    public PagedResponse<TaskHistoryModel> readAll(Pageable pageable, String tenantId, String searchQuery) {
        log.debug("readAllHistoryOfTask - START");
        Page<TaskHistory> histories = taskHistoryRepo.searchByString(pageable, tenantId, searchQuery);
        Page<TaskHistoryModel> taskHistoryModelPage = histories.map(TaskHistory::toModel);
        log.debug("readAllHistoryOfTask - END");

        return new PagedResponse<>(taskHistoryModelPage);
    }

    @Override
    public PagedResponse<TaskHistoryModel> readAllHistoryOfTask(Pageable pageable, String taskId) {
        log.debug("readAllHistoryOfTask - START");
        Page<TaskHistory> histories = taskHistoryRepo.findByTaskIdOrderByStartedAtDesc(pageable, taskId);
        Page<TaskHistoryModel> historyModels = histories.map(TaskHistory::toModel);
        log.debug("readAllHistoryOfTask - END");
        return new PagedResponse<>(historyModels);
    }

    @Override
    @Transactional
    public TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history) {
        log.debug("insertNewHistoryOfTask - START");
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "taskId", taskId));
        history.setTenantId(task.getTenantId());
        history.setTask(task);
        log.debug("insertNewHistoryOfTask - END");
        return taskHistoryRepo.save(history);
    }

    @Override
    @Transactional
    public TaskHistory updateProcessingHistoryOfTask(String taskId, TaskHistory history) {
        log.debug("updateHistoryOfTask - START");
        TaskHistory taskHistory = taskHistoryRepo.findFirstByTaskIdAndStatus(taskId, TaskStatus.PROCESSING)
                .orElseThrow(() -> new ResourceNotFoundException("TaskHistories", "taskId", taskId));

        BeanUtils.copyProperties(history, taskHistory, Utils.getNullPropertyNames(history));
        if (taskHistory.getStatus() == TaskStatus.SUCCESS || taskHistory.getStatus() == TaskStatus.ERRORED) {
            taskHistory.calculateExecutionTime();
        }
        log.debug("updateHistoryOfTask - END");
        return taskHistoryRepo.save(taskHistory);
    }

    @Override
    public TaskHistory findLatestByTaskId(String taskId) {
        return taskHistoryRepo.findFirstByTaskIdOrderByStartedAtDesc(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskHistories", "taskId", taskId));
    }

    @Override
    @Transactional
    public TaskHistory updateLatestHistoryOfTask(String taskId, TaskHistory history) {
        log.debug("updateLatestHistoryOfTask - START");
        TaskHistory taskHistory = taskHistoryRepo.findFirstByTaskIdOrderByStartedAtDesc(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskHistories", "taskId", taskId));

        BeanUtils.copyProperties(history, taskHistory, Utils.getNullPropertyNames(history));
        if (taskHistory.getStatus() == TaskStatus.SUCCESS || taskHistory.getStatus() == TaskStatus.ERRORED) {
            taskHistory.calculateExecutionTime();
        }
        log.debug("updateLatestHistoryOfTask - END");
        return taskHistoryRepo.save(taskHistory);
    }

    @Override
    public void deleteAllHistoriesOfTask(String taskId) {
        log.debug("deleteAllHistoriesOfTask - START");
        List<TaskHistory> histories = taskHistoryRepo.findByTaskIdOrderByStartedAtDesc(taskId);
        for (TaskHistory history : histories) {
            stepHistoryService.deleteAllStepsOfTaskHistory(history.getId());
        }

        taskHistoryRepo.deleteAll(histories);
        log.debug("deleteAllHistoriesOfTask - END");
    }
}
