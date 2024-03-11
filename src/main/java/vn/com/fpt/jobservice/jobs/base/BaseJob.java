package vn.com.fpt.jobservice.jobs.base;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import vn.com.fpt.jobservice.entity.StepHistory;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.entity.TaskHistory;
import vn.com.fpt.jobservice.model.LogModel;
import vn.com.fpt.jobservice.service.StepHistoryService;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.utils.TaskStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@DisallowConcurrentExecution
public abstract class BaseJob extends QuartzJobBean implements InterruptableJob {
    public String type;
    protected Task task;
    protected String jobUUID;
    private final List<BaseJobStep> steps = new ArrayList<>();
    private final List<LogModel> logs = new ArrayList<>();
    protected volatile boolean toStopFlag = true;

    protected JobExecutionContext context;

    @Autowired
    TaskService taskService;

    @Autowired
    StepHistoryService stepHistoryService;

    public void logger(String msg) {
        // String loggingOutput = String.format("[%s] %s", this.getClass().getName(), msg);
        String loggingOutput = String.format("%s", msg);
        log.info(loggingOutput);
        this.logs.add(LogModel.builder().time(new Date()).content(loggingOutput).build());
        this.context.put("logs", logs);
    }

    protected abstract void defineSteps() throws JobExecutionException;

    protected void addStep(BaseJobStep step) {
        this.steps.add(step);
    }

    private void preStepExecute(int currentStep, BaseJobStep step, TaskHistory taskHistory) {
        logger(String.format("Job execute step #%s: %s", currentStep, step.getClass().getSimpleName()));
        StepHistory stepHistory = new StepHistory();
        stepHistory.setStep(currentStep);
        stepHistory.setStepName(step.getClass().getName());
        stepHistoryService.insertNewStepOfTaskHistory(taskHistory.getId(), stepHistory);
    }

    private void postStepExecute(TaskHistory taskHistory) {
        StepHistory stepHistory = new StepHistory();
        stepHistory.setStatus(TaskStatus.SUCCESS);
        stepHistoryService.updateProcessingStepOfTaskHistory(taskHistory.getId(), stepHistory);
    }

    private void stepExceptionHandler(TaskHistory taskHistory, Exception e) {
        StepHistory stepHistory = new StepHistory();
        stepHistory.setStatus(TaskStatus.ERRORED);
        stepHistory.setErrorMessage(e.getMessage());
        stepHistoryService.updateProcessingStepOfTaskHistory(taskHistory.getId(), stepHistory);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        this.context = context;
        logger("Job executing...");

        JobKey key = context.getJobDetail().getKey();
        this.jobUUID = key.getName();
        this.task = taskService.readTaskByJobUUID(this.jobUUID);
        context.put("task", this.task);

        defineSteps();

        final TaskHistory taskHistory = (TaskHistory) context.get("taskHistory");

        int currentStep = 0;
        for (BaseJobStep step : steps) {
            currentStep++;
            if (toStopFlag) {
                try {
                    preStepExecute(currentStep, step, taskHistory);
                    step.execute(context);
                    postStepExecute(taskHistory);
                } catch (Exception e) {
                    logger(e.getMessage());
                    stepExceptionHandler(taskHistory, e);
                    throw new JobExecutionException(e);
                }
            } else {
                logger("Job execution is interrupted.");
                break;
            }
        }
        logger("Job finished!");
    }

    @Override
    public void interrupt() {
        toStopFlag = false;
    }
}
