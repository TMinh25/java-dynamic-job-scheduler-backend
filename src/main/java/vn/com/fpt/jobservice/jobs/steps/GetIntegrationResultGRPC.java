package vn.com.fpt.jobservice.jobs.steps;

import com.fpt.fis.integration.grpc.IntegrationService.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.IntegrationService.GetIntegrationResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

public class GetIntegrationResultGRPC extends BaseJobStep {

    public GetIntegrationResultGRPC(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final Task task = (Task) context.get("task");
        final IntegrationServiceGrpc integrationServiceGrpc = (IntegrationServiceGrpc) context.get("integrationServiceGrpc");
        try {
            GetIntegrationResult result = integrationServiceGrpc.getIntegrationById(task.getIntegrationId(), task.getTenantId());
            logger(String.format("Integration data for id %s:", task.getIntegrationId()));
            logger("- url      : " + result.getItem().getUrl());
            logger("- method   : " + result.getItem().getMethod());
            logger("- structure: " + new JSONObject(result.getStructure()));
            ExecuteIntegrationResult res = integrationServiceGrpc.executeIntegration(result.getStructure(), task.getTenantId());
            logger("Execute integration result: " + (Utils.isJsonArray(res.getResult()) ? new JSONArray(res.getResult()) : new JSONObject(res.getResult())));
            context.put("integrationResult", res.getResult());
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
