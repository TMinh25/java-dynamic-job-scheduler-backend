package vn.com.fpt.jobservice.service;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;

import vn.com.fpt.jobservice.jobs.BatchRenewalContract;

@Service
public class TaskSchedulerService {
  public static Date calculateNextExecutionTime(String cronExpression) throws ParseException {
    try {
      CronExpression cron = new CronExpression(cronExpression);
      Date now = new Date();
      return cron.getNextValidTimeAfter(now);
    } catch (Exception e) {
      throw new ParseException("cron expression is not valid!", 0);
    }
  }

  public void scheduleTask(String cronExpression, Runnable task) throws SchedulerException, ParseException {
    Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    JobDetail jobDetail = JobBuilder.newJob()
        .ofType(BatchRenewalContract.class)
        .withIdentity("job1", "group1")
        .build();

    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1")
        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
        .build();

    JobDataMap jobDataMap = new JobDataMap();
    jobDataMap.put("task", task);
    jobDetail.getJobDataMap().putAll(jobDataMap);

    scheduler.scheduleJob(jobDetail, trigger);
    scheduler.start();
  }
}
