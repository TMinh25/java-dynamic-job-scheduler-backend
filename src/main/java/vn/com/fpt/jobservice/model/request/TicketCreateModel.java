package vn.com.fpt.jobservice.model.request;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TicketCreateModel {

    private String id;

    private String title;

    private Long location;

    private List<String> informTo;

    private Map<String, Object> data;

    public static TicketCreateModel fromMap(Map<String, Object> map) {
        TicketCreateModel ticket = new TicketCreateModel();
        ticket.setId((String) map.get("id"));
        ticket.setTitle((String) map.get("title"));
        ticket.setLocation(Long.parseLong((String) map.get("location")));
        ticket.setInformTo((List<String>) map.get("informTo"));
        ticket.setData((Map<String, Object>) map.get("data"));
        return ticket;
    }
}
