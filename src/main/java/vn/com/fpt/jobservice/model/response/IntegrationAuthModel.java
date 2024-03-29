package vn.com.fpt.jobservice.model.response;

import lombok.Data;

@Data
public class IntegrationAuthModel {
    private String type;
    private Long integration;
    private String tokenField;
    private String token;
    private String username;
    private String password;
}