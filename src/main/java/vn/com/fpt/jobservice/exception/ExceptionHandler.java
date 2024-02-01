package vn.com.fpt.jobservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import vn.com.fpt.jobservice.model.Error;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler  {
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public Mono<EntityResponse<String>> example(Exception exception) {
        return EntityResponse.fromObject("").status(HttpStatus.NOT_FOUND).build();
    }
//    @org.springframework.web.bind.annotation.ExceptionHandler({Exception.class})
//    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
//        Error apiError = new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), "error occurred");
//        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//    }
}
