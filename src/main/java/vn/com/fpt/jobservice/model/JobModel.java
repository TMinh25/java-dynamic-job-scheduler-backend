package vn.com.fpt.jobservice.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Setter
@Getter
@Data
public class JobModel {
    private String jobName;
    private String groupName;
    private String jobStatus;
    private Date scheduleTime;
    private Date lastFiredTime;
    private Date nextFireTime;
}
