package vn.com.fpt.jobservice.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
//import vn.com.fpt.jobservice.integration_service.grpc.GetIntegrationResult;

@Getter
@Setter
@Builder
@Slf4j
public class IntegrationResponse {
    public Integer statusCode;
    public Integer messageCode;
    public String message;
    public IntegrationData responseData;

//    public static IntegrationResponse fromGrpc(GetIntegrationResult integrationResult) {
//        try {
//            vn.com.fpt.jobservice.integration_service.grpc.IntegrationData item = integrationResult.getItem();
//            vn.com.fpt.jobservice.integration_service.grpc.RequestDetail detail = integrationResult.getStructure();
//            return IntegrationResponse
//                    .builder()
//                    .responseData(IntegrationData
//                            .builder()
//                            .item(IntegrationItem
//                                    .builder()
////                                    .id(item.getId())
//                                    .tenantId(item.getTenantId())
//                                    .name(item.getName())
//                                    .type(item.getType())
//                                    .url(item.getUrl())
//                                    .method(item.getMethod())
////                                    .params(item.getParams())
////                                    .headers(item.getHeaders())
////                                    .body(item.getBody())
////                                    .outputConfig(item.getOutputConfig())
////                                    .mappingConfig(item.getMappingConfig())
////                                    .auth(item.getAuth())
//                                    .description(item.getDescription())
//                                    .structure(item.getStructure())
//                                    .build())
//                            .structure(IntegrationStructure
//                                    .builder()
//                                    .tenantId(detail.getTenantId())
////                                    .integrationId(detail.getIntegrationId())
//                                    .url(detail.getUrl())
//                                    .method(detail.getMethod())
////                                    .params(detail.getParams())
//                                    .headers(detail.getHeadersMap())
////                                    .body(detail.getBody())
//                                    .outputConfig(detail.getOutputConfigMap())
//                                    .mappingConfig(detail.getMappingConfigMap())
////                                    .auth(detail.getAuth())
//                                    .build())
//                            .build())
//                    .build();
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return null;
//    }
}