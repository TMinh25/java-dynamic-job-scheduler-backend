package vn.com.fpt.jobservice.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import vn.com.fpt.jobservice.jobs.base.SystemJob;
import vn.com.fpt.jobservice.jobs.steps.BatchTicketCreationIntegration;
import vn.com.fpt.jobservice.model.response.ApiResponse;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.service.impl.OrganizationServiceGrpc;
import vn.com.fpt.jobservice.utils.CallExternalAPI;
import vn.com.fpt.jobservice.utils.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SyncDataExternalMappingChange extends SystemJob {

    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Autowired
    OrganizationServiceGrpc organizationServiceGrpc;

    @Override
    protected void defineSteps() {

    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        super.executeInternal(context);
        if (this.task.getIntegrationId() != null && this.task.getIntegrationId() != 0) {

            try {
                ObjectMapper objectMapper = new ObjectMapper();

                GetIntegrationResult getIntegrationResult = integrationServiceGrpc.getIntegrationById(task.getIntegrationId());
                ExecuteIntegrationResult executeIntegrationResult = integrationServiceGrpc.executeIntegration(
                        getIntegrationResult.getStructure()
                );

                ApiResponse<Map<String, Object>> response = objectMapper.readValue(executeIntegrationResult.getResult(),
                        new TypeReference<>() {
                        });

                List<Map<String, Object>> dataList = response.getResponseData().getData();
                List<Map<String, String>> remapKeys = Utils.convertMapKeyObjectsToMapString(
                        task.toModel().getTaskInputData()
                );

                List<Map<String, Object>> dataListAfterChange = new ArrayList<>();

                if (dataList == null) {
                    logger("Job Execution is failed by ExecuteIntegration get result has no data!");
                    throw new JobExecutionException();
                }
                logger("Execute Integration got " + dataList.size() + " record(s)");

                dataList.forEach(it -> dataListAfterChange.add(Utils.remapObjectByKeys(it, remapKeys)));

                if (dataListAfterChange.isEmpty()) {
                    logger("Job Execution is failed by there aren't field were matched.");
                    throw new JobExecutionException();
                }

                logger("Data got " + dataListAfterChange.size() + " record(s) matching");
                logger("Data starts synchronizing ...");

                organizationServiceGrpc.executedForDataSync(dataListAfterChange);

                logger("Data has been synchronized!");

            } catch (Exception e) {
                logger("Job Execution is failed by " + e.getMessage());
                throw new JobExecutionException(e);
            }

        }
    }
}
