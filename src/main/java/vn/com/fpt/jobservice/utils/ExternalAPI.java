package vn.com.fpt.jobservice.utils;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExternalAPI {
  @Autowired
  RestTemplate restTemplate;

  public JsonNode exchangeGet(String url, HttpHeaders headers) {
    try {
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
      ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
          new ParameterizedTypeReference<Object>() {
          });
      ObjectMapper mapper = new ObjectMapper();
      if (response.getBody() != null) {
        String json = mapper.writeValueAsString(response.getBody());
        return mapper.readTree(json);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
    return null;
  }
}
