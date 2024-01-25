package vn.com.fpt.jobservice.service;

import org.springframework.scheduling.quartz.QuartzJobBean;
import vn.com.fpt.jobservice.model.JobModel;

import java.util.Date;
import java.util.List;

public interface JobService {

    boolean scheduleOneTimeJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date date);

    boolean scheduleCronJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date date, String cronExpression);

    boolean updateOneTimeJob(String jobName, Date date);

    boolean updateCronJob(String jobName, Date date, String cronExpression);

    boolean unscheduleJob(String jobName);

    boolean deleteJob(String jobName);

    boolean pauseJob(String jobName);

    boolean resumeJob(String jobName);

    boolean startJobNow(String jobName);

    boolean isJobRunning(String jobName);

    List<JobModel> getAllJobs();

    boolean isJobWithNamePresent(String jobName);

    String getJobState(String jobName);

    boolean stopJob(String jobName);

    boolean triggerJob(String jobName);

    boolean interuptJob(String jobName);

}