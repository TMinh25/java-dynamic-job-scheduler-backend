package vn.com.fpt.jobservice.utils;

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
}
