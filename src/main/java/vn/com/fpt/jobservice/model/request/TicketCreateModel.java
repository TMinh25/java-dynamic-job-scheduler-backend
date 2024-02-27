package vn.com.fpt.jobservice.model.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TicketCreateModel {

    private String id;

    private String title;

    private Long location;

    private List<String> infromTo;

    private Map<String, Object> data;
}
