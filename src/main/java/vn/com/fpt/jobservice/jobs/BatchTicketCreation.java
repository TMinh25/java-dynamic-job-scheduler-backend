package vn.com.fpt.jobservice.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.steps.*;
import vn.com.fpt.jobservice.model.LogModel;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.service.impl.KafkaProducer;
import vn.com.fpt.jobservice.utils.enums.TaskTypeType;

@Slf4j
@Component
public class BatchTicketCreation extends BaseJob {
    @Value("${u-service-api}")
    String uServiceURL;

    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Autowired
    KafkaProducer kafkaProducer;

    @Override
    protected void defineSteps() {
        this.addStep(new ShowJobContext(this));

        if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {
            this.addStep(new GetIntegrationResultGRPC(this));

            this.addStep(new RemapKeys(this, "integrationResult"));

            if (this.task.getTaskType().getType() == TaskTypeType.MANUAL) {
                this.addStep(new BatchTicketCreationManual(this));
            }
        } else {
            this.addStep(new BatchTicketCreationSPro(this));
        }
    }

    @Override
    protected void jobFinish() {
        logger(LogModel.LOGS.WAITING.get());
    }

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.initializedData(context);

        // Declare data for job steps
        final Task task = (Task) context.get("task");
        context.put("uServiceURL", uServiceURL);
        context.put("integrationServiceGrpc", integrationServiceGrpc);
        context.put("kafkaProducer", kafkaProducer);

        super.executeInternal(context);
    }
}
