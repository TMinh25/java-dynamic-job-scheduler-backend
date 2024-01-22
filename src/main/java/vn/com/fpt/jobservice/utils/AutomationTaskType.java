package vn.com.fpt.jobservice.utils;

public enum AutomationTaskType {
    BatchRenewalContract(Constants.BatchRenewalContract);

    AutomationTaskType(String jobName) {
        // if (!jobName.equals(this.name()))
        // throw new IllegalArgumentException();
    }

    public static class Constants {
        public static final String BatchRenewalContract = "BatchRenewalContract";
    }
}
