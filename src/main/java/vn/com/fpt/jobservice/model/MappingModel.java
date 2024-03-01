package vn.com.fpt.jobservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MappingModel {

    private boolean required;
    private String defaultValue;
    private String to;

}
