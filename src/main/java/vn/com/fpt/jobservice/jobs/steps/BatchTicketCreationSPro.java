package vn.com.fpt.jobservice.jobs.steps;

import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.model.request.KafkaMessageRequest;
import vn.com.fpt.jobservice.service.impl.KafkaProducer;

@Service
public class BatchTicketCreationSPro extends BaseJobStep {

    public BatchTicketCreationSPro(BaseJob baseJob) {
        super(baseJob);
    }

    private void validateContext(Task task) throws JobExecutionException {
        if (task.getTicketId() == null) {
            throw new JobExecutionException("Ticket ID is required to created batch ticket!");
        }
        if (task.getPhaseId() == null) {
            throw new JobExecutionException("Phase ID is required to created batch ticket!");
        }
        if (task.getSubProcessId() == null) {
            throw new JobExecutionException("Process ID is required to created batch ticket!");
        }
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final String messageType = "batch_ticket";
        final Task task = (Task) context.get("task");
        final KafkaProducer kafkaProducer = (KafkaProducer) context.get("kafkaProducer");
        validateContext(task);

        logger(String.format("Creating ticket for process [%s] with ticket [%s] and phase [%s]", task.getSubProcessId(), task.getTicketId(), task.getPhaseId()));

        KafkaMessageRequest message = KafkaMessageRequest.builder()
                .jobUUID(task.getJobUUID())
                .ticketId(task.getTicketId())
                .phaseId(task.getPhaseId())
                .tenantId(task.getTenantId())
                .subProcessId(task.getSubProcessId())
                .type(messageType)
                .build();

        logger(String.format("Sending kafka message for creating ticket with message: %s", new JSONObject(message)));

        kafkaProducer.sendKafkaMessage(message, task.getTenantId()).subscribe();

        /**
         final String uServiceURL = (String) context.get("uServiceURL");

         String createBatchURL = String.format("%s/batch/create?ticketId=%s&phaseId=%s&subProcessId=%s",
         uServiceURL,
         task.getTicketId(),
         task.getPhaseId(),
         task.getSubProcessId());
         HttpHeaders headers = new HttpHeaders();
         CallExternalAPI.exchangeGet(createBatchURL, headers, Object.class);
         */
    }
}
