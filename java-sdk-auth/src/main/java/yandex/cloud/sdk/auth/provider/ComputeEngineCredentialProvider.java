package yandex.cloud.sdk.auth.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.metadata.HttpConnectionConfig;
import yandex.cloud.sdk.auth.metadata.InstanceMetadataService;

import java.io.IOException;
import java.time.Instant;

/**
 * Retrieves {@link IamToken} of service account linked to a VM. Should be executed on Yandex.Cloud VM to work correctly.
 */
public class ComputeEngineCredentialProvider implements CredentialProvider {
    /**
     * This service is responsible for communicating with metadata server
     */
    private final InstanceMetadataService metadataService;

    private ComputeEngineCredentialProvider(InstanceMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Override
    public void close() {
        // Nothing
    }

    /**
     * Creates builder for <code>ComputeEngineCredentialProvider</code>
     *
     * @return {@link Builder} object
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * @return <code>IamToken</code> from VM metadata
     */
    @Override
    public IamToken get() {
        try {
            return extractToken(metadataService.getValue("instance/service-accounts/default/token"));
        } catch (IOException e) {
            throw new UnavailableIamTokenException(e);
        }
    }

    /**
     * Converts string from metadata to {@link IamToken} object
     *
     * @param content metadata entry content
     * @return <code>IamToken</code> object created from metadata response
     * @throws IOException if an I/O exception occurs during request
     */
    private IamToken extractToken(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TokenResponse response = objectMapper.readValue(content, TokenResponse.class);
        return new IamToken(response.accessToken, Instant.now().plusSeconds(response.expiresIn));
    }

    private static class TokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("token_type")
        private String tokenType;
    }

    public static class Builder extends AbstractCredentialProviderBuilder<Builder> {
        private InstanceMetadataService metadataService = new InstanceMetadataService();

        private Builder() {
        }

        /**
         * @param metadataServerUrl endpoint for metadata server
         * @return object itself for chained calls
         */
        public Builder metadataServerUrl(String metadataServerUrl) {
            this.metadataService = new InstanceMetadataService(HttpConnectionConfig.DEFAULT, metadataServerUrl);
            return this;
        }

        public Builder metadataService(InstanceMetadataService metadataService) {
            this.metadataService = metadataService;
            return this;
        }

        @Override
        protected CredentialProvider providerBuild() {
            return new ComputeEngineCredentialProvider(this.metadataService);
        }
    }
}
