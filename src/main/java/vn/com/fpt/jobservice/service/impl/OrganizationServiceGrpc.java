package vn.com.fpt.jobservice.service.impl;

import com.fpt.fis.organization.grpc.DataSyncResponse;
import com.fpt.fis.organization.grpc.SyncOrgChartRequestMessage;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fpt.fis.organization.grpc.OrganizationServiceGrpc.*;
import com.fpt.fis.organization.grpc.DataMappingRequest;

import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganizationServiceGrpc {

    @GrpcClient("organization-service")
    private OrganizationServiceBlockingStub organizationClient;

    public void executedForDataSync(List<Map<String, Object>> request) {

        try {
            List<DataMappingRequest> dataMappingList = new ArrayList<>();

            request.forEach(it -> {
                DataMappingRequest dataMapping = DataMappingRequest.newBuilder()
                        .putAllParam(it.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey,
                                        e -> e.getValue() == null ?
                                                "null" : e.getValue().toString())))
                        .build();
                dataMappingList.add(dataMapping);
            });

            SyncOrgChartRequestMessage dataSyncRequest = SyncOrgChartRequestMessage.newBuilder()
                    .addAllDataMappings(dataMappingList)
                    .build();

            DataSyncResponse response = organizationClient.executeSyncOrgChart(dataSyncRequest);
            log.debug("Sync department response: {}", response);

        } catch (Exception e) {
            log.error("createDepartment: ", e);
        }

    }

}
