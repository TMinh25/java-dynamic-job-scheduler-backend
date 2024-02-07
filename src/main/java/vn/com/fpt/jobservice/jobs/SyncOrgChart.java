package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;
import vn.com.fpt.jobservice.jobs.base.SystemJob;

public class SyncOrgChart extends SystemJob {
    @Override
    protected void defineSteps() {
    }
}
