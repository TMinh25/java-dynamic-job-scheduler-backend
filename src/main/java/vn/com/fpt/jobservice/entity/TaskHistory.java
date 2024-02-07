package vn.com.fpt.jobservice.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Column(name = "logs", columnDefinition = "TEXT")
    private String logs;

    @Column(name = "execution_time")
    private Long executionTime;

//    @OneToMany(mappedBy = "taskHistory", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<StepHistory> stepHistories;


    public TaskHistory() {
    }

    public TaskHistory(Task task, Long step, TaskStatus status, Date startedAt,
            Date endedAt, String errorMessage) {
        this.task = task;
        this.step = step;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.errorMessage = errorMessage;
        this.executionTime = Math.abs(endedAt.toInstant().getEpochSecond() - startedAt.toInstant().getEpochSecond());
    }

    public void calculateExecutionTime() {
        this.executionTime = Utils.calculateDateDifferenceInMillis(this.startedAt, this.endedAt);
    }

    public TaskHistoryModel toModel() {
        return TaskHistoryModel.builder()
                .id(this.id)
                .taskId(this.task.getId())
                .step(this.getStep())
                .status(this.getStatus())
                .startedAt(this.getStartedAt())
                .endedAt(this.getEndedAt())
                .errorMessage(this.getErrorMessage())
                .executionTime(this.getExecutionTime())
                .build();
    }
}
