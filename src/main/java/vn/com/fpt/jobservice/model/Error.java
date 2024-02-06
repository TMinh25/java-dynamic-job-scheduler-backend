package vn.com.fpt.jobservice.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class Error {
    @Getter
    private HttpStatus status;
    private String message;

    public Error(HttpStatus status, String message, List<String> errors) {
        super();
        this.status = status;
        this.message = message;
    }

    public Error(HttpStatus status, String message) {
        super();
        this.status = status;
        this.message = message;
    }
}
