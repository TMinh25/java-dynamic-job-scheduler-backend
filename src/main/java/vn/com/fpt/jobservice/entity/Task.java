package vn.com.fpt.jobservice.entity;

import java.text.ParseException;
import java.util.Date;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.service.TaskSchedulerService;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Entity
@Table(name = "tasks")
// @SecondaryTable(name = "task_types", pkJoinColumns =
// @PrimaryKeyJoinColumn(name = "task_type_id"))
// @SecondaryTable(name = "tickets", pkJoinColumns = @PrimaryKeyJoinColumn(name
// = "ticket_id"))
// @SecondaryTable(name = "phases", pkJoinColumns = @PrimaryKeyJoinColumn(name =
// "phase_id"))
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "modifiedAt" }, allowGetters = true)
public class Task extends BaseEntity {

  @Id
  @UuidGenerator
  private String id;

  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "task_type_id")
  private Long taskTypeId;

  @ColumnDefault("'{}'")
  @Column(name = "task_input_data")
  private String taskInputData;

  @NotNull
  @Column(name = "ticket_id")
  private Long ticketId;

  @NotNull
  @Column(name = "phase_id")
  private Long phaseId;

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

  @PrePersist
  public void taskCreate() {
    try {
      log.info("Calculating next avocation", id);
      this.nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
      this.active = true;
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
      log.info("Calculating next avocation", id);
      this.nextInvocation = TaskSchedulerService.calculateNextExecutionTime(cronExpression);
      if (this.maxRetries == null) {
        this.maxRetries = 1;
      }
    } catch (ParseException e) {
      log.error("Error calculating next execution time: ", e);
    }
  }
}