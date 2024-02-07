package vn.com.fpt.jobservice.jobs.steps;

import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.jobs.base.BaseTaskStep;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

@Slf4j
public class BatchRenewalIntegration extends BaseTaskStep {
    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @Override
    protected void execute(JobExecutionContext context) {
        final String integrationURL = (String) context.get("integrationURL");
        final Task task = (Task) context.get("task");
//
//        // Getting data from integration
//        Long integrationId = task.getIntegrationId();
//        String getIntegrationURL = String.format("%s/get/%s", integrationURL, integrationId);
//        HttpHeaders headers = new HttpHeaders();
//        IntegrationResponse response = CallExternalAPI.exchangeGet(
//                getIntegrationURL,
//                headers,
//                IntegrationResponse.class);
//
//        if (response != null) {
//            // Execute the integration thread
//            IntegrationStructure executionRequest = response.getResponseData().getStructure();
//            String executeIntegrationURL = String.format("%s/execute", integrationURL);
//            Object executionResponse = CallExternalAPI.exchangePost(
//                    executeIntegrationURL,
//                    headers,
//                    executionRequest,
//                    Object.class);
//            assert executionResponse != null;
//            log.info(executionResponse.toString());
//        } else {
//            throw new JobExecutionException("Can not get data for integration!");
//        }
        GetIntegrationResult result = integrationServiceGrpc.getIntegrationById(task.getIntegrationId());
        System.out.println(result);
        ExecuteIntegrationResult res = integrationServiceGrpc.executeIntegration(Utils.stringToObject(result.getStructure(), IntegrationStructure.class));
        System.out.println(res);

        System.out.println("Done");
    }
}
