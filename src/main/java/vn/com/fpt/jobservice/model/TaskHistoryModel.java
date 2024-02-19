package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.utils.TaskStatus;

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
    private List<Map<String, String>> logs;
}
