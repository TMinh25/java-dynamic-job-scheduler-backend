package vn.com.fpt.jobservice.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntegrationResponse {
  public Integer statusCode;
  public Integer messageCode;
  public String message;
  public IntegrationResponseData responseData;
}