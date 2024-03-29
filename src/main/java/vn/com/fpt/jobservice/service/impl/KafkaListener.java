package vn.com.fpt.jobservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fpt.framework.kafka.consumer.annotation.ReactiveKafkaListener;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.fpt.jobservice.entity.Task;
import vn.com.fpt.jobservice.model.LogModel;
import vn.com.fpt.jobservice.model.TaskHistoryModel;
import vn.com.fpt.jobservice.model.TaskModel;
import vn.com.fpt.jobservice.model.response.KafkaMessageResponse;
import vn.com.fpt.jobservice.repositories.TaskTypeRepository;
import vn.com.fpt.jobservice.service.TaskHistoryService;
import vn.com.fpt.jobservice.service.TaskService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class KafkaListener {

    @Value("${kafka.enabled}")
    public Boolean useKafka;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskHistoryService taskHistoryService;

    @Autowired
    TaskTypeRepository ttRepository;

    @ReactiveKafkaListener(topics = "${kafka.topics.consumerJobServiceTopic}")
    public void listenMessage(String message) throws Exception {
        if (useKafka) {
            log.info("# Consumer start channel");
            try {
                Gson gson = new Gson();
                KafkaMessageResponse kafkaMessage = gson.fromJson(message, KafkaMessageResponse.class);

                String tenantId = kafkaMessage.getTenantId();
                Long ticketId = kafkaMessage.getTicketId();
                Long phaseId = kafkaMessage.getPhaseId();
                String type = kafkaMessage.getType();
                String jobUUID = kafkaMessage.getJobUUID();

                Task task;
                TaskModel taskModel;
                switch (type) {
                    case "addTaskJob":
                        taskModel = new TaskModel();
                        taskModel.setCronExpression(kafkaMessage.getCronExpression());
                        taskModel.setTaskTypeId(kafkaMessage.getTaskTypeId());
                        taskModel.setMaxRetries(kafkaMessage.getMaxRetries());
                        taskModel.setPhaseId(phaseId);
                        taskModel.setPhaseName(kafkaMessage.getPhaseName());
                        taskModel.setTicketId(ticketId);
                        taskModel.setSubProcessId(kafkaMessage.getSubProcessId());
                        taskModel.setTaskInputData(null);
                        taskModel.setTenantId(tenantId);
                        task = taskModel.toEntity(ttRepository);
                        taskService.createTask(task);
                        break;
                    case "triggerJob":
                        task = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
                        taskService.triggerJob(task.getId());
                        break;
                    case "unscheduleTask":
                        task = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
                        taskService.unscheduleTask(task, kafkaMessage.getIsUpdate());
                        break;
                    case "updateJob":
                        task = taskService.readTaskByTicketIdAndPhaseIdAndTenantId(ticketId, phaseId, tenantId);
                        taskModel = new TaskModel();
                        taskModel.setActive(kafkaMessage.getActive());
                        taskModel.setStatus(kafkaMessage.getStatus());
                        taskService.updateTaskById(task.getId(), taskModel);
                        break;
                    case "log":
                        task = taskService.readTaskByJobUUID(jobUUID);
                        handleLogMessage(task, kafkaMessage);
                        break;
                }
                log.debug("ConsumerMessageQueue successfully!");
            } catch (Exception ex) {
                log.debug("ConsumerMessageQueue Error: {}", ex.getMessage());
            }
        }
    }

    private void handleLogMessage(Task task, KafkaMessageResponse kafkaMessage) throws JsonProcessingException {
        TaskHistoryModel taskHistoryModel = taskHistoryService.findLatestByTaskId(task.getId()).toModel();
        List<Map<String, Object>> logList = taskHistoryModel.getLogs();

        if (!logList.isEmpty()) {
            // Remove the last log if it's "WAITING"
            Map<String, Object> lastLog = logList.get(logList.size() - 1);
            if (Objects.equals(lastLog.get("content"), LogModel.LOGS.WAITING.get())) {
                logList.remove(logList.size() - 1);
            }
        }

        // Add the new log and finish log
        LogModel newLog = LogModel.builder().time(new Date()).content(kafkaMessage.getMessage()).build();
        logList.add(newLog.toMap());
        LogModel finishLog = LogModel.builder().time(new Date()).content(LogModel.LOGS.FINISH.get()).build();
        logList.add(finishLog.toMap());

        // Update task history
        taskHistoryModel.setEndedAt(new Date());
        taskHistoryModel.setLogs(logList);
        taskHistoryService.updateLatestHistoryOfTask(task.getId(), taskHistoryModel.toEntity());
    }
}
