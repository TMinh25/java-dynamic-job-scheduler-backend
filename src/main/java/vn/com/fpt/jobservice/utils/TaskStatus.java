package vn.com.fpt.jobservice.utils;

public enum TaskStatus {
    ERRORED(Constants.ERRORED),
    PENDING(Constants.PENDING),
    PROCESSING(Constants.PROCESSING),
    SUCCESS(Constants.SUCCESS);

    TaskStatus(String status) {
        if (!status.equals(this.name()))
            throw new IllegalArgumentException();
    }

    public static class Constants {
        public static final String ERRORED = "ERRORED";
        public static final String PENDING = "PENDING";
        public static final String PROCESSING = "PROCESSING";
        public static final String SUCCESS = "SUCCESS";
    }
}
