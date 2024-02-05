package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.task_service.grpc.TaskGrpc;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    private List<Object> taskInputData;
    private Long integrationId;
    private String integrationName;
    private Long ticketId;
    private Long phaseId;
    private String phaseName;
    private Long subProcessId;
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

    public static TaskModel fromGrpc(TaskGrpc taskGrpc) {
        List<Object> taskInputData;
        if (!String.valueOf(taskGrpc.getTaskInputDataList()).equals("[]")) {
            taskInputData = Utils.convertRepeatedAny2List(taskGrpc.getTaskInputDataList());
        } else {
            taskInputData = new ArrayList<Object>();
        }
        return TaskModel.builder()
                .id(taskGrpc.getId())
                .name(taskGrpc.getName())
                .taskTypeId(taskGrpc.getTaskTypeId())
                .taskTypeId(taskGrpc.getTaskTypeId())
                .taskInputData(taskInputData)
                .integrationId(taskGrpc.getIntegrationId())
                .integrationName(taskGrpc.getIntegrationName())
                .ticketId(taskGrpc.getTicketId())
                .phaseId(taskGrpc.getPhaseId())
                .phaseName(taskGrpc.getPhaseName())
                .subProcessId(taskGrpc.getSubProcessId())
                .retryCount(taskGrpc.getRetryCount())
                .maxRetries(taskGrpc.getMaxRetries() == 0 ? null : taskGrpc.getMaxRetries())
                .status(TaskStatus.fromString(taskGrpc.getStatus()))
                .startStep(taskGrpc.getStartStep())
                .cronExpression(taskGrpc.getCronExpression())
                .active(taskGrpc.getActive())
                .nextInvocation(Utils.convertProtocTimestamp2Date(taskGrpc.getNextInvocation()))
                .prevInvocation(Utils.convertProtocTimestamp2Date(taskGrpc.getPrevInvocation()))
                .jobUUID(taskGrpc.getJobUUID())
                .createdAt(Utils.convertProtocTimestamp2Date(taskGrpc.getCreatedAt()))
                .modifiedAt(Utils.convertProtocTimestamp2Date(taskGrpc.getModifiedAt()))
                .createdBy(taskGrpc.getCreatedBy())
                .modifiedBy(taskGrpc.getModifiedBy())
                .build();
    }

    public Task toEntity(TaskTypeRepository ttRepository) {
        Task taskEntity = new Task();

        if (this.getId() != null) {
            taskEntity.setId(this.getId());
        } else {
            taskEntity.setId(UUID.randomUUID().toString());
        }
        taskEntity.setName(this.getName());
        if (this.getTaskTypeId() != null) {
            TaskType taskType = ttRepository.findById(this.getTaskTypeId()).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", this.getTaskTypeId()));
            taskEntity.setTaskType(taskType);
        }
        if (this.getMaxRetries() != null) {
            taskEntity.setMaxRetries(this.getMaxRetries());
        }
        taskEntity.setTicketId(this.getTicketId());
        taskEntity.setPhaseId(this.getPhaseId());
        taskEntity.setPhaseName(this.getPhaseName());
        taskEntity.setIntegrationId(this.getIntegrationId());
        taskEntity.setSubProcessId(this.getSubProcessId());
        taskEntity.setRetryCount(this.getRetryCount());
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
