package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.exception.ResourceNotFoundException;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskTypeRepository ttRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    JobService jobService;

    final String TENANT_ID_HEADER = "Tenant-Id";

    @GetMapping()
    public PagedResponse<Task> searchTasks(@RequestHeader(TENANT_ID_HEADER) String tenantId, @RequestParam(value = "page", defaultValue = "1") int pageIndex, @RequestParam(value = "size", defaultValue = "10") int pageSize, @RequestParam(value = "search", required = false, defaultValue = "") String searchQuery) {
        return taskService.searchTasks(PageRequest.of(pageIndex - 1, pageSize), searchQuery, tenantId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task createTask(@RequestHeader(TENANT_ID_HEADER) String tenantId, @RequestBody TaskModel taskModel) throws Exception {
        taskModel.setTenantId(tenantId);
        Task task = taskModel.toEntity(ttRepository);
        return taskService.createTask(task);
    }

    @GetMapping("/read-status")
    public Boolean readStatusByTicketAndPhase(@RequestHeader(TENANT_ID_HEADER) String tenantId, @RequestParam(value = "ticketId", required = true) Long ticketId, @RequestParam(value = "phaseId", required = true) Long phaseId) throws Exception {
        return taskService.readActiveByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
    }

    @GetMapping("/{id}")
    public TaskModel readTaskById(@PathVariable(value = "id") String id) throws Exception {
        return taskService.readTaskById(id).toModel();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") String id) throws Exception {
        return taskService.deleteTaskById(id);
    }

    @GetMapping("/unschedule-task")
    public Boolean unscheduleTask(@RequestParam(value = "tenantId") String tenantId, @RequestParam(value = "ticketId", required = false) Long ticketId, @RequestParam(value = "phaseId", required = false) Long phaseId, @RequestParam(value = "id", required = false) String id, @RequestParam(value = "update", defaultValue = "false") Boolean isUpdate) throws Exception {
        try {
            Task task;
            isUpdate = false;
            if (id != null) {
                task = taskService.readTaskById(id);
            } else if (ticketId != null && phaseId != null) {
                task = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
            } else {
                throw new IllegalArgumentException("Either 'id' or both 'ticketId' and 'phaseId' are required.");
            }

            return taskService.unscheduleTask(task, isUpdate);
        } catch (ResourceNotFoundException e) {
            return true;
        }
    }

    @PutMapping("/{id}")
    public Task updateTaskById(@PathVariable(value = "id") String id, @RequestBody TaskModel taskModel) throws Exception {
        return taskService.updateTaskById(id, taskModel);
    }

//    @PutMapping()
//    public Task updateTaskByTicketAndPhase(@RequestParam(value = "ticketId", required = true) Long ticketId, @RequestParam(value = "phaseId", required = true) Long phaseId, @RequestBody TaskModel taskModel) throws Exception {
//        Task task = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
//        return taskService.updateTaskById(task.getId(), taskModel);
//    }


    @GetMapping("/trigger/{id}")
    public boolean triggerJob(@PathVariable(value = "id") String id) throws Exception {
        return taskService.triggerJob(id);
    }

//    @GetMapping("/trigger")
//    public boolean triggerJobByPhase(@RequestParam(value = "ticketId", required = true) Long ticketId, @RequestParam(value = "phaseId", required = true) Long phaseId) throws Exception {
//        Task task = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
//        return taskService.triggerJob(task.getId());
//    }

//    @GetMapping("/interrupt/{id}")
//    public ResponseEntity<Object> interruptJob(@PathVariable(value = "id") String id) {
//        return taskService.interuptJob(id);
//    }
}
