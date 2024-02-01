//package vn.com.fpt.jobservice.service.impl;
//
//import lombok.extern.slf4j.Slf4j;
//import net.devh.boot.grpc.client.inject.GrpcClient;
//import org.springframework.stereotype.Service;
//import vn.com.fpt.jobservice.integration_service.grpc.GetIntegrationRequest;
//import vn.com.fpt.jobservice.integration_service.grpc.GetIntegrationResult;
//import vn.com.fpt.jobservice.integration_service.grpc.IntegrationServiceGrpc.IntegrationServiceBlockingStub;
//import vn.com.fpt.jobservice.integration_service.grpc.RequestDetail;
//import vn.com.fpt.jobservice.model.response.IntegrationResponse;
//import vn.com.fpt.jobservice.model.response.IntegrationStructure;
//
//@Slf4j
//@Service
//public class IntegrationServiceGrpcImpl {
//    @GrpcClient("grpc-integration")
//    IntegrationServiceBlockingStub synchronousClient;
//
//    public IntegrationResponse getIntegration(Long integrationId) {
//        try {
//            GetIntegrationRequest request = GetIntegrationRequest.newBuilder().setId(integrationId).build();
//
//            //new GetIntegrationRequest(integrationId);
//            GetIntegrationResult result = synchronousClient.getIntegration(request);
//
//            return IntegrationResponse.fromGrpc(result);
//        } catch (Exception e) {
//            log.error("getDepartment: ", e);
//        }
//        return null;
//    }
//
//    public Object executeIntegration(IntegrationStructure executionRequest) {
//        try {
//            RequestDetail response = synchronousClient.executeIntegration(RequestDetail);
//            return response;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
