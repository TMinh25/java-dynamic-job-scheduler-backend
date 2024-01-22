package vn.com.fpt.jobservice.model;

import java.util.Date;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskHistoryModel {
    private Long id;
    private String taskId;
    private Long step;
    private String errorMessage;
    private Integer retryCount;
    private TaskStatus status;
    private Long executionTime;
    private Date startedAt;
    private Date endedAt;

    private Map<String, Object> oldData;
    private Map<String, Object> newData;
}
