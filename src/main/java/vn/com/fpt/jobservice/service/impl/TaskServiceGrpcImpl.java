package vn.com.fpt.jobservice.service.impl;

import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.task_service.grpc.*;
import vn.com.fpt.jobservice.task_service.grpc.TaskServiceGrpc.TaskServiceImplBase;

@Slf4j
@GrpcService
public class TaskServiceGrpcImpl extends TaskServiceImplBase {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskTypeRepository taskTypeRepository;

    @Override
    public void createTask(TaskCreateRequest request, StreamObserver<TaskResponse> responseObserver) {
        try {
            TaskGrpc grpcTask = request.getTask();
            TaskModel taskModel = TaskModel.fromGrpc(grpcTask);
            Task task = taskModel.toEntity(taskTypeRepository);

            task = taskService.createTask(task);

            TaskGrpc taskGrpc = task.toGrpc();

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(taskGrpc)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));
        }
    }

    @Override
    public void updateTaskByTaskId(TaskUpdateRequestByTaskId request, StreamObserver<TaskResponse> responseObserver) {
        try {
            String taskId = request.getId();
            TaskGrpc grpcTask = request.getTask();
            TaskModel taskModel = TaskModel.fromGrpc(grpcTask);

            Task task = taskService.updateTaskById(taskId, taskModel);

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(task.toGrpc())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));
        }
    }

    @Override
    public void updateTaskByTicketIdAndPhaseId(TaskUpdateRequestByTicketIdAndPhaseId request, StreamObserver<TaskResponse> responseObserver) {
        try {
            Long ticketId = request.getTicketId();
            Long phaseId = request.getPhaseId();
            TaskGrpc grpcTask = request.getTask();

            TaskModel taskModel = TaskModel.fromGrpc(grpcTask);

            Task taskFound = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
            Task task = taskService.updateTaskById(taskFound.getId(), taskModel);

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(task.toGrpc())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));
        }
    }

    @Override
    public void triggerTaskByTaskId(TaskTriggerRequestByTaskId request, StreamObserver<TaskTriggerResponse> responseObserver) {
        try {
            boolean success = taskService.triggerJob(request.getId());

            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));
        }
    }

    @Override
    public void triggerTaskByTicketIdAndPhaseId(TaskTriggerRequestByTicketIdAndPhaseId request, StreamObserver<TaskTriggerResponse> responseObserver) {
        try {
            Long ticketId = request.getTicketId();
            Long phaseId = request.getPhaseId();
            Task taskFound = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);

            boolean success = taskService.triggerJob(taskFound.getId());

            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            Metadata metadata = new Metadata();
            responseObserver.onError(io.grpc.Status.INVALID_ARGUMENT.withDescription(e.getMessage())
                    .asRuntimeException(metadata));
        }
    }
}
