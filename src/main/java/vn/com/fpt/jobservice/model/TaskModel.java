package vn.com.fpt.jobservice.model;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import vn.com.fpt.jobservice.utils.enums.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.*;

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

    private String tenantId;

    public static TaskModel fromGrpc(TaskGrpc taskGrpc, TaskTypeRepository taskTypeRepo) {
        List<Object> taskInputData;
        if (!String.valueOf(taskGrpc.getTaskInputDataList()).equals("[]")) {
            taskInputData = Utils.convertRepeatedAny2List(taskGrpc.getTaskInputDataList());
        } else {
            taskInputData = new ArrayList<Object>();
        }
        Optional<TaskType> taskType = taskTypeRepo.findById(taskGrpc.getTaskTypeId());
        TaskType taskTypeEntity = null;
        if (taskType.isPresent()) {
            taskTypeEntity = taskType.get();
        }
        return TaskModel.builder()
                .id(taskGrpc.getId())
                .tenantId(taskGrpc.getTenantId())
                .name(taskGrpc.getName())
                .taskTypeId(taskGrpc.getTaskTypeId())
                .taskType(taskTypeEntity)
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

    public Task toEntity(TaskTypeRepository taskTypeRepo) throws JsonProcessingException {
        Task taskEntity = new Task();

        if (this.getId() != null) {
            taskEntity.setId(this.getId());
        } else {
            taskEntity.setId(UUID.randomUUID().toString());
        }
        taskEntity.setName(this.getName());
        if (this.getTaskTypeId() != null) {
            TaskType taskType = taskTypeRepo.findById(this.getTaskTypeId()).orElseThrow(() -> new ResourceNotFoundException("TaskType", "id", this.getTaskTypeId()));
            taskEntity.setTaskType(taskType);
        }
        if (this.getMaxRetries() != null) {
            taskEntity.setMaxRetries(this.getMaxRetries());
        }
        taskEntity.setTenantId(this.getTenantId());
        taskEntity.setTaskInputData(Utils.objectToString(this.getTaskInputData()));
        taskEntity.setTicketId(this.getTicketId());
        taskEntity.setPhaseId(this.getPhaseId());
        taskEntity.setPhaseName(this.getPhaseName());
        taskEntity.setIntegrationId(this.getIntegrationId());
        taskEntity.setIntegrationName(this.getIntegrationName());
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
