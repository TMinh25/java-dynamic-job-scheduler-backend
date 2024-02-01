package vn.com.fpt.jobservice.jobs.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public abstract class BaseJob extends QuartzJobBean {
    public String jobName;
    public String type;

    protected void jobInfo(String msg) {
        log.info(String.format("[%s] %s", this.getClass().getName(), msg));
    }
}
