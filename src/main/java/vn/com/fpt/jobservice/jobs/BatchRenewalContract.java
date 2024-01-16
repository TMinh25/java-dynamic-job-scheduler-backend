package vn.com.fpt.jobservice.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.service.TaskService;

@Slf4j
public class BatchRenewalContract extends BaseJob {
  @Autowired
  TaskService taskService;

  @Override
  public void executeInternal(JobExecutionContext context) throws JobExecutionException {
    super.executeInternal(context);
    try {
      Thread.sleep(3000);
      log.info("executeInternal " + this.className);
    } catch (Exception e) {
    }
    throw new JobExecutionException("kajnsdkanskdj.job execution");
  }
}
