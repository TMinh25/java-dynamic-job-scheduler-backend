package vn.com.fpt.jobservice.model.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fpt.fis.integration.grpc.IntegrationService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import vn.com.fpt.jobservice.utils.Utils;

import java.util.Date;
import java.util.Map;

@Getter
@Setter
@Builder
@Slf4j
public class IntegrationItem {
    private Long id;
    private String tenantId;
    private String name;
    private String type;
    private String url;
    private String method;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, Object> body;
    private Map<String, String> outputConfig;
    private Map<String, String> mappingConfig;
    private Object auth;
    private String description;
    private String structure;

    private Date createdTime;
    private Date modifiedTime;
    private String createdBy;
    private String modifiedBy;

    public static IntegrationItem fromGrpcData(IntegrationService.IntegrationData i) throws JsonProcessingException {
        var STRING_MAP_TYPE_REF = new TypeReference<Map<String, String>>() {
        };
        var STRING_OBJECT_MAP_TYPE_REF = new TypeReference<Map<String, Object>>() {
        };
        var AUTH_MAP_TYPE_REF = new TypeReference<IntegrationAuthModel>() {
        };

        IntegrationItem.IntegrationItemBuilder itemBuilder = IntegrationItem.builder()
                .id(i.getId())
                .tenantId(i.getTenantId())
                .name(i.getName())
                .type(i.getType())
                .url(i.getUrl())
                .method(i.getMethod())
                .description(i.getDescription())
                .structure(i.getStructure());

        if (!i.getParams().isEmpty()) {
            itemBuilder.params(Utils.stringToObject(i.getParams(), STRING_MAP_TYPE_REF));
        }
        if (!i.getHeaders().isEmpty()) {
            itemBuilder.headers(Utils.stringToObject(i.getHeaders(), STRING_MAP_TYPE_REF));
        }
        if (!i.getBody().isEmpty()) {
            itemBuilder.body(Utils.stringToObject(i.getBody(), STRING_OBJECT_MAP_TYPE_REF));
        }
        if (!i.getOutputConfig().isEmpty()) {
            itemBuilder.outputConfig(Utils.stringToObject(i.getOutputConfig(), STRING_MAP_TYPE_REF));
        }
        if (!i.getMappingConfig().isEmpty()) {
            itemBuilder.mappingConfig(Utils.stringToObject(i.getMappingConfig(), STRING_MAP_TYPE_REF));
        }
        if (!i.getAuth().isEmpty()) {
            itemBuilder.auth(Utils.stringToObject(i.getAuth(), AUTH_MAP_TYPE_REF));
        }

        return itemBuilder.build();
    }
}
