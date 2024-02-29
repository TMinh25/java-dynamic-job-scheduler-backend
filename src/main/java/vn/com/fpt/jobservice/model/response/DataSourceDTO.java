package vn.com.fpt.jobservice.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataSourceDTO {
    private String id;
    private String name;
}
