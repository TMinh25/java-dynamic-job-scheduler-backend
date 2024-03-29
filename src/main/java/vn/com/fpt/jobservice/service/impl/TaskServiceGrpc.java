package vn.com.fpt.jobservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.TaskService;
import vn.com.fpt.jobservice.task_service.grpc.*;

@Slf4j
@GrpcService
public class TaskServiceGrpc extends ReactorTaskServiceGrpc.TaskServiceImplBase {
    @Autowired
    TaskService taskService;

    @Autowired
    TaskTypeRepository taskTypeRepository;

    @Override
    public Mono<TaskResponse> createTask(TaskCreateRequest request) {
        try {
            TaskGrpc grpcTask = request.getTask();
            TaskModel taskModel = TaskModel.fromGrpc(grpcTask, taskTypeRepository);
            Task task = taskModel.toEntity(taskTypeRepository);

            task = taskService.createTask(task);

            TaskGrpc taskGrpc = task.toGrpc();

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(taskGrpc)
                    .build();
            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl createTask err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskResponse> updateTaskByTaskId(TaskUpdateRequestByTaskId request) {
        try {
            String taskId = request.getId();
            TaskGrpc grpcTask = request.getTask();
            TaskModel taskModel = TaskModel.fromGrpc(grpcTask, taskTypeRepository);

            Task task = taskService.updateTaskById(taskId, taskModel);

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(task.toGrpc())
                    .build();
            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl updateTaskByTaskId err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskResponse> updateTaskByTicketIdAndPhaseId(TaskUpdateRequestByTicketIdAndPhaseId request) {
        try {
            Long ticketId = request.getTicketId();
            Long phaseId = request.getPhaseId();
            String tenantId = request.getTenantId();
            TaskGrpc grpcTask = request.getTask();

            TaskModel taskModel = TaskModel.fromGrpc(grpcTask, taskTypeRepository);

            Task taskFound = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
            Task task = taskService.updateTaskById(taskFound.getId(), taskModel);

            TaskResponse response = TaskResponse
                    .newBuilder()
                    .setTask(task.toGrpc())
                    .build();
            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl updateTaskByTicketIdAndPhaseId err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskTriggerResponse> triggerTaskByTaskId(TaskTriggerRequestByTaskId request) {
        try {
            boolean success = taskService.triggerJob(request.getId());

            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();

            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl triggerTaskByTaskId err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskTriggerResponse> triggerTaskByTicketIdAndPhaseId(TaskTriggerRequestByTicketIdAndPhaseId request) {
        try {
            Long ticketId = request.getTicketId();
            Long phaseId = request.getPhaseId();
            String tenantId = request.getTenantId();
            Task taskFound = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);

            boolean success = taskService.triggerJob(taskFound.getId());

            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();

            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl triggerTaskByTicketIdAndPhaseId err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskActiveResponse> readTaskActiveByTicketIdAndPhaseId(TaskTriggerRequestByTicketIdAndPhaseId request) {
        try {
            Long ticketId = request.getTicketId();
            Long phaseId = request.getPhaseId();
            String tenantId = request.getTenantId();
            Boolean taskActive = taskService.readActiveByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
            TaskActiveResponse response = TaskActiveResponse.newBuilder().setActive(taskActive).build();
            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl triggerTaskByTicketIdAndPhaseId err(): ", e);
            return Mono.error(e);
        }
    }

    @Override
    public Mono<TaskTriggerResponse> unscheduleTaskByTicketIdAndPhaseId(UnscheduleTaskRequest request) {
        try {
            String id = request.getId();
            long ticketId = request.getTicketId();
            long phaseId = request.getPhaseId();
            String tenantId = request.getTenantId();
            boolean isUpdate = request.getUpdate();

            Task task;

            if (!id.isEmpty()) {
                task = taskService.readTaskById(id);
            } else if (ticketId != 0 && phaseId != 0) {
                task = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
            } else {
                throw new IllegalArgumentException("Either 'id' or both 'ticketId' and 'phaseId' are required.");
            }
            Boolean success = taskService.unscheduleTask(task, isUpdate);
            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();
            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl triggerTaskByTicketIdAndPhaseId err(): ", e);
            return Mono.error(e);
        }
    }
}
