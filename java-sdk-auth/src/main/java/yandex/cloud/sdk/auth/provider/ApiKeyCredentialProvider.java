package yandex.cloud.sdk.auth.provider;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.ManagedChannel;

import yandex.cloud.api.iam.v1.IamTokenServiceGrpc;
import yandex.cloud.api.iam.v1.IamTokenServiceOuterClass.CreateIamTokenRequest;
import yandex.cloud.api.iam.v1.IamTokenServiceOuterClass.CreateIamTokenResponse;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.apikey.ApiKey;
import yandex.cloud.sdk.auth.apikey.InvalidServiceAccountKeyException;
import yandex.cloud.sdk.auth.grpc.ManagedChannelFactory;
import yandex.cloud.sdk.auth.jwt.JwtCreator;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;
import yandex.cloud.sdk.auth.useragent.UserAgent;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

/**
 * Exchanges API key for IAM token.
 * API key is exchanged by generating JWT and then exchanging JWT to IAM token.
 */
public class ApiKeyCredentialProvider implements CredentialProvider {
    /**
     * Private key used to authenticate service accounts
     */
    private final ApiKey key;

    private final ManagedChannel channel;

    /**
     * Iam token service stub used to exchanges API key for IAM token
     */
    private final IamTokenServiceGrpc.IamTokenServiceBlockingStub iamTokenService;

    private ApiKeyCredentialProvider(ApiKey key, ManagedChannel channel) {
        this.key = key;
        this.channel = channel;
        this.iamTokenService = IamTokenServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void close() {
        channel.shutdown();
    }

    /**
     * Creates builder for <code>ApiKeyCredentialProvider</code>
     *
     * @return {@link Builder} object
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public IamToken get() {
        String jwt = key.getJwt().getToken();
        CreateIamTokenRequest request = CreateIamTokenRequest.newBuilder()
                .setJwt(jwt)
                .build();
        CreateIamTokenResponse response = iamTokenService.create(request);
        return new IamToken(response.getIamToken(), Instant.ofEpochSecond(response.getExpiresAt().getSeconds()));
    }

    public static class Builder extends AbstractCredentialProviderBuilder<Builder> {
        private ServiceAccountKey serviceAccountKey;
        private JwtCreator jwtCreator;
        private String endpoint = "iam.api.cloud.yandex.net:443";
        private String userAgent = UserAgent.DEFAULT;

        private Builder() {
        }

        public Builder serviceAccountKey(ServiceAccountKey serviceAccountKey) {
            this.serviceAccountKey = serviceAccountKey;
            return this;
        }

        public Builder jwtCreator(JwtCreator jwtCreator) {
            this.jwtCreator = jwtCreator;
            return this;
        }

        public Builder cloudIAMEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Creates a {@link ServiceAccountKey} object from its JSON representation
         *
         * @param json credentials data
         * @return object itself for chained calls
         */
        public Builder fromJson(String json) {
            serviceAccountKey = readKeyFromJson(json);
            return this;
        }

        /**
         * Reads environment variable content and creates a {@link ServiceAccountKey} object from its JSON representation
         *
         * @param env environment variable name for credentials data
         * @return object itself for chained calls
         */
        public Builder fromEnv(String env) {
            String json = System.getenv(env);
            if (Objects.isNull(json)) {
                throw new IllegalArgumentException(String.format("Environment variable %s is not set", env));
            }
            serviceAccountKey = readKeyFromJson(json);
            return this;
        }

        /**
         * Reads file content and creates a {@link ServiceAccountKey} object from its JSON representation
         *
         * @param path - path to a file with credentials data
         * @return object itself for chained calls
         */
        public Builder fromFile(Path path) {
            serviceAccountKey = readKeyFromJsonFile(path);
            return this;
        }

        @Override
        protected CredentialProvider providerBuild() {
            if (serviceAccountKey == null) {
                throw new IllegalStateException("build api key credential provider without service account key");
            }
            ApiKey apiKey = new ApiKey(serviceAccountKey);
            if (jwtCreator != null) {
                apiKey = apiKey.withJwtCreator(jwtCreator);
            }

            ManagedChannel managedChannel = ManagedChannelFactory.getInstance().newManagedChannel(endpoint, userAgent);
            return new ApiKeyCredentialProvider(apiKey, managedChannel);
        }

        private static ServiceAccountKey readKeyFromJsonFile(Path path) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(path.toFile(), ServiceAccountKey.class);
            } catch (JsonParseException | JsonMappingException e) {
                throw new InvalidServiceAccountKeyException(e);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Could not read file from path %s", path), e);
            }
        }

        private static ServiceAccountKey readKeyFromJson(String value) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return objectMapper.readValue(value, ServiceAccountKey.class);
            } catch (JsonParseException | JsonMappingException e) {
                throw new InvalidServiceAccountKeyException(e);
            } catch (IOException e) {
                throw new IllegalArgumentException("Could not convert json value to object", e);
            }
        }
    }


}
