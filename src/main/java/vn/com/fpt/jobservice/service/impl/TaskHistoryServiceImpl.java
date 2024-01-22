package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.repositories.TaskHistoryRepository;
import vn.com.fpt.jobservice.repositories.TaskRepository;
import vn.com.fpt.jobservice.service.TaskHistoryService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TaskHistoryServiceImpl implements TaskHistoryService {
    @Autowired
    private TaskRepository taskRepo;
    @Autowired
    private TaskHistoryRepository taskHistoryRepo;

    @Override
    public List<TaskHistoryModel> readAllHistoryOfTask(String taskId) {
        log.debug("readAllHistoryOfTask - START");
        List<TaskHistory> histories = taskHistoryRepo.findByTaskIdOrderByStartedAtDesc(taskId);

        List<TaskHistoryModel> historyModels = new ArrayList<>();
        for (TaskHistory history : histories) {
            TaskHistoryModel historyModel = history.toModel();
            historyModels.add(historyModel);
        }

        log.debug("readAllHistoryOfTask - END");
        return historyModels;
    }

    @Override
    public TaskHistory insertNewHistoryOfTask(String taskId, TaskHistory history) {
        Task task = taskRepo.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "taskId", taskId));
        history.setTask(task);
        log.debug("insertNewHistoryOfTask - END");
        return taskHistoryRepo.save(history);
    }

    @Override
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
}
