package vn.com.fpt.jobservice.model;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PagedResponse<T> {
  private final Page<T> page;

  public PagedResponse(Page<T> pagedEntity) {
    this.page = pagedEntity;
  }

  @JsonProperty("records")
  public List<T> getContent() {
    return this.page.getContent();
  }

  @JsonProperty("total")
  public long getTotalElements() {
    return page.getTotalElements();
  }

  @JsonProperty("currentPage")
  public int getNumber() {
    return page.getNumber();
  }

  @JsonProperty("totalPages")
  public int getTotalPages() {
    return page.getTotalPages();
  }
}
