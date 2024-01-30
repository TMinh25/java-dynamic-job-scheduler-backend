package vn.com.fpt.jobservice.entity;

import java.util.Date;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Entity
@Table(name = "task_histories")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "step")
    private Long step;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Column(name = "execution_time")
    private Long executionTime;

    @Column(name = "old_data", columnDefinition = "TEXT collate utf8mb4_unicode_ci")
    private String oldData;

    @Column(name = "new_data", columnDefinition = "TEXT collate utf8mb4_unicode_ci")
    private String newData;

    public TaskHistory() {
    }

    public TaskHistory(Task task, Long step, String errorMessage, Integer retryCount, TaskStatus status, Date startedAt,
            Date endedAt, String oldData, String newData) {
        this.task = task;
        this.step = step;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.oldData = oldData;
        this.newData = newData;
        this.executionTime = Math.abs(endedAt.toInstant().getEpochSecond() - startedAt.toInstant().getEpochSecond());
    }

    public void calculateExecutionTime() {
        this.executionTime = Math
                .abs(this.endedAt.toInstant().getEpochSecond() - this.startedAt.toInstant().getEpochSecond());
    }

    public TaskHistoryModel toModel() {
        // ObjectMapper objectMapper = new ObjectMapper();
        // Map<String, Object> oldData;
        // Map<String, Object> newData;
        // try {
        //     oldData = objectMapper.readValue(this.oldData, new TypeReference<Map<String, Object>>() {});
        //     newData = objectMapper.readValue(this.newData, new TypeReference<Map<String, Object>>() {});
        // } catch (Exception e) {
        //     log.error("Can not convert oldData or newData to Map<String, Object> " + e.getMessage());
        //     oldData = new HashMap<String, Object>();
        //     newData = new HashMap<String, Object>();
        // }

        return TaskHistoryModel.builder()
                .id(this.id)
                .taskId(this.task.getId())
                .step(this.getStep())
                .errorMessage(this.getErrorMessage())
                .retryCount(this.getRetryCount())
                .status(this.getStatus())
                .startedAt(this.getStartedAt())
                .endedAt(this.getEndedAt())
                .executionTime(this.getExecutionTime())
                // .oldData(oldData)
                // .newData(newData)
                .build();
    }

}
