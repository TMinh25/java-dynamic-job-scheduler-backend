package vn.com.fpt.jobservice.model.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponseData<T> {
    private int totalData;
    private List<T> data;
}
