package vn.com.fpt.jobservice.jobs.base;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.service.TaskService;

@Slf4j
public abstract class BaseJob extends QuartzJobBean implements InterruptableJob {
    public String type;
    protected Task task;
    protected String jobUUID;
    protected volatile boolean toStopFlag = true;
    protected String className = this.getClass().getName();

    @Autowired
    TaskService taskService;

    protected void jobInfo(String msg) {
        log.info(String.format("[%s] %s", this.getClass().getName(), msg));
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        jobInfo("executing...");

        JobKey key = context.getJobDetail().getKey();
        this.jobUUID = key.getName();
        this.task = taskService.readTaskByJobUUID(this.jobUUID);
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        jobInfo(String.format("[%s] stopping thread... ", this.className));
        toStopFlag = false;
    }
}
