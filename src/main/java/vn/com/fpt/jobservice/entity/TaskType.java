package vn.com.fpt.jobservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import vn.com.fpt.jobservice.utils.AutomationTaskType;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "class_name")
    private AutomationTaskType className;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TaskTypeType type;

    public TaskType() {
    }

    public TaskType(String name, AutomationTaskType className) {
        this.name = name;
        this.className = className;
    }
}
