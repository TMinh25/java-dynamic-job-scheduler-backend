package vn.com.fpt.jobservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaMessageRequest {
    private String type;
    private String jobUUID;
    private String tenantId;
    private Long ticketId;
    private Long phaseId;
    private Long subProcessId;
    private Long processId;
    private TicketCreateModel request;
}