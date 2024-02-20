package vn.com.fpt.jobservice.jobs;

import vn.com.fpt.jobservice.jobs.base.BaseJob;
import vn.com.fpt.jobservice.jobs.steps.ShowJobContext;

public class DataSynchronization extends BaseJob {
    @Override
    protected void defineSteps() {
        this.steps.add(new ShowJobContext(this));
    }
}
