package vn.com.fpt.jobservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.fis.integration.grpc.ExecuteIntegrationResult;
import com.fpt.fis.integration.grpc.GetIntegrationListResult;
import com.fpt.fis.integration.grpc.GetIntegrationResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.fpt.jobservice.model.response.DataSourceDTO;
import vn.com.fpt.jobservice.service.impl.IntegrationServiceGrpc;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integrations")
@Slf4j
public class IntegrationController {
    @Autowired
    IntegrationServiceGrpc integrationServiceGrpc;

    @GetMapping()
    public GetIntegrationListResult getIntegrationGRPC() {
        GetIntegrationListResult result = integrationServiceGrpc.getListIntegration();
        return result;
    }

    @GetMapping("/field-mapping/{id}")
    public List<DataSourceDTO> getFieldMapping(@PathVariable("id") Long id) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        GetIntegrationResult integrationData = integrationServiceGrpc.getIntegrationById(id);
        ExecuteIntegrationResult result = integrationServiceGrpc.executeIntegration(integrationData.getStructure());

        JSONObject jsonObject = new JSONObject(result.getResult());
        Set<String> allKeys = extractFieldNames(jsonObject, "");

        List<DataSourceDTO> dataSources = allKeys.stream()
                .map(item -> DataSourceDTO.builder().id(item).name(item.replace(".", " - ")).build())
                .collect(Collectors.toList());


        return dataSources;
    }

    private static Set<String> extractFieldNames(JSONObject jsonObject, String parentFieldName) {
        Set<String> result = new TreeSet<>(Utils.getNestedFieldComparator());
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                // If the value is a JSONObject, recursively call the function with the new parent field name
                Set<String> extractedObject = extractFieldNames((JSONObject) value, getParentFieldName(parentFieldName, key));
                result.addAll(extractedObject);
            } else if (value instanceof JSONArray) {
                // If the value is a JSONArray, iterate through its elements
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object arrayElement = jsonArray.get(i);
                    if (arrayElement instanceof JSONObject) {
                        // If the array element is a JSONObject, recursively call the function with the new parent field name
                        String currentKey = "[" + key + "]";
                        Set<String> extractedObject = extractFieldNames((JSONObject) arrayElement, getParentFieldName(parentFieldName, currentKey));
                        result.addAll(extractedObject);
                    }
                }
            } else {
                // If the value is not an object or array, print the field name
                String finalFieldName = getParentFieldName(parentFieldName, key);
                result.add(finalFieldName);
            }
        }
        return result;
    }

    private static String getParentFieldName(String parentFieldName, String childFieldName) {
        if (parentFieldName.isEmpty()) {
            return childFieldName;
        } else {
            return parentFieldName + "." + childFieldName;
        }
    }

    @GetMapping("/execute/{id}")
    public ExecuteIntegrationResult executeIntegrationGRPC(@PathVariable("id") Long id) throws Exception {
        GetIntegrationResult integrationData = integrationServiceGrpc.getIntegrationById(id);
        String structureStr = integrationData.getStructure();

        return integrationServiceGrpc.executeIntegration(structureStr);
    }
}
