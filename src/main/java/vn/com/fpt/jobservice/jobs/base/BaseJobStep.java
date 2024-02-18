package vn.com.fpt.jobservice.jobs.base;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public abstract class BaseJobStep {
    private BaseJob baseJob;

    public BaseJobStep(BaseJob baseJob) {
        this.baseJob = baseJob;
    }

    public BaseJobStep() {
    }

    protected void logger(String msg) {
        if (baseJob != null) {
            baseJob.logger(msg);
        }
    }

    protected abstract void execute(JobExecutionContext context) throws JobExecutionException;
}
