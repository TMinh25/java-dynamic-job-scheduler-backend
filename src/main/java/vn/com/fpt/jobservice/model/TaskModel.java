package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskModel {
    private String id;
    private String name;
    private TaskType taskType;
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
    private String jobUUID;

    public Task toEntity(TaskTypeRepository taskTypeRepository) {
        Task taskEntity = new Task();
        if (this.getId() != null) {
            taskEntity.setId(this.getId());
        } else {
            taskEntity.setId(UUID.randomUUID().toString());
        }
        taskEntity.setName(this.getName());
        if (taskTypeId != null) {
            TaskType taskType = taskTypeRepository.findById(taskTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", taskTypeId));
            taskEntity.setTaskType(taskType);
        }
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
