package vn.com.fpt.jobservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.model.StepHistoryModel;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.Date;


@Entity
@Table(name = "step_histories")
@Data
@EntityListeners(AuditingEntityListener.class)
public class StepHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_history_id", nullable = false)
    private TaskHistory taskHistory;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "step")
    private Integer step;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "status")
    private TaskStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_at")
    private Date startedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ended_at")
    private Date endedAt;

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "execution_time")
    private Long executionTime;

    public StepHistory() {
    }

    public StepHistory(Long id, TaskHistory taskHistory, Task task, Integer step, String stepName, TaskStatus status, Date startedAt, Date endedAt, String errorMessage) {
        this.id = id;
        this.taskHistory = taskHistory;
        this.task = task;
        this.step = step;
        this.stepName = stepName;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.errorMessage = errorMessage;
        this.executionTime = Math
                .abs(this.endedAt.toInstant().getEpochSecond() - this.startedAt.toInstant().getEpochSecond());
    }

    public void calculateExecutionTime() {
        this.executionTime = Utils.calculateDateDifferenceInMillis(this.startedAt, this.endedAt);
    }

    public StepHistoryModel toModel() {
        return StepHistoryModel.builder()
//                .id(this.id)
                .taskHistoryId(this.taskHistory.getId())
                .taskId(this.task.getId())
                .step(this.getStep())
                .stepName(this.getStepName())
                .status(this.getStatus())
                .startedAt(this.getStartedAt())
                .endedAt(this.getEndedAt())
                .errorMessage(this.getErrorMessage())
                .executionTime(this.getExecutionTime())
                .build();
    }
}

