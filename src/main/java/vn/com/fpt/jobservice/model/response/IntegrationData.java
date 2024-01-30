package vn.com.fpt.jobservice.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IntegrationData {
  private IntegrationItem item;
  private IntegrationStructure structure;
}