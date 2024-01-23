package vn.com.fpt.jobservice.model.response;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class IntegrationStructure {
  @Autowired
  ObjectMapper objectMapper;

  private String tenantId;
  private Long integrationId;
  private String url;
  private String method;

  public String getMethod() {
    return method.toUpperCase();
  }

  @Nullable
  private Map<String, Object> params;
  @Nullable
  private Map<String, String> headers;
  @Nullable
  private Map<String, Object> body;
  @Nullable
  private Map<String, String> outputConfig;
  @Nullable
  private Map<String, String> mappingConfig;
  @Nullable
  private Map<String, Map<String, String>> auth;

  public String convertToJson() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage());
      return null;
    }
  }
}
