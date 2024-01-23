package vn.com.fpt.jobservice.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrationResponseData {
  private IntegrationItem item;
  private IntegrationStructure structure;
}