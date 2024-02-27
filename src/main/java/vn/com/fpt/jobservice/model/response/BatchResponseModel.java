package vn.com.fpt.jobservice.model.response;

import lombok.Data;

@Data
public class BatchResponseModel {
    private String messageCode;

    private String message;

    private Object data;
}
