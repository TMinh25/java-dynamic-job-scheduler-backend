package vn.com.fpt.jobservice.service;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.jobs.BatchRenewalContract;

import java.text.ParseException;
import java.util.Date;

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
