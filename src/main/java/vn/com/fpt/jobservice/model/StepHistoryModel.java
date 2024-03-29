package vn.com.fpt.jobservice.model;

import lombok.Builder;
import lombok.Data;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;

import java.util.Date;

@Data
@Builder
public class StepHistoryModel {

//    private Long id;

    private Long taskHistoryId;

    private String taskId;

    private Integer step;

    private String stepName;

    private TaskStatus status;

    private Date startedAt;

    private Date endedAt;

    private Long executionTime;

    private String errorMessage;
}
