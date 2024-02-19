package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.entity.StepHistory;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.StepHistoryModel;
import vn.com.fpt.jobservice.repositories.StepHistoryRepository;
import vn.com.fpt.jobservice.repositories.TaskHistoryRepository;
import vn.com.fpt.jobservice.service.StepHistoryService;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class StepHistoryServiceImpl implements StepHistoryService {
    @Autowired
    private StepHistoryRepository stepHistoryRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Override
    public List<StepHistoryModel> readAllStepOfTaskHistory(Long taskHistoryId) {
        log.debug("readAllStepOfTaskHistory - START");
        List<StepHistory> histories = stepHistoryRepository.findByTaskHistoryIdOrderByStepDesc(taskHistoryId);

        List<StepHistoryModel> historyModels = new ArrayList<>();
        for (StepHistory history : histories) {
            StepHistoryModel historyModel = history.toModel();
            historyModels.add(historyModel);
        }

        log.debug("readAllStepOfTaskHistory - END");
        return historyModels;
    }

    @Override
    public StepHistory insertNewStepOfTaskHistory(Long taskHistoryId, StepHistory history) {
        log.debug("insertNewStepOfTaskHistory - START");
        TaskHistory taskHistory = taskHistoryRepository.findById(taskHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("TaskHistory", "id", taskHistoryId));
        history.setTaskHistory(taskHistory);
        history.setTask(taskHistory.getTask());
        history.setStartedAt(new Date());
        history.setStatus(TaskStatus.PROCESSING);
        log.debug("insertNewStepOfTaskHistory - END");
        return stepHistoryRepository.save(history);
    }

    @Override
    public StepHistory updateProcessingStepOfTaskHistory(Long taskHistoryId, StepHistory history) {
        log.debug("updateProcessingStepOfTaskHistory - START");
        StepHistory stepHistory = stepHistoryRepository.findFirstByTaskHistoryIdAndStatus(taskHistoryId, TaskStatus.PROCESSING)
                .orElseThrow(() -> new ResourceNotFoundException("StepHistory", "taskHistoryId", taskHistoryId));

        BeanUtils.copyProperties(history, stepHistory, Utils.getNullPropertyNames(history));
        if (stepHistory.getStatus() == TaskStatus.SUCCESS || stepHistory.getStatus() == TaskStatus.ERRORED) {
            stepHistory.setEndedAt(new Date());
            stepHistory.calculateExecutionTime();
        }
        log.debug("updateProcessingStepOfTaskHistory - END");
        return stepHistoryRepository.save(stepHistory);
    }

    @Override
    public void deleteAllStepsOfTaskHistory(Long taskHistoryId) {
        log.debug("deleteAllHistoriesOfTask - START");
        List<StepHistory> histories = stepHistoryRepository.findByTaskHistoryIdOrderByStepDesc(taskHistoryId);

        stepHistoryRepository.deleteAll(histories);
        log.debug("deleteAllHistoriesOfTask - END");
    }
}
