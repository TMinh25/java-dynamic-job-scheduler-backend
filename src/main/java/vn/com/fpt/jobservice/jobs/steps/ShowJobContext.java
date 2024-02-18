package vn.com.fpt.jobservice.jobs.steps;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;

public class ShowJobContext extends BaseJobStep {
    public ShowJobContext(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        logger(context.toString());
    }
}
