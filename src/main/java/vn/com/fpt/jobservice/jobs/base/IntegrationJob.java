package vn.com.fpt.jobservice.jobs.base;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.UnableToInterruptJobException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.InternalIntegration;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.repositories.InternalIntegrationRepository;
import vn.com.fpt.jobservice.service.TaskService;

@Slf4j
public abstract class IntegrationJob extends QuartzJobBean implements InterruptableJob {
    protected volatile boolean toStopFlag = true;
    protected String className = this.getClass().getName();
    protected String jobUUID;
    protected Task task;
    protected InternalIntegration internalIntegration;
    @Autowired
    TaskService taskService;
    @Autowired
    InternalIntegrationRepository iiRepository;

    @Value("${integration-api}")
    String integrationURL;

    protected void jobInfo(String msg) {
        log.info(String.format("[%s] %s", this.className, msg));
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info(
                "________________________________________________________________________________________________________________________");
        jobInfo("executing...");
        JobKey key = context.getJobDetail().getKey();
        this.jobUUID = key.getName();

        this.task = taskService.readTaskByJobUUID(this.jobUUID);
        this.internalIntegration = iiRepository.getReferenceById(this.internalIntegration.getId());

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
