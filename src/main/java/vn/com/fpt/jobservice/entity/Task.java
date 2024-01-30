package vn.com.fpt.jobservice.entity;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.service.TaskSchedulerService;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Entity
@Table(name = "tasks", uniqueConstraints = @UniqueConstraint(name = "unique_phase_ticket", columnNames = { "ticket_id",
        "phase_id" }))
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "modifiedAt" }, allowGetters = true)
public class Task extends BaseEntity {
    /**
     *
     */
    private static final long serialVersionUID = -1490775644920845503L;

    @Id
    @UuidGenerator
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(255) collate utf8mb4_unicode_ci")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_type_id", referencedColumnName = "id", nullable = false)
    private TaskType taskType;

    @Column(name = "task_input_data", columnDefinition = "TEXT collate utf8mb4_unicode_ci")
    private String taskInputData;

    // Call external by making request to the integration service
    @Column(name = "integration_id")
    private Long integrationId;

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

    @PrePersist
    public void taskCreate() {
        this.active = true;
        this.jobUUID = String.format("%s_%s", taskType.getClassName(), UUID.randomUUID());
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
            this.modifiedAt = new Date();
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    public TaskModel toModel() {
        ObjectMapper objectMapper = new ObjectMapper();
        Object[] taskInputData;
//        Long internalIntegrationId = null;
        if (this.getTaskInputData() != null && !this.getTaskInputData().equals("[]")) {
            try {
                taskInputData = objectMapper.readValue(this.taskInputData, Object[].class);
            } catch (Exception e) {
                log.error("Can not convert taskInputData to Object[]: " + e.getMessage());
                taskInputData = new Object[0];
            }
        } else {
            taskInputData = new Object[0];
        }
        return TaskModel.builder().id(this.id)
                .name(this.name)
                .taskType(this.taskType)
                .taskInputData(taskInputData)
                .ticketId(this.ticketId)
                .phaseId(this.phaseId)
                .phaseName(phaseName)
                .integrationId(integrationId)
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

    public boolean canUpdateTask() {
        return this.status != TaskStatus.SUCCESS || this.maxRetries == null;
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