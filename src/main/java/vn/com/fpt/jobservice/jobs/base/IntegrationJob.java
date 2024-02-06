package vn.com.fpt.jobservice.jobs.base;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public abstract class IntegrationJob extends BaseJob {
    static String type = "INTEGRATION";

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);
    }
}
