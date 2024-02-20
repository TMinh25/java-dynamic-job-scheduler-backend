package vn.com.fpt.jobservice.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationIntegration;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationSPro;
import vn.com.fpt.jobservice.jobs.steps.ShowJobContext;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;

@Slf4j
public class BatchTicketCreation extends SystemJob {
    @Value("${u-service-api}")
    String uServiceURL;

    @Value("${integration-api}")
    String integrationURL;

    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Override
    protected void defineSteps() {
        this.steps.add(new ShowJobContext(this));

        if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {
            this.steps.add(new BatchTicketCreationIntegration(this));
        } else {
            this.steps.add(new BatchTicketCreationSPro(this));
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Declare data for job steps
        context.put("uServiceURL", uServiceURL);
        context.put("integrationURL", integrationURL);
        context.put("integrationServiceGrpc", integrationServiceGrpc);

        super.executeInternal(context);
    }
}
