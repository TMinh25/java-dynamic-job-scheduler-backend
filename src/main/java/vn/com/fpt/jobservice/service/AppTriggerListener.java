package vn.com.fpt.jobservice.service;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Component
@Slf4j
public class AppTriggerListener implements TriggerListener {

    @Override
    public String getName() {
        return "globalTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println(trigger);
        log.info("AppTriggerListener.triggerFired()");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        log.info("AppTriggerListener.vetoJobExecution()");
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        log.info("AppTriggerListener.triggerMisfired()");
        String jobName = trigger.getJobKey().getName();
        log.info("Job name: " + jobName + " is misfired");
    }

    @Override
    public void triggerComplete(
            Trigger trigger,
            JobExecutionContext context,
            Trigger.CompletedExecutionInstruction triggerInstructionCode) {
        log.info("AppTriggerListener.triggerComplete()");
    }
}