package vn.com.fpt.jobservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Data
@Getter
@Setter
@Builder
public class LogModel {
    private Date time;
    private String content;
}
