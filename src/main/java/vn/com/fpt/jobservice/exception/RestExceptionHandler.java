package vn.com.fpt.jobservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.context.request.WebRequest;
import vn.com.fpt.jobservice.model.Error;

@RestControllerAdvice
@Slf4j
class RestExceptionHandler {
    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> exception(Exception ex) {
        log.error(ex.getLocalizedMessage());
        ex.printStackTrace();
        Error apiError = new Error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
