package vn.com.fpt.jobservice.service.impl;

import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.organization.grpc.CreateDepartmentRequest;
import vn.com.fpt.jobservice.organization.grpc.DepartmentMapping;
import vn.com.fpt.jobservice.organization.grpc.OrganizationServiceGrpc.OrganizationServiceBlockingStub;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganizationServiceGrpc {

    @GrpcClient("organization-service")
    private OrganizationServiceBlockingStub organizationClient;

    public Object createDepartment(List<Map<String, Object>> request) {

        try {
            List<DepartmentMapping> departmentMappingList = new ArrayList<>();

            request.forEach(it -> {
                DepartmentMapping departmentMapping = DepartmentMapping.newBuilder()
                        .putAllParam(it.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        e -> (String) e.getValue())))
                        .build();
                departmentMappingList.add(departmentMapping);
            });

            CreateDepartmentRequest createDepartmentRequest = CreateDepartmentRequest.newBuilder()
                    .addAllDepartmentMapping(departmentMappingList)
                    .build();

            return organizationClient.createDepartment(createDepartmentRequest);

        } catch (Exception e) {
            log.error("createDepartment: ", e);
        }
        return new Object();

    }

}
