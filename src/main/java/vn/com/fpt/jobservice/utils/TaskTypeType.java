package vn.com.fpt.jobservice.utils;

public enum TaskTypeType {
    SYSTEM("SYSTEM"),
    CUSTOM("CUSTOM"),
    INTEGRATION("INTEGRATION");

    TaskTypeType(String type) {
        if (!type.equals(this.name()))
            throw new IllegalArgumentException();
    }
}
