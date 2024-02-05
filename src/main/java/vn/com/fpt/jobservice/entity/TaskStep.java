package vn.com.fpt.jobservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "task_steps", uniqueConstraints = @UniqueConstraint(columnNames = "class_name"))
@Data
@EntityListeners(AuditingEntityListener.class)
public class TaskStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, columnDefinition = "varchar(255) collate utf8mb4_unicode_ci")
    private String name;

    @Column(name = "class_name")
    private String className;

    @Column(name = "description")
    private String description;

//    @ManyToOne
//    @JoinTable(name = "task_type_steps",
//            joinColumns = @JoinColumn(name = "task_type_id"),
//            inverseJoinColumns = @JoinColumn(name = "step_id"),
//            uniqueConstraints = @UniqueConstraint(
//                    name = "unique_step",
//                    columnNames = {"task_type_id", "step_id", "step"}))
//    private TaskType taskType;

    public TaskStep() {
    }

    public TaskStep(String name, String className, String description) {
        this.name = name;
        this.className = className;
        this.description = description;
    }
}