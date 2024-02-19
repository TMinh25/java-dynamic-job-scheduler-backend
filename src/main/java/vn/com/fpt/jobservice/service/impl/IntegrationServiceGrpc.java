package vn.com.fpt.jobservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fpt.fis.integration.grpc.*;
import com.fpt.fis.integration.grpc.IntegrationServiceGrpcGrpc.IntegrationServiceGrpcBlockingStub;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.utils.Utils;

@Slf4j
@Service
public class IntegrationServiceGrpc {
    @GrpcClient("integration")
    private IntegrationServiceGrpcBlockingStub integrationServiceBlockingStub;

    public GetIntegrationListResult getListIntegration() {
        log.debug("getListIntegration - START");
        GetIntegrationListRequest request = GetIntegrationListRequest.newBuilder().build();
        GetIntegrationListResult result = integrationServiceBlockingStub.getIntegrationList(request);
        log.debug("getListIntegration - END");
        return result;
    }

    public GetIntegrationResult getIntegrationById(Long id) {
        log.debug("getIntegrationById - START");
        GetIntegrationRequest request = GetIntegrationRequest.newBuilder().setId(id).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.getIntegration(request);
    }

    public ExecuteIntegrationResult verifyIntegrations(String request) throws Exception {
        log.debug("getIntegrationById - START");
        ExecuteIntegrationRequest grpcRequest = ExecuteIntegrationRequest.newBuilder().setRequest(request).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.verifyIntegration(grpcRequest);
    }

    public ExecuteIntegrationResult executeIntegration(String request) throws Exception {
        log.debug("getIntegrationById - START");
        ExecuteIntegrationRequest grpcRequest = ExecuteIntegrationRequest.newBuilder().setRequest(request).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.executeIntegration(grpcRequest);
    }
}
