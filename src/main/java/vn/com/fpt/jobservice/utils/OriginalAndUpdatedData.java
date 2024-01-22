package vn.com.fpt.jobservice.utils;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OriginalAndUpdatedData {
  public Map<String, Object> oldData;
  public Map<String, Object> newData;

  public OriginalAndUpdatedData(Map<String, Object> oldData, Map<String, Object> newData) {
    this.oldData = oldData;
    this.newData = newData;
  }
}