package vn.com.fpt.jobservice.jobs.steps;

import com.fpt.fis.integration.grpc.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;

public class GetIntegrationResultGRPC extends BaseJobStep {

    public GetIntegrationResultGRPC(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final Task task = (Task) context.get("task");
        final IntegrationServiceGrpc integrationServiceGrpc = (IntegrationServiceGrpc) context.get("integrationServiceGrpc");
        try {
            GetIntegrationResult result = integrationServiceGrpc.getIntegrationById(task.getIntegrationId());
            logger(String.format("Integration data for id %s:", task.getIntegrationId()));
            logger("- url      : " + result.getItem().getUrl());
            logger("- method   : " + result.getItem().getMethod());
            logger("- structure: " + result.getStructure());
            ExecuteIntegrationResult res = integrationServiceGrpc.executeIntegration(result.getStructure());
            logger("Execute integration result: " + res);
            context.put("integrationResult", res.getResult());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
