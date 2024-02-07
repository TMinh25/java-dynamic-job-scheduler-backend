package vn.com.fpt.jobservice.jobs.steps;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.jobs.base.BaseTaskStep;

@Slf4j
public class ShowJobContext extends BaseTaskStep {
    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("cccccccccccccccccccccccccccccccccccccccccccccccccccccccccc");
    }
}
