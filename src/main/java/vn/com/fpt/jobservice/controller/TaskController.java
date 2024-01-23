package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.InternalIntegrationRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    TaskTypeRepository ttRepository;

    @Autowired
    InternalIntegrationRepository iiRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    JobService jobService;

    @GetMapping()
    public PagedResponse<Task> searchTasks(Pageable pageable,
            @RequestParam(value = "search", required = false, defaultValue = "") String searchQuery) {
        return taskService.searchTasks(pageable, searchQuery);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task createTask(@RequestBody TaskModel taskModel) throws Exception {
        Task task = taskModel.toEntity(ttRepository, iiRepository);
        return taskService.createTask(task);
    }

    @GetMapping("/{id}")
    public TaskModel readTaskById(@PathVariable(value = "id") String id) throws Exception {
        return taskService.readTaskById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTaskById(@PathVariable(value = "id") String id) throws Exception {
        return taskService.deleteTaskById(id);
    }

    @PatchMapping("/{id}")
    public Task updateTaskById(@PathVariable(value = "id") String id, @RequestBody TaskModel taskModel)
            throws Exception {
        return taskService.updateTaskById(id, taskModel);
    }

    @PatchMapping()
    public Task updateTaskByTicketAndPhase(
            @RequestParam(value = "ticketId", required = true) Long ticketId,
            @RequestParam(value = "phaseId", required = true) Long phaseId,
            @RequestBody TaskModel taskModel) throws Exception {
        Task task = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
        return taskService.updateTaskById(task.getId(), taskModel);
    }

    @GetMapping("/jobs")
    public List<Map<String, Object>> getAllJobs() {
        List<Map<String, Object>> list = jobService.getAllJobs();
        return list;
    }

    @GetMapping("/trigger/{id}")
    public ResponseEntity<Object> triggerJob(@PathVariable(value = "id") String id) throws Exception {
        return taskService.triggerJob(id);
    }

    @GetMapping("/trigger")
    public ResponseEntity<Object> triggerJobByPhase(
            @RequestParam(value = "ticketId", required = true) Long ticketId,
            @RequestParam(value = "phaseId", required = true) Long phaseId) throws Exception {
        Task task = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
        return taskService.triggerJob(task.getId());
    }

    @GetMapping("/interupt/{id}")
    public ResponseEntity<Object> interuptJob(@PathVariable(value = "id") String id) {
        return taskService.interuptJob(id);
    }
}
