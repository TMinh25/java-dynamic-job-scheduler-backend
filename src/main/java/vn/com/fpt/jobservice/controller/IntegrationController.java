package vn.com.fpt.jobservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationListResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import org.springframework.aop.interceptor.AsyncExecutionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.fpt.jobservice.model.response.ApiResponse;
import vn.com.fpt.jobservice.model.response.IntegrationResponse;
import vn.com.fpt.jobservice.model.response.IntegrationStructure;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/integrations")
public class IntegrationController {
    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @GetMapping()
    public GetIntegrationListResult getIntegrationGRPC() {
        GetIntegrationListResult result = integrationServiceGrpc.getListIntegration();
        return result;
    }

    @GetMapping("/field-mapping/{id}")
    public Map<String, Object> getFieldMapping(@PathVariable("id") Long id) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        GetIntegrationResult integrationData = integrationServiceGrpc.getIntegrationById(id);
        String structureStr = integrationData.getStructure();

        ExecuteIntegrationResult result = integrationServiceGrpc.executeIntegration(structureStr);
        ApiResponse<Map<String, Object>> response = objectMapper.readValue(result.getResult(), new TypeReference<ApiResponse<Map<String, Object>>>() {
        });
        return Utils.convertToMap(response.getResponseData());
    }

    @GetMapping("/execute/{id}")
    public ExecuteIntegrationResult executeIntegrationGRPC(@PathVariable("id") Long id) throws Exception {
        GetIntegrationResult integrationData = integrationServiceGrpc.getIntegrationById(id);
        String structureStr = integrationData.getStructure();

        return integrationServiceGrpc.executeIntegration(structureStr);
    }
}
