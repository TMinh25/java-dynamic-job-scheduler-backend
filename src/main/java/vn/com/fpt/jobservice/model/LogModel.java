package vn.com.fpt.jobservice.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Data
@Getter
@Setter
@Builder
public class LogModel {
    private Date time;
    private String content;

    public enum LOGS {
        START("Job executing..."),
        WAITING("Job waiting for message..."),
        FINISH("Job finished!");

        String message;

        LOGS(String message) {
            this.message = message;
        }

        public String get() {
            return this.message;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("time", time);
        map.put("content", content);
        return map;
    }
}
