package vn.com.fpt.jobservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Slf4j
public class CallExternalAPI {
    public static <T> T exchangeGet(String url, HttpHeaders headers, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    responseType);
            return response.getBody();
        } catch (Exception e) {
            throw e;
        }
    }

    public static <T> T exchangePost(String url, HttpHeaders headers, Object requestBody, Class<T> responseType) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType);
            return response.getBody();
        } catch (Exception e) {
            throw e;
        }
    }
}
