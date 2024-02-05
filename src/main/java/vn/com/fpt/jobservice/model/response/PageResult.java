package vn.com.fpt.jobservice.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
public class PageResult<T> {
    private Long totalData;

    private List<T> data;

    public PageResult(Long totalData, List<T> data) {
        this.totalData = totalData;
        this.data = data;
    }

    public PageResult() {
        super();
        this.totalData = 0l;
        this.data = new ArrayList<>();
    }

    public PageResult(ArrayList<T> dataList) {
        super();
        this.data = dataList;
        this.totalData = (long) dataList.size();
    }
}
