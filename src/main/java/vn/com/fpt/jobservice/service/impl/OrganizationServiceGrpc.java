package vn.com.fpt.jobservice.service.impl;

import com.google.protobuf.Any;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.organization.grpc.DataSyncRequest;
import vn.com.fpt.jobservice.organization.grpc.DataMapping;
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

    public Object executedForDataSync(List<Map<String, Object>> request) {

        try {
            List<DataMapping> dataMappingList = new ArrayList<>();

            request.forEach(it -> {
                DataMapping dataMapping = DataMapping.newBuilder()
                        .putAllParam(it.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        e -> (String) e.getValue())))
                        .build();
                dataMappingList.add(dataMapping);
            });

            DataSyncRequest dataSyncRequest = DataSyncRequest.newBuilder()
                    .addAllDataMapping(dataMappingList)
                    .build();

            return organizationClient.executedForDataSync(dataSyncRequest);

        } catch (Exception e) {
            log.error("createDepartment: ", e);
        }

        return new Object();
    }

}
