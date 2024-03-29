package vn.com.fpt.jobservice.service.impl;

import com.fpt.fis.integration.grpc.IntegrationService.*;
import com.fpt.fis.integration.grpc.IntegrationServiceGrpcGrpc.IntegrationServiceGrpcBlockingStub;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class IntegrationServiceGrpc {
    @GrpcClient("integration")
    private IntegrationServiceGrpcBlockingStub integrationServiceBlockingStub;

    public List<IntegrationData> getListIntegration(String tenantId) {
        log.debug("getListIntegration - START");
        GetIntegrationListRequest request = GetIntegrationListRequest.newBuilder().setTenantId(tenantId).build();
        GetIntegrationListResult result = integrationServiceBlockingStub.getIntegrationList(request);
        log.debug("getListIntegration - END");
        return result.getDataList();
    }

    public GetIntegrationResult getIntegrationById(Long id, String tenantId) {
        log.debug("getIntegrationById - START");
        GetIntegrationRequest request = GetIntegrationRequest.newBuilder().setId(id).setTenantId(tenantId).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.getIntegration(request);
    }

    public ExecuteIntegrationResult verifyIntegrations(String request, String tenantId) throws Exception {
        log.debug("getIntegrationById - START");
        ExecuteIntegrationRequest grpcRequest = ExecuteIntegrationRequest.newBuilder().setRequest(request).setTenantId(tenantId).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.verifyIntegration(grpcRequest);
    }

    public ExecuteIntegrationResult executeIntegration(String request, String tenantId) throws Exception {
        log.debug("getIntegrationById - START");
        ExecuteIntegrationRequest grpcRequest = ExecuteIntegrationRequest.newBuilder().setRequest(request).setTenantId(tenantId).build();
        log.debug("getIntegrationById - END");

        return integrationServiceBlockingStub.executeIntegration(grpcRequest);
    }
}
