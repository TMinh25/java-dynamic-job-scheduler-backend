package vn.com.fpt.jobservice.jobs.steps;

import com.fpt.fis.integration.grpc.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

public class BatchTicketCreationIntegration extends BaseJobStep {

    public BatchTicketCreationIntegration(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final String integrationURL = (String) context.get("integrationURL");
        final Task task = (Task) context.get("task");
        final IntegrationServiceGrpc integrationServiceGrpc = (IntegrationServiceGrpc) context.get("integrationServiceGrpc");
        try {
            GetIntegrationResult result = integrationServiceGrpc.getIntegrationById(task.getIntegrationId());
            logger(String.format("Integration data for id %s:", task.getIntegrationId()));
            logger("- url      : " + result.getItem().getUrl());
            logger("- method   : " + result.getItem().getMethod());
            logger("- structure: " + result.getStructure());
            ExecuteIntegrationResult res = integrationServiceGrpc.executeIntegration(result.getStructure());
            logger("Execute integration result: " + res.toString());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
