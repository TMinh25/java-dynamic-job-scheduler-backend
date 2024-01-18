package vn.com.fpt.jobservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.Date;

@Entity
@Table(name = "task_histories")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TaskHistory {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

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

    public TaskHistory() {
    }

    public TaskHistory(Task task, Long step, String errorMessage, Integer retryCount, TaskStatus status, Date startedAt,
            Date endedAt) {
        this.task = task;
        this.step = step;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.executionTime = Math.abs(endedAt.toInstant().getEpochSecond() - startedAt.toInstant().getEpochSecond());
    }

    public void calculateExecutionTime() {
        System.out.println(this.startedAt);
        System.out.println(this.endedAt);
        this.executionTime = Math
                .abs(this.endedAt.toInstant().getEpochSecond() - this.startedAt.toInstant().getEpochSecond());
    }
}
