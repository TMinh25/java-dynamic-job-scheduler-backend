package vn.com.fpt.jobservice.jobs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.model.response.ApiResponse;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.service.impl.OrganizationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SyncDataExternalMappingChange extends BaseJob {

    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Autowired
    OrganizationServiceGrpc organizationServiceGrpc;

    @Override
    protected void defineSteps() {
    }

    /**
     *  Input data with "from":
     *  - array ends with object : key.[key2][1].key22 vs key,key22 =object, [key2]= array, [1]= position of element (The first number starts with 0) in list (get all value don't need add)
     *  - array ends with array: key.[key2][1]
     *  - with object: key.key1.key2...keyn
     *  Input data with "to":
     *  - array ends with object: key.[key1].key2.[key...keyn].keyn+1 result like this: {"key":{"key1":["key2":"...","key":...["keyn":["keyn+1":"..."]]]}}
     *  - array ends with array: key.[key1] result like this: {"key":{"key1":["2","3"]}}
     * */

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

                ApiResponse<Map<String, Object>> response = objectMapper.readValue(
                        executeIntegrationResult.getResult(),
                        new TypeReference<ApiResponse<Map<String, Object>>>() {
                        }
                );

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
