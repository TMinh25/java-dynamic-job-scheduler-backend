package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.steps.GetIntegrationResultGRPC;
import vn.com.fpt.jobservice.jobs.steps.RemapKeys;
import vn.com.fpt.jobservice.jobs.steps.ShowJobContext;
import vn.com.fpt.jobservice.jobs.steps.SyncDataOrganizationGRPC;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.service.impl.OrganizationServiceGrpc;

public class DataSynchronization extends BaseJob {
    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;
    @Autowired
    OrganizationServiceGrpc organizationServiceGrpc;

    @Override
    protected void defineSteps() throws JobExecutionException {
        this.addStep(new ShowJobContext(this));
        if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {
            this.addStep(new GetIntegrationResultGRPC(this));
            this.addStep(new RemapKeys(this, "integrationResult"));
            this.addStep(new SyncDataOrganizationGRPC(this));
        } else {
            throw new JobExecutionException("The field 'integrationId' is required for the job to run.");
        }
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.initializedData(context);

        // Declare data for job steps
        context.put("integrationServiceGrpc", integrationServiceGrpc);
        context.put("organizationServiceGrpc", organizationServiceGrpc);

        super.executeInternal(context);
    }
}
