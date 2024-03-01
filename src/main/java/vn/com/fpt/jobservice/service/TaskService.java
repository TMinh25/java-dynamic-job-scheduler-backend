package vn.com.fpt.jobservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;

import java.util.List;

public interface TaskService {
    PagedResponse<Task> searchTasks(Pageable pageable, String searchQuery);

    PagedResponse<Task> readAllTasks(Pageable pageable);

    Task readTaskById(String id);

    Task readTaskByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception;

    Boolean readActiveByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception;

    Task readTaskByJobUUID(String jobUUID);

    Task createTask(Task task) throws Exception;

    ResponseEntity<Object> deleteTaskById(String id);

    Task updateTaskById(String id, TaskModel task);

    Task updateTask(String id, TaskModel task);

    List<Task> getPendingTasks();

    Boolean scheduleTask(Task task) throws Exception;

    Boolean unscheduleTask(Task task);

    Boolean triggerJob(String taskId) throws Exception;

    ResponseEntity<Object> interuptJob(String taskId);
}
