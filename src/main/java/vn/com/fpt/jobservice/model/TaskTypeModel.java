package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.entity.TaskType;
import vn.com.fpt.jobservice.utils.enums.TaskTypeType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskTypeModel {
    private Long id;
    private String name;
    private String className;
    private TaskTypeType type;

    public TaskType toEntity() {
        TaskType taskTypeEntity = new TaskType();
        taskTypeEntity.setId(this.getId());
        taskTypeEntity.setName(this.getName());
        taskTypeEntity.setClassName(this.getClassName());
        taskTypeEntity.setType(this.getType());
        return taskTypeEntity;
    }
}
