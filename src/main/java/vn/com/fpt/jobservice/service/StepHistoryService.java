package vn.com.fpt.jobservice.service;

import vn.com.fpt.jobservice.entity.StepHistory;
import vn.com.fpt.jobservice.model.StepHistoryModel;

import java.util.List;

public interface StepHistoryService {
    List<StepHistoryModel> readAllStepOfTaskHistory(Long taskHistoryId);

    StepHistory insertNewStepOfTaskHistory(Long taskHistoryId, StepHistory history);

    StepHistory updateProcessingStepOfTaskHistory(Long taskHistoryId, StepHistory history);

    void deleteAllStepsOfTaskHistory(Long taskHistoryId);
}
