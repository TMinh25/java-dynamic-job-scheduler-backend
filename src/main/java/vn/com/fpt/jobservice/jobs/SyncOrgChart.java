package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import vn.com.fpt.jobservice.jobs.base.SystemJob;

public class SyncOrgChart extends SystemJob {
    @Value("${u-service-api}")
    String uServiceURL;

    @Value("${integration-api}")
    String integrationURL;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        context.put("uServiceURL", uServiceURL);
        context.put("integrationURL", integrationURL);

    }
}
