package vn.com.fpt.jobservice.model;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.utils.AutomationTaskType;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskModel {
  @Autowired
  TaskTypeRepository taskTypeRepository;

  private String id;
  private String name;
  private TaskType taskType;
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
  private String jobUUID;

  public Task toEntity() {
    Task taskEntity = new Task();
    taskEntity.setId(this.getId());
    taskEntity.setName(this.getName());
    taskEntity.setTaskType(this.getTaskType());
    taskEntity.setTaskInputData(this.getTaskInputData());
    taskEntity.setTicketId(this.getTicketId());
    taskEntity.setPhaseId(this.getPhaseId());
    taskEntity.setRetryCount(this.getRetryCount());
    taskEntity.setMaxRetries(this.getMaxRetries());
    taskEntity.setStartStep(this.getStartStep());
    taskEntity.setCronExpression(this.getCronExpression());
    taskEntity.setNextInvocation(this.getNextInvocation());
    taskEntity.setPrevInvocation(this.getPrevInvocation());
    taskEntity.setStatus(this.getStatus());
    taskEntity.setActive(this.getActive());
    taskEntity.setJobUUID(this.getJobUUID());
    return taskEntity;
  }
}
