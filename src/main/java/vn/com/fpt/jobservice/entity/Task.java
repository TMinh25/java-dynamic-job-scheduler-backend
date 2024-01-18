package vn.com.fpt.jobservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.service.TaskSchedulerService;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tasks")
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

    @Column(name = "name", columnDefinition="varchar(255) collate utf8mb4_unicode_ci")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "task_type_id", referencedColumnName = "id", nullable = false)
    private TaskType taskType;

    @ColumnDefault("'{}'")
    @Column(name = "task_input_data")
    private String taskInputData;

    @NotNull
    @Column(name = "ticket_id")
    private Long ticketId;

    @NotNull
    @Column(name = "phase_id")
    private Long phaseId;

    @Column(name = "phase_name")
    private String phaseName;

    @ColumnDefault("0")
    @Column(name = "retry_count")
    private Integer retryCount;

    @ColumnDefault("1")
    @Column(name = "max_retries")
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
            log.debug("Calculating next invocation", id);
            this.nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
            if (this.maxRetries == null) {
                this.maxRetries = 1;
            }
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    @PreUpdate
    public void taskUpdate() {
        try {
            log.debug("Calculating next invocation", id);
            this.nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
            if (this.maxRetries == null) {
                this.maxRetries = 1;
            }
        } catch (ParseException e) {
            log.error("Error calculating next execution time: ", e);
        }
    }

    public TaskModel toModel() {
        return TaskModel.builder().id(this.id).name(this.name).taskType(this.taskType).taskInputData(this.taskInputData)
                .ticketId(this.ticketId).phaseId(this.phaseId).retryCount(this.retryCount).maxRetries(this.maxRetries)
                .startStep(this.startStep).cronExpression(this.cronExpression).nextInvocation(this.nextInvocation)
                .prevInvocation(this.prevInvocation).status(this.status).active(this.active).jobUUID(this.jobUUID)
                .build();
    }

    public boolean canScheduleJob() {
        return this.active && (this.retryCount < this.maxRetries);
    }
}