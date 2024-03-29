package vn.com.fpt.jobservice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.utils.Utils;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistoryModel {
    private Long id;
    private String taskId;
    private TaskModel task;
    private Long step;
    private String errorMessage;
    private TaskStatus status;
    private Long executionTime;
    private Date startedAt;
    private Date endedAt;

    private List<StepHistoryModel> stepHistories;
    private List<Map<String, Object>> logs;

    public TaskHistory toEntity() throws JsonProcessingException {
        TaskHistory newEntity = new TaskHistory();
        newEntity.setId(this.id);
//        newEntity.setTask(this.task.toEntity());
        newEntity.setStep(this.step);
        newEntity.setErrorMessage(this.errorMessage);
        newEntity.setStatus(this.status);
        newEntity.setExecutionTime(this.executionTime);
        newEntity.setStartedAt(this.startedAt);
        newEntity.setEndedAt(this.endedAt);
        newEntity.setLogs(Utils.objectToString(logs));
        return newEntity;
    }
}
