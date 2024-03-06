package vn.com.fpt.jobservice.jobs.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
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
            TicketCreateModel ticketCreateRequest = TicketCreateModel.fromMap(keyMappedResult);
            logger("ticketCreateRequest: " + ticketCreateRequest.toString());
            HttpHeaders headers = new HttpHeaders();
            BatchResponseModel ticketCreateResponse = CallExternalAPI.exchangePost(
                    uServiceURL + "/batch/create-ticket",
                    headers,
                    ticketCreateRequest,
                    BatchResponseModel.class);

            logger("Ticket create result: " + ticketCreateResponse.toString());
        } catch (HttpClientErrorException e) {
            throw new JobExecutionException(e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}

