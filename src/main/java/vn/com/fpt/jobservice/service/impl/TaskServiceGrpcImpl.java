package vn.com.fpt.jobservice.service.impl;

import io.grpc.Metadata;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.reflection.v1alpha.ErrorResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.task_service.grpc.TaskCreationRequest;
import vn.com.fpt.jobservice.task_service.grpc.TaskCreationResponse;
import vn.com.fpt.jobservice.task_service.grpc.TaskGrpc;
import vn.com.fpt.jobservice.task_service.grpc.TaskServiceGrpc.TaskServiceImplBase;
import vn.com.fpt.jobservice.utils.TaskStatus;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@GrpcService
public class TaskServiceGrpcImpl extends TaskServiceImplBase {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskTypeRepository taskTypeRepository;

    @Override
    public void createTask(TaskCreationRequest request, StreamObserver<TaskCreationResponse> responseObserver) {
        try {
            TaskGrpc grpcTask = request.getTask();
            TaskModel taskModel = convertToModel(grpcTask);
            Task task = taskModel.toEntity(taskTypeRepository);

            task = taskService.createTask(task);

            TaskCreationResponse response = TaskCreationResponse
                    .newBuilder()
                    .setTask(task.toGrpc())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            Metadata.Key<ErrorResponse> errorResponseKey = ProtoUtils.keyForProto(ErrorResponse.getDefaultInstance());
            Metadata metadata = new Metadata();

            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));

        }
    }

    private TaskModel convertToModel(TaskGrpc taskGrpc) {
        List<Object> taskInputData;
        if (!String.valueOf(taskGrpc.getTaskInputDataList()).equals("[]")) {
            taskInputData = Utils.convertRepeatedAny2List(taskGrpc.getTaskInputDataList());
        } else {
            taskInputData = new ArrayList<Object>();
        }
        return TaskModel.builder()
                .id(taskGrpc.getId())
                .name(taskGrpc.getName())
                .taskTypeId(taskGrpc.getTaskTypeId())
                .taskTypeId(taskGrpc.getTaskTypeId())
                .taskInputData(taskInputData)
                .integrationId(taskGrpc.getIntegrationId())
                .ticketId(taskGrpc.getTicketId())
                .phaseId(taskGrpc.getPhaseId())
                .phaseName(taskGrpc.getPhaseName())
                .subProcessId(taskGrpc.getSubProcessId())
                .retryCount(taskGrpc.getRetryCount())
                .maxRetries(taskGrpc.getMaxRetries())
                .status(TaskStatus.fromString(taskGrpc.getStatus()))
                .startStep(taskGrpc.getStartStep())
                .cronExpression(taskGrpc.getCronExpression())
                .active(taskGrpc.getActive())
                .nextInvocation(Utils.convertProtocTimestamp2Date(taskGrpc.getNextInvocation()))
                .prevInvocation(Utils.convertProtocTimestamp2Date(taskGrpc.getPrevInvocation()))
                .jobUUID(taskGrpc.getJobUUID())
                .createdAt(Utils.convertProtocTimestamp2Date(taskGrpc.getCreatedAt()))
                .modifiedAt(Utils.convertProtocTimestamp2Date(taskGrpc.getModifiedAt()))
                .createdBy(taskGrpc.getCreatedBy())
                .modifiedBy(taskGrpc.getModifiedBy())
                .build();
    }
}
