package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.utils.JobUtils;

import java.util.*;

@Service
@Slf4j
public class JobServiceImpl implements JobService {
    private final String groupKey = "JOB_SERVICE";

    @Autowired
    @Lazy
    SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ApplicationContext context;

    /**
     * Schedule a job by jobName at given date.
     */
    @Override
    public boolean scheduleOneTimeJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date date) {
        log.debug("Request received to scheduleJob");

        String jobKey = jobName;
        String triggerKey = jobName;

        JobDetail jobDetail = JobUtils.createJob(jobClass, false, context, jobKey, this.groupKey);

        log.debug("creating trigger for key: " + jobKey + " at date: " + date);
        Trigger cronTriggerBean = JobUtils.createSingleTrigger(triggerKey, date,
                SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        // Trigger cronTriggerBean = JobUtil.createSingleTrigger(triggerKey, date,
        // SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
            log.debug("Job with key jobKey: " + jobKey + " and group:" + this.groupKey
                    + " scheduled successfully for date:" + dt);
            return true;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while scheduling job with key:" + jobKey + " message:" + e.getMessage());
        }

        return false;
    }

    /**
     * Schedule a job by jobName at given date.
     */
    @Override
    public boolean scheduleCronJob(String jobName, Class<? extends QuartzJobBean> jobClass, Date date,
                                   String cronExpression) {
        log.debug("Request received to scheduleJob");

        String jobKey = jobName;
        String triggerKey = jobName;

        JobDetail jobDetail = JobUtils.createJob(jobClass, false, context, jobKey, this.groupKey);

        log.debug("creating trigger for key: " + jobKey + " at date: " + date);
        Trigger cronTriggerBean = JobUtils.createCronTrigger(triggerKey, date, cronExpression,
                SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            Date dt = scheduler.scheduleJob(jobDetail, cronTriggerBean);
            log.debug("Job with key jobKey: " + jobKey + " and group:" + this.groupKey
                    + " scheduled successfully for date:" + dt);
            return true;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while scheduling job with key:" + jobKey + " message:" + e.getMessage());
        }

        return false;
    }

    /**
     * Update one time scheduled job.
     */
    @Override
    public boolean updateOneTimeJob(String jobName, Date date) {
        log.debug("Request received for updating one time job.");

        String jobKey = jobName;

        log.debug("Parameters received for updating one time job jobKey: " + jobKey + ", date: " + date);
        try {
            // Trigger newTrigger = JobUtil.createSingleTrigger(jobKey, date,
            // SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
            Trigger newTrigger = JobUtils.createSingleTrigger(jobKey, date, SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

            Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            log.debug("Trigger associated with jobKey: " + jobKey + " rescheduled successfully for date: " + dt);
            return true;
        } catch (Exception e) {
            log.error("SchedulerException while updating one time job with key: " + jobKey + " message: "
                    + e.getMessage());
            return false;
        }
    }

    /**
     * Update scheduled cron job.
     */
    @Override
    public boolean updateCronJob(String jobName, Date date, String cronExpression) {
        log.debug("Request received for updating cron job.");

        String jobKey = jobName;

        log.debug("Parameters received for updating cron job jobKey: " + jobKey + ", date: " + date);
        try {
            // Trigger newTrigger = JobUtil.createSingleTrigger(jobKey, date,
            // SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT);
            Trigger newTrigger = JobUtils.createCronTrigger(jobKey, date, cronExpression,
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);

            Date dt = schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobKey), newTrigger);
            log.debug("Trigger associated with jobKey: " + jobKey + " rescheduled successfully for date: " + dt);
            return true;
        } catch (Exception e) {
            log.error(
                    "SchedulerException while updating cron job with key: " + jobKey + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove the indicated Trigger from the scheduler.
     * If the related job does not have any other triggers, and the job is not
     * durable, then the job will also be deleted.
     */
    @Override
    public boolean unscheduleJob(String jobName) {
        log.debug("Request received for unscheduleding job.");

        String jobKey = jobName;

        TriggerKey tkey = new TriggerKey(jobKey);
        log.debug("Parameters received for unscheduling job : tkey: " + jobKey);
        try {
            boolean status = schedulerFactoryBean.getScheduler().unscheduleJob(tkey);
            log.debug("Trigger associated with jobKey: " + jobKey + " unscheduled with status: " + status);
            return status;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while unscheduling job with key: " + jobKey + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete the identified Job from the Scheduler - and any associated Triggers.
     */
    @Override
    public boolean deleteJob(String jobName) {
        log.debug("Request received for deleting job.");

        String jobKey = jobName;

        JobKey jkey = new JobKey(jobKey, this.groupKey);
        log.debug("Parameters received for deleting job jobKey: " + jobKey);

        try {
            boolean status = schedulerFactoryBean.getScheduler().deleteJob(jkey);
            log.debug("Job with jobKey: " + jobKey + " deleted with status: " + status);
            return status;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while deleting job with key: " + jobKey + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Pause a job
     */
    @Override
    public boolean pauseJob(String jobName) {
        log.debug("Request received for pausing job.");

        String jobKey = jobName;
        JobKey jkey = new JobKey(jobKey, this.groupKey);
        log.debug("Parameters received for pausing job jobKey: " + jobKey + ", groupkey: " + this.groupKey);

        try {
            schedulerFactoryBean.getScheduler().pauseJob(jkey);
            log.debug("Job with jobKey: " + jobKey + " paused succesfully.");
            return true;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while pausing job with key: " + jobName + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resume paused job
     */
    @Override
    public boolean resumeJob(String jobName) {
        log.debug("Request received for resuming job.");

        String jobKey = jobName;

        JobKey jKey = new JobKey(jobKey, this.groupKey);
        log.debug("Parameters received for resuming job jobKey: " + jobKey);
        try {
            schedulerFactoryBean.getScheduler().resumeJob(jKey);
            log.debug("Job with jobKey: " + jobKey + " resumed succesfully.");
            return true;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while resuming job with key: " + jobKey + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Start a job now
     */
    @Override
    public boolean startJobNow(String jobName) {
        log.debug("Request received for starting job now.");

        String jobKey = jobName;

        JobKey jKey = new JobKey(jobKey, this.groupKey);
        log.debug("Parameters received for starting job now : jobKey: " + jobKey);
        try {
            schedulerFactoryBean.getScheduler().triggerJob(jKey);
            log.debug("Job with jobKey: " + jobKey + " started now succesfully.");
            return true;
        } catch (SchedulerException e) {
            log.error(
                    "SchedulerException while starting job now with key: " + jobKey + " message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if job is already running
     */
    @Override
    public boolean isJobRunning(String jobName) {
        log.debug("Request received to check if job is running");

        String jobKey = jobName;

        log.debug("Parameters received for checking job is running now : jobKey: " + jobKey);
        try {

            List<JobExecutionContext> currentJobs = schedulerFactoryBean.getScheduler().getCurrentlyExecutingJobs();
            if (currentJobs != null) {
                for (JobExecutionContext jobCtx : currentJobs) {
                    String jobNameDB = jobCtx.getJobDetail().getKey().getName();
                    String groupNameDB = jobCtx.getJobDetail().getKey().getGroup();
                    if (jobKey.equalsIgnoreCase(jobNameDB) && this.groupKey.equalsIgnoreCase(groupNameDB)) {
                        return true;
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with key: " + jobKey + " is running. error message: "
                    + e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Get all jobs
     */
    @Override
    public List<Map<String, Object>> getAllJobs() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey: scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    String jobName = jobKey.getName();
                    String jobGroup = jobKey.getGroup();

                    // get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    if (triggers.get(0) != null) {
                        Date scheduleTime = triggers.get(0).getStartTime();
                        Date nextFireTime = triggers.get(0).getNextFireTime();
                        Date lastFiredTime = triggers.get(0).getPreviousFireTime();

                        Map<String, Object> map = new HashMap<>();
                        map.put("jobName", jobName);
                        map.put("groupName", jobGroup);
                        map.put("scheduleTime", new Date(scheduleTime.getTime()).toString());
                        map.put("lastFiredTime",
                                lastFiredTime != null ? new Date(lastFiredTime.getTime()).toString() : null);
                        map.put("nextFireTime", new Date(nextFireTime.getTime()).toString());

                        if (isJobRunning(jobName)) {
                            map.put("jobStatus", "RUNNING");
                        } else {
                            String jobState = getJobState(jobName);
                            map.put("jobStatus", jobState);
                        }

                        list.add(map);
                        log.debug("Job details:");
                        log.debug(
                                "Job Name:" + jobName + ", Group Name:" + groupName + ", Schedule Time:" + scheduleTime);
                    }
                }

            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while fetching all jobs. error message: " + e.getMessage());
        } catch (Exception e) {
        }
        return list;
    }

    /**
     * Check job exist with given name
     */
    @Override
    public boolean isJobWithNamePresent(String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, this.groupKey);
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            if (scheduler.checkExists(jobKey)) {
                return true;
            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with name and group exist:" + e.getMessage());
        }
        return false;
    }

    /**
     * Get the current state of job
     */
    public String getJobState(String jobName) {
        log.debug("JobServiceImpl.getJobState()");

        try {
            JobKey jobKey = new JobKey(jobName, this.groupKey);

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
            if (triggers != null && triggers.size() > 0) {
                for (Trigger trigger : triggers) {
                    TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());

                    if (TriggerState.PAUSED.equals(triggerState)) {
                        return "PAUSED";
                    } else if (TriggerState.BLOCKED.equals(triggerState)) {
                        return "BLOCKED";
                    } else if (TriggerState.COMPLETE.equals(triggerState)) {
                        return "COMPLETE";
                    } else if (TriggerState.ERROR.equals(triggerState)) {
                        return "ERROR";
                    } else if (TriggerState.NONE.equals(triggerState)) {
                        return "NONE";
                    } else if (TriggerState.NORMAL.equals(triggerState)) {
                        return "SCHEDULED";
                    }
                }
            }
        } catch (SchedulerException e) {
            log.error("SchedulerException while checking job with name and group exist:" + e.getMessage());
        }
        return null;
    }

    /**
     * Stop a job
     */
    @Override
    public boolean stopJob(String jobName) {
        log.debug("JobServiceImpl.stopJob()");
        try {
            String jobKey = jobName;

            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jkey = new JobKey(jobKey, this.groupKey);

            return scheduler.interrupt(jkey);

        } catch (SchedulerException e) {
            log.error("SchedulerException while stopping job. error message: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean triggerJob(String jobName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = new JobKey(jobName, this.groupKey);

            scheduler.triggerJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean interuptJob(String jobName) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobKey jobKey = new JobKey(jobName, this.groupKey);

            scheduler.pauseJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
