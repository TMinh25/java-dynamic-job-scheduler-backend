package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import vn.com.fpt.jobservice.jobs.base.IntegrationJob;

public class TestIntegrationJob extends IntegrationJob {
  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    super.executeInternal(context);
  }
}
