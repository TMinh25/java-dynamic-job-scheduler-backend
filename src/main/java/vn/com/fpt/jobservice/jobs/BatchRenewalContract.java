package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.model.response.IntegrationResponse;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.utils.CallExternalAPI;

@Slf4j
public class BatchRenewalContract extends SystemJob {
    static String jobName = "Tạo hợp đồng lao động";

    @Value("${u-service-api}")
    String uServiceURL;

    @Value("${integration-api}")
    String integrationURL;

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);
        try {
            if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {
                // Getting data from integration
                Long integrationId = this.task.getIntegrationId();
                String getIntegrationURL = String.format("%s/get/%s", integrationURL, integrationId);
                HttpHeaders headers = new HttpHeaders();
                IntegrationResponse response = CallExternalAPI.exchangeGet(
                        getIntegrationURL,
                        headers,
                        IntegrationResponse.class);

                if (response != null) {
                    // Execute the integration thread
                    IntegrationStructure executionRequest = response.getResponseData().getStructure();
                    String executeIntegrationURL = String.format("%s/execute", integrationURL);
                    Object executionResponse = CallExternalAPI.exchangePost(
                            executeIntegrationURL,
                            headers,
                            executionRequest,
                            Object.class);
                    assert executionResponse != null;
                    jobInfo(executionResponse.toString());
                } else {
                    throw new JobExecutionException("Can not get data for integration!");
                }
            } else {
                String createBatchURL = String.format("%s/batch/create?ticketId=%s&phaseId=%s&subProcessId=%s",
                        uServiceURL,
                        this.task.getTicketId(),
                        this.task.getPhaseId(),
                        this.task.getTaskType().getProcessId());
                HttpHeaders headers = new HttpHeaders();
                CallExternalAPI.exchangeGet(createBatchURL, headers, Object.class);
            }
        } catch (Exception e) {
            throw new JobExecutionException(e.getMessage());
        }
    }
}
