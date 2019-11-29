package yandex.cloud.sdk.auth.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import yandex.cloud.sdk.auth.IamToken;

import java.io.IOException;

public class ServiceAccountTokenSupplier {
    private final InstanceMetadataService metadataService;

    public ServiceAccountTokenSupplier() {
        metadataService = new InstanceMetadataService();
    }

    public IamToken get() throws IOException {
        return extractToken(metadataService.getValue("instance/service-accounts/default/token"));
    }

    private IamToken extractToken(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse response = objectMapper.readValue(content, TokenResponse.class);
        return new IamToken(response.accessToken);
    }

    static class TokenResponse {
        @JsonProperty("access_token") private String accessToken;
        @JsonProperty("expires_in") private Long expires;
        @JsonProperty("token_type") private String tokenType;
    }
}
