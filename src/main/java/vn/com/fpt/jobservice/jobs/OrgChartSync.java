package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.jobs.base.SystemJob;

public class OrgChartSync extends SystemJob {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);

        System.out.println("'knaskjdnkandjksna'");
    }
}
