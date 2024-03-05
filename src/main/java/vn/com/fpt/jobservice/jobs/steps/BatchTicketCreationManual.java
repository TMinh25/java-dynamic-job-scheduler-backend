package vn.com.fpt.jobservice.jobs.steps;

import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
    public BatchTicketCreationManual(BaseJob baseJob) {
        super(baseJob);
    }

    private String removeHtmlTags(String html) {
        return html.replaceAll("\"\\\\<.*?\\\\>\"", "");
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final String uServiceURL = (String) context.get("uServiceURL");
        final Task task = (Task) context.get("task");
        final ExecuteIntegrationResult integrationResult = (ExecuteIntegrationResult) context.get("integrationResult");
        try {
            TicketCreateModel ticketCreateRequest = Utils.stringToObject(integrationResult.getResult(), TicketCreateModel.class);
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

