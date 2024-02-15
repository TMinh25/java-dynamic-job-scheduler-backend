package vn.com.fpt.jobservice.utils;

public enum JobType {
    TICKET_CREATION("TICKET_CREATION"),
    SYNCHRONIZE_DATA("SYNCHRONIZE_DATA");

    JobType(String type) {
        if (!type.equals(this.name()))
            throw new IllegalArgumentException();
    }
}
