package yandex.cloud.sdk.auth.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import yandex.cloud.sdk.auth.IamToken;

import java.io.IOException;

/**
 * Retrieves {@link IamToken} of service account linked to a VM. Should be executed on Yandex.Cloud VM to work correctly.
 */
public class ServiceAccountTokenSupplier {
    /**
     * This service is responsible for communicating with metadata server
     */
    private final InstanceMetadataService metadataService;

    public ServiceAccountTokenSupplier() {
        metadataService = new InstanceMetadataService();
    }

    /**
     * @return <code>IamToken</code> from VM metadata
     * @throws IOException if an I/O exception occurs during request
     */
    public IamToken get() throws IOException {
        return extractToken(metadataService.getValue("instance/service-accounts/default/token"));
    }

    /**
     * Converts string from metadata to {@link IamToken} object
     * @param content metadata entry content
     * @return <code>IamToken</code> object created from metadata response
     * @throws IOException if an I/O exception occurs during request
     */
    private IamToken extractToken(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse response = objectMapper.readValue(content, TokenResponse.class);
        return new IamToken(response.accessToken);
    }

    private static class TokenResponse {
        @JsonProperty("access_token") private String accessToken;
        @JsonProperty("expires_in") private Long expires;
        @JsonProperty("token_type") private String tokenType;
    }
}
