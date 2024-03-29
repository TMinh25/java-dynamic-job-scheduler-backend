package vn.com.fpt.jobservice.utils.enums;

import java.util.HashMap;
import java.util.Map;

public enum TaskStatus {
    ERRORED("ERRORED"),
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    SUCCESS("SUCCESS"),
    CANCELED("CANCELED");

    TaskStatus(String status) {
        if (!status.equals(this.name()))
            throw new IllegalArgumentException();
    }

    private static final Map<String, TaskStatus> statusMap = new HashMap<>();
    static {
        for (TaskStatus status : TaskStatus.values()) {
            statusMap.put(status.name(), status);
        }
    }
    public static TaskStatus fromString(String status) {
        return statusMap.get(status);
    }
}
