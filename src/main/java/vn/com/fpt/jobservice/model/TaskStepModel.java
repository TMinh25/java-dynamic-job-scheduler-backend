package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.entity.TaskStep;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStepModel implements Comparable<TaskStepModel> {
    private Long id;
    private String name;
    private String className;
    private String description;
    private Long step;

    public TaskStep toEntity() {
        TaskStep entity = new TaskStep();
        entity.setId(this.getId());
        entity.setName(this.getName());
        entity.setClassName(this.getClassName());
        entity.setDescription(this.getDescription());
        return entity;
    }

    @Override
    public int compareTo(TaskStepModel otherStep) {
        return this.step.compareTo(otherStep.getStep());
    }
}
