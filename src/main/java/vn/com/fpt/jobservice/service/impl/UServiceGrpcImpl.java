package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.u_service.grpc.BatchRenewalRequest;
import vn.com.fpt.jobservice.u_service.grpc.UServiceGrpc.UServiceBlockingStub;

@Slf4j
@Service
public class UServiceGrpcImpl {
    @GrpcClient("grpc-u-service")
    UServiceBlockingStub uServiceStubClient;

    public Object createBatchRenewalContract(Long ticketId, Long phaseId, Long subProcessId) {
        try {
            BatchRenewalRequest request = BatchRenewalRequest
                    .newBuilder()
                    .setTicketId(ticketId)
                    .setPhaseId(phaseId)
                    .setSubProcessId(subProcessId)
                    .build();

            return uServiceStubClient.createBatchRenewalContract(request);
        } catch (Exception e) {
            log.error("createBatchRenewalContract: ", e);
        }
        return new Object();
    }
}
