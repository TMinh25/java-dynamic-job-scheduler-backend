package vn.com.fpt.jobservice.service;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.utils.TaskStatus;

@Configuration
@Slf4j
public class AppJobsListener implements JobListener {
    @Autowired
    TaskService _taskService;
    @Autowired
    TaskHistoryService _taskHistoryService;
    @Autowired
    JobService _jobService;

    @Override
    public String getName() {
        return "globalJob";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info(
                "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.info("AppJobsListener.jobToBeExecuted()");

        Date executionDate = new Date();

        String jobUUID = context.getJobDetail().getKey().getName();
        Task task = _taskService.readTaskByJobUUID(jobUUID)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "jobUUID", jobUUID));

        TaskHistory taskHistory = new TaskHistory();
        taskHistory.setTask(task);
        taskHistory.setStep(0L);
        taskHistory.setStatus(TaskStatus.PROCESSING);
        taskHistory.setStartedAt(executionDate);
        _taskHistoryService.insertNewHistoryOfTask(task.getId(), taskHistory);

        task.setPrevInvocation(executionDate);
        task.setStatus(taskHistory.getStatus());
        task.setRetryCount(task.getRetryCount() + 1);
        _taskService.updateTaskById(task.getId(), task.toModel());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("AppJobsListener.jobExecutionVetoed()");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        log.info("AppJobsListener.jobWasExecuted()");
        Task task = null;
        TaskHistory taskHistory = new TaskHistory();
        try {
            String jobUUID = context.getJobDetail().getKey().getName();
            task = _taskService.readTaskByJobUUID(jobUUID)
                    .orElseThrow(() -> new ResourceNotFoundException("Task", "jobUUID", jobUUID));
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
                _taskHistoryService.updateProcessingHistoryOfTask(task.getId(), taskHistory);

                task.setStatus(taskHistory.getStatus());
                if (task.getStatus() == TaskStatus.SUCCESS || task.getRetryCount() >= task.getMaxRetries())
                    task.setActive(false);

                _taskService.updateTaskById(task.getId(), task.toModel());
            }
        }
    }
}