package vn.com.fpt.jobservice.jobs;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseJob extends QuartzJobBean implements InterruptableJob {
  protected volatile boolean toStopFlag = true;
  protected String className = this.getClass().getName();

  @Override
  protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    log.info(String.format("[%s] executing...", this.className));
    JobKey key = context.getJobDetail().getKey();
    log.info(
        "Job started with key:"
            + key.getName()
            + ", Group :" + key.getGroup()
            + ", Thread Name :"
            + Thread.currentThread().getName());
  };

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info(String.format("[%s] stopping thread... ", this.className));
    toStopFlag = false;
  }
}
