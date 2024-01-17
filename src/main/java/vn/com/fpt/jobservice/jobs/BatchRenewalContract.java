package vn.com.fpt.jobservice.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class BatchRenewalContract extends BaseJob {

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);
        try {
            Thread.sleep(3000);

            // ObjectMapper objectMapper = new ObjectMapper();
            // RequestDetailsModel taskInputData = objectMapper.readValue(
            //     this.task.getTaskInputData(),
            //     RequestDetailsModel.class);

            jobInfo(task.getTaskInputData());
        } catch (Exception e) {
            throw new JobExecutionException(e.getMessage());
        }
    }
}
