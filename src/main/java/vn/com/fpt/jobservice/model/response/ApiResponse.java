package vn.com.fpt.jobservice.model.response;


import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private int statusCode;
    private String messageCode;
    private String message;
    private ApiResponseData<T> responseData;
}
