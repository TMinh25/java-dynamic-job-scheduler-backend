package vn.com.fpt.jobservice.model.response;

import java.util.Date;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IntegrationItem {
  private Long id;
  private String tenantId;
  private String name;
  private String type;
  private String url;
  private String method;
  private Map<String, String> params;
  private Map<String, String> headers;
  private Map<String, Object> body;
  private Map<String, String> outputConfig;
  private Map<String, String> mappingConfig;
  private Map<String, Map<String, String>> auth;
  private String description;
  private String structure;

  private Date createdTime;
  private Date modifiedTime;
  private String createdBy;
  private String modifiedBy;
}
