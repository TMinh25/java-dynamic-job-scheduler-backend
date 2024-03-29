package vn.com.fpt.jobservice.utils.enums;

public enum TaskTypeType {
    SYSTEM("SYSTEM"),
    CUSTOM("CUSTOM"),
    INTEGRATION("INTEGRATION"),
    MANUAL("MANUAL");

    TaskTypeType(String type) {
        if (!type.equals(this.name()))
            throw new IllegalArgumentException();
    }
}
