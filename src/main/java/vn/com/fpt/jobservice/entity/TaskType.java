package vn.com.fpt.jobservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "task_types", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "modifiedAt"}, allowGetters = true)
public class TaskType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    public TaskType() {
    }

    public TaskType(String name) {
        this.name = name;
    }
}
