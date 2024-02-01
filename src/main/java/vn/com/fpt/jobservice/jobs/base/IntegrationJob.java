package vn.com.fpt.jobservice.jobs.base;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.repositories.InternalIntegrationRepository;
import vn.com.fpt.jobservice.service.TaskService;

public abstract class IntegrationJob extends BaseJob implements InterruptableJob {
    protected volatile boolean toStopFlag = true;
    protected String className = this.getClass().getName();
    protected String jobUUID;
    protected Task task;
    static String type = "INTEGRATION";
    @Autowired
    TaskService taskService;
    @Autowired
    InternalIntegrationRepository _iiRepository;

    @Value("${integration-api}")
    String integrationURL;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        jobInfo("executing...");
        JobKey key = context.getJobDetail().getKey();
        this.jobUUID = key.getName();

        this.task = taskService.readTaskByJobUUID(this.jobUUID);

        jobInfo(String.format("Job started with key: %s, group: %s, thread: %s",
                key.getName(),
                key.getGroup(),
                Thread.currentThread().getName()));
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        jobInfo(String.format("[%s] stopping thread... ", this.className));
        toStopFlag = false;
    }
}
