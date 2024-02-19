package vn.com.fpt.jobservice.model.response;


import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private int statusCode;
    private String messageCode;
    private String message;
    private ResponseData<T> responseData;
}

@Data
class ResponseData<T> {
    private int totalData;
    private List<T> data;
}