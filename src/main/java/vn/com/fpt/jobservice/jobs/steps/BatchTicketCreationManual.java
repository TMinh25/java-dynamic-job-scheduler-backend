package vn.com.fpt.jobservice.jobs.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.model.request.TicketCreateModel;
import vn.com.fpt.jobservice.model.response.BatchResponseModel;
import vn.com.fpt.jobservice.utils.CallExternalAPI;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.Map;
import java.util.Objects;

public class BatchTicketCreationManual extends BaseJobStep {
    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    public BatchTicketCreationManual(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final String uServiceURL = (String) context.get("uServiceURL");
        final Task task = (Task) context.get("task");
        final Map<String, Object> keyMappedResult = (Map<String, Object>) context.get("keyMappedResult");
        try {
            if (task.getSubProcessId() == null) {
                throw new JobExecutionException("Process ID is required to created ticket!");
            }

            TicketCreateModel ticketCreateRequest = TicketCreateModel.fromMap(keyMappedResult);
            logger("ticketCreateRequest: " + new JSONObject(ticketCreateRequest));

            logger(String.format("Creating ticket for process: %s", task.getSubProcessId()));
            logger("- url   : " + uServiceURL + "/batch/create-ticket?processId=" + task.getSubProcessId());
            logger("- method: POST");

            HttpHeaders headers = new HttpHeaders();
            BatchResponseModel ticketCreateResponse = CallExternalAPI.exchangePost(
                    uServiceURL + "/batch/create-ticket?processId=" + task.getSubProcessId(),
                    headers,
                    ticketCreateRequest,
                    BatchResponseModel.class);

            logger("Ticket create result: " + ticketCreateResponse.toString());

            if (!Objects.equals(ticketCreateResponse.getMessageCode(), "200")) {
                throw new Exception(ticketCreateResponse.getMessage());
            }
        } catch (HttpClientErrorException e) {
            throw new JobExecutionException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}

