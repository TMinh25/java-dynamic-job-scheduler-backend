package vn.com.fpt.jobservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.JobModel;
import vn.com.fpt.jobservice.model.PagedResponse;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.repositories.InternalIntegrationRepository;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.JobService;
import vn.com.fpt.jobservice.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@CrossOrigin("*")
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
    public PagedResponse<Task> searchTasks(
            @RequestParam(value = "page", defaultValue = "0") int pageIndex,
            @RequestParam(value = "size", defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false, defaultValue = "") String searchQuery) {
        return taskService.searchTasks(PageRequest.of(pageIndex, pageSize), searchQuery);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Task createTask(@RequestBody TaskModel taskModel) throws Exception {
        Task task = taskModel.toEntity(ttRepository);
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

    @PutMapping("/{id}")
    public Task updateTaskById(@PathVariable(value = "id") String id, @RequestBody TaskModel taskModel)
            throws Exception {
        return taskService.updateTaskById(id, taskModel);
    }

    @PutMapping()
    public Task updateTaskByTicketAndPhase(
            @RequestParam(value = "ticketId", required = true) Long ticketId,
            @RequestParam(value = "phaseId", required = true) Long phaseId,
            @RequestBody TaskModel taskModel) throws Exception {
        Task task = taskService.readTaskByTicketIdAndPhaseId(ticketId, phaseId);
        return taskService.updateTaskById(task.getId(), taskModel);
    }

    @GetMapping("/jobs")
    public List<JobModel> getAllJobs() {
        return jobService.getAllJobs();
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

    @GetMapping("/interrupt/{id}")
    public ResponseEntity<Object> interruptJob(@PathVariable(value = "id") String id) {
        return taskService.interuptJob(id);
    }
}
