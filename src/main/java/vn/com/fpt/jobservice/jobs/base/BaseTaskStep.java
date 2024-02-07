package vn.com.fpt.jobservice.jobs.base;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.logging.Logger;


public abstract class BaseTaskStep {
    protected abstract void execute(JobExecutionContext context) throws JobExecutionException;
}
