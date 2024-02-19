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
            TaskGrpc grpcTask = request.getTask();

            TaskModel taskModel = TaskModel.fromGrpc(grpcTask, taskTypeRepository);

            Task taskFound = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
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
            Task taskFound = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);

            boolean success = taskService.triggerJob(taskFound.getId());

            TaskTriggerResponse response = TaskTriggerResponse.newBuilder().setSuccess(success).build();

            return Mono.just(response);
        } catch (Exception e) {
            log.error("TaskServiceGrpcImpl triggerTaskByTicketIdAndPhaseId err(): ", e);
            return Mono.error(e);
        }
    }
}
