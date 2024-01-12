package vn.com.fpt.jobservice.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskModel {
  private String id;
  private String name;
  private Long taskTypeId;
  private String taskInputData;
  private Long ticketId;
  private Long phaseId;
  private Integer retryCount;
  private Integer maxRetries;
  private TaskStatus status;
  private Integer startStep;
  private String cronExpression;
  private Boolean active;
  private Date nextInvocation;
  private Date prevInvocation;

  public Task toEntity() {
    Task taskEntity = new Task();
    taskEntity.setId(this.getId());
    taskEntity.setTaskTypeId(this.getTaskTypeId());
    taskEntity.setTaskInputData(this.getTaskInputData());
    taskEntity.setTicketId(this.getTicketId());
    taskEntity.setPhaseId(this.getPhaseId());
    taskEntity.setRetryCount(this.getRetryCount());
    taskEntity.setMaxRetries(this.getMaxRetries());
    taskEntity.setStatus(this.getStatus());
    taskEntity.setStartStep(this.getStartStep());
    taskEntity.setName(this.getName());
    taskEntity.setCronExpression(this.getCronExpression());
    return taskEntity;
  }
}
