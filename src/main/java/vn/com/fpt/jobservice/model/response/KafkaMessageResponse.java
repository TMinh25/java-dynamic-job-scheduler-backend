package vn.com.fpt.jobservice.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.com.fpt.jobservice.utils.enums.TaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessageResponse {
    private String tenantId;
    private String jobUUID;
	private String type;
    private Long ticketId;
    private Long phaseId;
    private String cronExpression;
	private Long taskTypeId;
	private String phaseName;
	private Integer maxRetries;
	private List<Object> taskInputData;
	private Long subProcessId;
    private Boolean isUpdate;
    private Boolean active;
    private TaskStatus status;

    private String messageCode;
    private String message;
    private Object data;
}
