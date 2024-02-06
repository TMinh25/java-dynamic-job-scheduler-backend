package vn.com.fpt.jobservice.jobs.base;

import org.quartz.JobExecutionContext;

public abstract class BaseTaskStep {
    protected abstract Object execute(JobExecutionContext context);
}
