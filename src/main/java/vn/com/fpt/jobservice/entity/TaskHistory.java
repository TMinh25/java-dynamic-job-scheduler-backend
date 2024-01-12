package vn.com.fpt.jobservice.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Entity
@Table(name = "task_histories")
@Data
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = { "createdAt", "modifiedAt" }, allowGetters = true)
public class TaskHistory extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "task_id")
  private Task task;

  @Column(name = "step")
  private Long step;

  @Column(name = "error_message")
  private String errorMessage;

  @Column(name = "retry_count")
  private Integer retryCount;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private TaskStatus status;
}
