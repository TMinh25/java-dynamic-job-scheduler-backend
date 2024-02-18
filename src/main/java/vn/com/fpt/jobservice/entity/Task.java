package vn.com.fpt.jobservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.service.TaskSchedulerService;
import vn.com.fpt.jobservice.task_service.grpc.TaskGrpc;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.text.ParseException;
import java.util.*;

@Entity
@Table(name = "tasks", uniqueConstraints = @UniqueConstraint(name = "unique_phase_ticket", columnNames = {"ticket_id",
        "phase_id"}))
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "modifiedAt"}, allowGetters = true)
public class Task extends BaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = -1490775644920845503L;

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(255) collate utf8mb4_unicode_ci") // unique = true
    private String name;

    @ManyToOne
    @JoinColumn(name = "task_type_id", referencedColumnName = "id")
    private TaskType taskType;

    @Column(name = "task_input_data", columnDefinition = "TEXT collate utf8mb4_unicode_ci")
    private String taskInputData;

    @Column(name = "integration_id")
    private Long integrationId;

    @Column(name = "integration_name")
    private String integrationName;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "phase_id")
    private Long phaseId;

    @Column(name = "phase_name", columnDefinition = "VARCHAR(255) collate utf8mb4_unicode_ci")
    private String phaseName;

    @Column(name = "subprocess_id")
    private Long subProcessId;

    @ColumnDefault("0")
    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries", nullable = true)
    private Integer maxRetries;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'PENDING'")
    @Column(name = "status")
    private TaskStatus status;

    @ColumnDefault("0")
    @Column(name = "start_step")
    private Integer startStep;

    @Column(name = "cron_expression")
    private String cronExpression;

    @ColumnDefault("b'1'") // true
    @Column(name = "active")
    private Boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "next_invocation")
    private Date nextInvocation;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "prev_invocation")
    private Date prevInvocation;

    @Column(name = "job_uuid", unique = true)
    private String jobUUID;

    @Column(name = "synchronize_data")
    private String synchronizeData;

    @PrePersist
    public void taskCreate() {
        this.active = true;
        if (this.taskType != null) {
            this.jobUUID = String.format("%s_%s", this.taskType.getClassName(), UUID.randomUUID());
        }
        if (this.status == null) {
            this.status = TaskStatus.PENDING;
        }
        try {
            log.debug("Calculating next invocation: " + id);
            if (this.canScheduleJob()) {
                this.nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
            }
            this.createdAt = new Date();
            this.modifiedAt = new Date();
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    @PreUpdate
    public void taskUpdate() {
        try {
            log.debug("Calculating next invocation: " + id);
            if (this.canScheduleJob()) {
                this.setNextInvocation(TaskSchedulerService.calculateNextExecutionTime(cronExpression));
            }
            if (this.getStatus() == TaskStatus.CANCELED) {
                this.active = false;
            }
            this.modifiedAt = new Date();
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    public void generateNextInvocation() {
        try {
            this.setNextInvocation(TaskSchedulerService.calculateNextExecutionTime(this.cronExpression));
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    public TaskModel toModel() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> taskInputData;
        if (this.getTaskInputData() != null && !this.getTaskInputData().equals("[]")) {
            try {
                taskInputData = objectMapper.readValue(this.taskInputData, new TypeReference<List<Object>>() {
                });
            } catch (Exception e) {
                log.error("Can not convert taskInputData to Object[]: " + e.getMessage());
                taskInputData = new ArrayList<Object>();
            }
        } else {
            taskInputData = new ArrayList<Object>();
        }
        return TaskModel.builder().id(this.id)
                .name(this.name)
                .taskType(this.taskType)
                .taskInputData(taskInputData)
                .ticketId(this.ticketId)
                .phaseId(this.phaseId)
                .phaseName(phaseName)
                .integrationId(integrationId)
                .integrationName(integrationName)
                .subProcessId(this.subProcessId)
                .retryCount(this.retryCount)
                .maxRetries(this.maxRetries)
                .startStep(this.startStep)
                .cronExpression(this.cronExpression)
                .nextInvocation(this.nextInvocation)
                .prevInvocation(this.prevInvocation)
                .status(this.status)
                .active(this.active)
                .jobUUID(this.jobUUID)
                .createdAt(this.createdAt)
                .modifiedAt(this.modifiedAt)
                .createdBy(this.createdBy)
                .modifiedBy(this.modifiedBy)
                .build();
    }

    public TaskGrpc toGrpc() {
        return TaskGrpc.newBuilder()
                .setId(this.getId())
                .setTicketId(this.getTicketId())
                .setPhaseId(this.getPhaseId())
                .setPhaseName(this.getPhaseName())
                .setIntegrationId(this.getIntegrationId())
                .setIntegrationName(this.getIntegrationName())
                .setSubProcessId(this.getSubProcessId())
                .setRetryCount(this.getRetryCount())
                .setStartStep(this.getStartStep())
                .setCronExpression(this.getCronExpression())
                .setNextInvocation(Utils.convertDate2ProtocTimestamp(this.getNextInvocation()))
                .setPrevInvocation(Utils.convertDate2ProtocTimestamp(this.getPrevInvocation()))
                .setStatus(String.valueOf(this.getStatus()))
                .setActive(this.getActive())
                .setJobUUID(this.getJobUUID())
                .setCreatedAt(Utils.convertDate2ProtocTimestamp(this.getCreatedAt()))
                .setModifiedAt(Utils.convertDate2ProtocTimestamp(this.getModifiedAt()))
                .setCreatedBy(this.getCreatedBy())
                .setModifiedBy(this.getModifiedBy())
                .build();
    }

    public boolean canUpdateTask() {
        List<TaskStatus> cannotUpdateStatus = Arrays.asList(TaskStatus.SUCCESS, TaskStatus.PROCESSING, TaskStatus.CANCELED);
        if (this.status != TaskStatus.PROCESSING && this.maxRetries == null) {
            return true;
        } else return !cannotUpdateStatus.contains(this.status);
    }

    public boolean canScheduleJob() {
        if (!this.active) {
            return false;
        }

        if (this.maxRetries == null) {
            return true;
        }

        if (this.status == TaskStatus.SUCCESS) {
            return false;
        }

        return this.retryCount < this.maxRetries;
    }
}