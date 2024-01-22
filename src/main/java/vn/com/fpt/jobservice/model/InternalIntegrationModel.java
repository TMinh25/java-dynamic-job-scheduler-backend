package vn.com.fpt.jobservice.model;

import java.util.Map;

public class InternalIntegrationModel {
  private Long id;
  private String url;
  private String method;
  private Map<String, Object> params;
  private Map<String, String> headers;
  private Map<String, Object> body;
  private Map<String, String> outputConfig;
  private Map<String, String> mappingConfig;
  private Map<String, Map<String, String>> auth;
}
