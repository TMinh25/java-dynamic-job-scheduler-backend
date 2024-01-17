package vn.com.fpt.jobservice.model;

import jakarta.annotation.Nullable;
import lombok.Setter;

import java.util.Map;

@Setter
public class RequestDetailsModel {
    public String tenantId;
    public Long integrationId;
    public String url;
    public String method;
    @Nullable
    public Map<String, Object> params;
    @Nullable
    public Map<String, String> headers;
    @Nullable
    public Map<String, Object> body;
    @Nullable
    public Map<String, String> outputConfig;
    @Nullable
    public Map<String, String> mappingConfig;
    @Nullable
    public Map<String, Map<String, String>> auth;
}