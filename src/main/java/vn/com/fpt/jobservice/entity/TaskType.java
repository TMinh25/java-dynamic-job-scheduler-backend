package vn.com.fpt.jobservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.utils.TaskTypeType;

@Entity
@Table(name = "task_types", uniqueConstraints = @UniqueConstraint(columnNames = "class_name"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, columnDefinition = "varchar(255) collate utf8mb4_unicode_ci")
    private String name;

    @Column(name = "class_name")
    private String className;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TaskTypeType type;

    @Column(name = "process_id")
    private Long processId;

    public TaskType() {
    }

    public TaskType(String name, String className, TaskTypeType type, Long processId) {
        this.name = name;
        this.className = className;
        this.type = type;
        this.processId = processId;
    }
}
