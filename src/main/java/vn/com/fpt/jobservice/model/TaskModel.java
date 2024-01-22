package vn.com.fpt.jobservice.model;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.InternalIntegration;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repositories.InternalIntegrationRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class TaskModel {
    private String id;
    private String name;
    private TaskType taskType;
    private Long taskTypeId;
    private Object[] taskInputData;
    private Long internalIntegrationId;
    private Long ticketId;
    private Long phaseId;
    private String phaseName;
    private Integer retryCount;
    private Integer maxRetries;
    private TaskStatus status;
    private Integer startStep;
    private String cronExpression;
    private Boolean active;
    private Date nextInvocation;
    private Date prevInvocation;
    private String jobUUID;

    private Date createdAt;
    private Date modifiedAt;
    private String createdBy;
    private String modifiedBy;

    public Task toEntity(TaskTypeRepository ttRepository, InternalIntegrationRepository iiRepository) {
        Task taskEntity = new Task();

        if (this.getId() != null) {
            taskEntity.setId(this.getId());
        } else {
            taskEntity.setId(UUID.randomUUID().toString());
        }
        taskEntity.setName(this.getName());
        if (taskTypeId != null) {
            TaskType taskType = ttRepository.findById(taskTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", taskTypeId));
            taskEntity.setTaskType(taskType);
        }
        if (internalIntegrationId != null) {
            InternalIntegration internalIntegration = iiRepository.findById(internalIntegrationId).orElseThrow(
                    () -> new ResourceNotFoundException("InternalIntegration", "id", internalIntegrationId));
            taskEntity.setInternalIntegration(internalIntegration);
        }
        try {
            if (this.getTaskInputData().length > 0) {
                String taskInputDataString = Arrays.toString(this.getTaskInputData());
                taskEntity.setTaskInputData(taskInputDataString);
            } else {
                taskEntity.setTaskInputData("[]");
            }
        } catch (Exception e) {
            log.error("Can not convert taskInputData to String: " + e.getMessage());
        }
        taskEntity.setTicketId(this.getTicketId());
        taskEntity.setPhaseId(this.getPhaseId());
        taskEntity.setPhaseName(this.getPhaseName());
        taskEntity.setRetryCount(this.getRetryCount());
        taskEntity.setMaxRetries(this.getMaxRetries());
        taskEntity.setStartStep(this.getStartStep());
        taskEntity.setCronExpression(this.getCronExpression());
        taskEntity.setNextInvocation(this.getNextInvocation());
        taskEntity.setPrevInvocation(this.getPrevInvocation());
        taskEntity.setStatus(this.getStatus());
        taskEntity.setActive(this.getActive());
        taskEntity.setJobUUID(this.getJobUUID());
        taskEntity.setCreatedAt(this.getCreatedAt());
        taskEntity.setModifiedAt(this.getModifiedAt());
        taskEntity.setCreatedBy(this.getCreatedBy());
        taskEntity.setModifiedBy(this.getModifiedBy());
        return taskEntity;
    }
}
