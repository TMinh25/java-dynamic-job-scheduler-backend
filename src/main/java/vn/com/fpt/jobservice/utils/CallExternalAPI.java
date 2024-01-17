package vn.com.fpt.jobservice.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class CallExternalAPI {

    public static JsonNode exchangeGet(String url, String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotEmpty(token)) {
                headers.setBearerAuth(token);
            }
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

    @SuppressWarnings("deprecation")
    public static JsonNode exchangePost(String url, String token, String requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ObjectMapper mapper = new ObjectMapper();
        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (StringUtils.isNotEmpty(token)) {
                headers.setBearerAuth(token);
            }
            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request,
                    new ParameterizedTypeReference<Object>() {
                    });
            log.warn("response: " + response.getBody());
            if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
                String json = mapper.writeValueAsString(response.getBody());
                JsonNode jsonBody = mapper.readTree(json);
                if (jsonBody.get("statusCodeValue") != null && jsonBody.get("statusCodeValue").asInt() != 200) {
                    String msgError = jsonBody.get("body") != null ? jsonBody.get("body").asText() : "System error";
                    return mapper.readTree("{\"error\":\"" + msgError + "\"}");
                } else {
                    return jsonBody;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                String json = "{\"error\":\"The connection to server failed. Please contact your system administrator.\"}";
                return mapper.readTree(json);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }
}
