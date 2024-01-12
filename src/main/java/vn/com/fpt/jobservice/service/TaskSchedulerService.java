package vn.com.fpt.jobservice.service;

import java.text.ParseException;
import java.util.Date;

import org.quartz.CronExpression;
import org.springframework.stereotype.Service;

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

  // public void scheduleTask(String cronExpression, Runnable task) throws SchedulerException, ParseException {
  //   Scheduler scheduler = new StdSchedulerFactory().getScheduler();
  //   JobDetail jobDetail = JobBuilder.newJob()
  //       .ofType(SimpleJob.class)
  //       .withIdentity("job1", "group1")
  //       .build();

  //   Trigger trigger = TriggerBuilder.newTrigger()
  //       .withIdentity("trigger1", "group1")
  //       .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
  //       .build();

  //   JobDataMap jobDataMap = new JobDataMap();
  //   jobDataMap.put("task", task);
  //   jobDetail.getJobDataMap().putAll(jobDataMap);

  //   scheduler.scheduleJob(jobDetail, trigger);
  //   scheduler.start();
  // }

  // public static class SimpleJob implements Job {
  //   @Override
  //   public void execute(JobExecutionContext context) throws JobExecutionException {
  //     Runnable task = (Runnable) context.getJobDetail().getJobDataMap().get("task");
  //     task.run();
  //   }
  // }
}
