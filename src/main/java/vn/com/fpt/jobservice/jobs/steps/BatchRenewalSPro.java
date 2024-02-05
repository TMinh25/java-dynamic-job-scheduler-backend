package vn.com.fpt.jobservice.jobs.steps;
import org.springframework.http.HttpHeaders;

import org.quartz.JobExecutionContext;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseTaskStep;
import vn.com.fpt.jobservice.utils.CallExternalAPI;

public class BatchRenewalSPro extends BaseTaskStep {
    @Override
    protected Object execute(JobExecutionContext context) {
        final String uServiceURL = (String) context.get("uServiceURL");
        final Task task = (Task) context.get("task");

        String createBatchURL = String.format("%s/batch/create?ticketId=%s&phaseId=%s&subProcessId=%s",
                uServiceURL,
                task.getTicketId(),
                task.getPhaseId(),
                task.getTaskType().getProcessId());
        HttpHeaders headers = new HttpHeaders();
        CallExternalAPI.exchangeGet(createBatchURL, headers, Object.class);

        return context;
    }
}
