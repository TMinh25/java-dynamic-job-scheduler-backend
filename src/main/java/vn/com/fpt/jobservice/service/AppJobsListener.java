package vn.com.fpt.jobservice.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.Date;

@Configuration
@Slf4j
public class AppJobsListener implements JobListener {
    @Autowired
    TaskService taskService;
    @Autowired
    TaskHistoryService taskHistoryService;

    @Override
    public String getName() {
        return "globalJob";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.debug("AppJobsListener.jobToBeExecuted()");

        Date executionDate = new Date();

        String jobUUID = context.getJobDetail().getKey().getName();
        Task task = taskService.readTaskByJobUUID(jobUUID);

        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setStep(0L);
        taskHistory.setStatus(TaskStatus.PROCESSING);
        taskHistory.setStartedAt(executionDate);
        taskHistoryService.insertNewHistoryOfTask(task.getId(), taskHistory);

        task.setPrevInvocation(executionDate);
        task.setStatus(taskHistory.getStatus());
        task.setRetryCount(task.getRetryCount() + 1);
        taskService.updateTaskById(task.getId(), task.toModel());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.debug("AppJobsListener.jobExecutionVetoed()");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.debug("AppJobsListener.jobWasExecuted()");
        Task task = null;
        TaskHistory taskHistory = new TaskHistory();
        try {
            String jobUUID = context.getJobDetail().getKey().getName();
            task = taskService.readTaskByJobUUID(jobUUID);
            taskHistory.setTask(task);
            taskHistory.setStatus(TaskStatus.SUCCESS);

            if (jobException != null) {
                throw jobException;
            }

        } catch (Exception e) {
            taskHistory.setStatus(TaskStatus.ERRORED);
            taskHistory.setErrorMessage(e.getMessage());
        } finally {
            if (task != null) {
                taskHistory.setEndedAt(new Date());
                taskHistoryService.updateProcessingHistoryOfTask(task.getId(), taskHistory);

                task.setStatus(taskHistory.getStatus());
                if (!task.canScheduleJob())
                    task.setActive(false);

                taskService.updateTaskById(task.getId(), task.toModel());
            }
        }
    }
}