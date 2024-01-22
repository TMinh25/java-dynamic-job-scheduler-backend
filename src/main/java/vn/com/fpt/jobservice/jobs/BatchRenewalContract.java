package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.utils.CallExternalAPI;

@Slf4j
public class BatchRenewalContract extends SystemJob {

    @Value("${uservice-api}")
    String uServiceURL;

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);
        try {
            if (this.task.getIntegrationId() != null) {
                // TODO: implement integration
            } else {
                String createBatchURL = String.format("%s/batch/create?ticketId=%s&phaseId=%s&subProcessId=%s",
                        uServiceURL, this.task.getTicketId(), this.task.getPhaseId(), this.task.getTaskType().getProcessId());
                CallExternalAPI.exchangeGet(createBatchURL, null);
            }
        } catch (Exception e) {
            throw new JobExecutionException(e.getMessage());
        }
    }
}
