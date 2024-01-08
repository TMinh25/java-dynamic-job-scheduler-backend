package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskModel {
	private String id;
	private String name;
	private String cronExpression;
	private String apiConfig;
	private String dataConfig;
}
