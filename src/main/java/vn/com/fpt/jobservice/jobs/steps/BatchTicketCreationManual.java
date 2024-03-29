package vn.com.fpt.jobservice.jobs.steps;

import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.model.request.KafkaMessageRequest;
import vn.com.fpt.jobservice.model.request.TicketCreateModel;
import vn.com.fpt.jobservice.service.impl.KafkaProducer;

import java.util.Map;

public class BatchTicketCreationManual extends BaseJobStep {

    public BatchTicketCreationManual(BaseJob baseJob) {
        super(baseJob);
    }

    private void validateContext(Task task) throws JobExecutionException {
        if (task.getSubProcessId() == null) {
            throw new JobExecutionException("Process ID is required to created ticket!");
        }
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final String kafkaMessageType = "ticket";
        final Task task = (Task) context.get("task");
        final KafkaProducer kafkaProducer = (KafkaProducer) context.get("kafkaProducer");
        final Map<String, Object> keyMappedResult = (Map<String, Object>) context.get("keyMappedResult");
        validateContext(task);
        TicketCreateModel ticketCreateRequest = TicketCreateModel.fromMap(keyMappedResult);

        logger("ticketCreateRequest: " + new JSONObject(ticketCreateRequest));
        logger(String.format("Creating ticket for process: %s", task.getSubProcessId()));

        KafkaMessageRequest message = KafkaMessageRequest.builder()
                .jobUUID(task.getJobUUID())
                .processId(task.getSubProcessId())
                .request(ticketCreateRequest)
                .type(kafkaMessageType)
                .build();

        logger(String.format("Sending kafka message for creating ticket with message: %s", new JSONObject(message)));

        kafkaProducer.sendKafkaMessage(message, task.getTenantId()).subscribe();


//		final String uServiceURL = (String) context.get("uServiceURL");
//        logger("- url   : " + uServiceURL + "/batch/create-ticket?processId=" + task.getSubProcessId());
//        logger("- method: POST");
//
//        HttpHeaders headers = new HttpHeaders();
//        BatchResponseModel ticketCreateResponse = CallExternalAPI.exchangePost(
//                uServiceURL + "/batch/create-ticket?processId=" + task.getSubProcessId(), headers,
//                ticketCreateRequest, BatchResponseModel.class);
//
//        logger("Ticket create result: " + ticketCreateResponse.toString());
//
//        if (!Objects.equals(ticketCreateResponse.getMessageCode(), "200")) {
//            throw new Exception(ticketCreateResponse.getMessage());
//        }
    }
}
