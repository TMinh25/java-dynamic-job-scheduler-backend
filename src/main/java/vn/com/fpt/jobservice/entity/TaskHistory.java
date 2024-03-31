package vn.com.fpt.jobservice.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Lob
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "started_at")
    private Date startedAt;

    @Column(name = "ended_at")
    private Date endedAt;

    @Lob
    @Column(name = "logs", columnDefinition = "TEXT")
    private String logs;

    @Column(name = "execution_time")
    private Long executionTime;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

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

    @SneakyThrows
    public TaskHistoryModel toModel() {
        List<Map<String, Object>> logList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        if (this.logs != null) {
            logList = objectMapper.readValue(this.logs, new TypeReference<List<Map<String, Object>>>() {
            });
        }

        return TaskHistoryModel.builder()
                .id(this.id)
                .taskId(this.task.getId())
                .task(this.task.toModel())
                .step(this.getStep())
                .status(this.getStatus())
                .startedAt(this.getStartedAt())
                .endedAt(this.getEndedAt())
                .errorMessage(this.getErrorMessage())
                .executionTime(this.getExecutionTime())
                .logs(logList)
                .build();
    }
}
