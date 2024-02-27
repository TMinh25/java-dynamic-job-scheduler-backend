package vn.com.fpt.jobservice.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationIntegration;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationManual;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationSPro;
import vn.com.fpt.jobservice.jobs.steps.ShowJobContext;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.TaskTypeType;

@Slf4j
public class BatchTicketCreation extends BaseJob {
    @Value("${u-service-api}")
    String uServiceURL;

    @Value("${integration-api}")
    String integrationURL;

    @Override
    protected void defineSteps() {
        this.steps.add(new ShowJobContext(this));

        if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {
            this.steps.add(new BatchTicketCreationIntegration(this));
        } else {
            this.steps.add(new BatchTicketCreationSPro(this));
        }

        if (this.task.getTaskType().getType() == TaskTypeType.MANUAL) {
            this.steps.add(new BatchTicketCreationManual(this));
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Declare data for job steps
        context.put("uServiceURL", uServiceURL);
        context.put("integrationURL", integrationURL);

        super.executeInternal(context);
    }
}
