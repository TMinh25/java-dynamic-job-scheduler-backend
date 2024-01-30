package vn.com.fpt.jobservice.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;

public interface TaskService {
    PagedResponse<Task> searchTasks(Pageable pageable, String searchQuery);

    PagedResponse<Task> readAllTasks(Pageable pageable);

    TaskModel readTaskById(String id) throws Exception;

    Task readTaskByTicketIdAndPhaseId(Long ticketId, Long phaseId) throws Exception;

    Task readTaskByJobUUID(String jobUUID);

    Task createTask(Task task) throws Exception;

    ResponseEntity<Object> deleteTaskById(String id);

    Task updateTaskById(String id, TaskModel task);

    List<Task> getPendingTasks();

    boolean scheduleJob(Task task) throws Exception;

    ResponseEntity<Object> triggerJob(String id) throws Exception;

    ResponseEntity<Object> interuptJob(String id);
}
