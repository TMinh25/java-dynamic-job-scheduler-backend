package vn.com.fpt.jobservice.jobs.steps;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.base.BaseJobStep;
import vn.com.fpt.jobservice.service.impl.OrganizationServiceGrpc;

import java.util.List;
import java.util.Map;

public class SyncDataOrganizationGRPC extends BaseJobStep {

    public SyncDataOrganizationGRPC(BaseJob baseJob) {
        super(baseJob);
    }

    @Override
    protected void execute(JobExecutionContext context) throws JobExecutionException {
        final OrganizationServiceGrpc organizationServiceGrpc = (OrganizationServiceGrpc) context.get("organizationServiceGrpc");
        final Map<String, Object> keyMappedResult = (Map<String, Object>) context.get("keyMappedResult");
        logger("Syncing " + keyMappedResult.size() + " record(s) to organization-service");
        organizationServiceGrpc.executedForDataSync((List<Map<String, Object>>) keyMappedResult.get("data"));
        logger("Data has been synchronized!");
    }
}
