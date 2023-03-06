package yandex.cloud.sdk.auth.provider;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import yandex.cloud.api.iam.v1.IamTokenServiceGrpc;
import yandex.cloud.sdk.auth.IamToken;
import yandex.cloud.sdk.auth.useragent.UserAgent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

import static yandex.cloud.api.iam.v1.IamTokenServiceOuterClass.CreateIamTokenRequest;
import static yandex.cloud.api.iam.v1.IamTokenServiceOuterClass.CreateIamTokenResponse;

/**
 * Exchanges OAuth token for IAM token.
 * OAuth token is exchanged by querying IamTokenService.
 */
public class OauthCredentialProvider implements CredentialProvider {
    private final String oauth;
    private final ManagedChannel channel;
    private final IamTokenServiceGrpc.IamTokenServiceBlockingStub iamTokenService;

    private OauthCredentialProvider(String oauth, ManagedChannel channel) {
        this.oauth = oauth;
        this.channel = channel;
        this.iamTokenService = IamTokenServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void close() {
        channel.shutdown();
    }

    /**
     * Creates builder for <code>OauthCredentialProvider</code>
     *
     * @return {@link Builder} object
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public IamToken get() {
        CreateIamTokenRequest request = CreateIamTokenRequest.newBuilder()
                .setYandexPassportOauthToken(oauth)
                .build();

        CreateIamTokenResponse response = iamTokenService.create(request);
        return new IamToken(response.getIamToken(), Instant.ofEpochSecond(response.getExpiresAt().getSeconds()));
    }

    public static class Builder extends AbstractCredentialProviderBuilder<Builder> {
        private String oauth;
        private String endpoint = "iam.api.cloud.yandex.net:443";
        private String userAgent = UserAgent.DEFAULT;

        private Builder() {
        }

        public Builder oauth(String oauth) {
            this.oauth = oauth;
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
         * Reads file content into variable oauth
         *
         * @param path path to a file with credentials data
         * @return object itself for chained calls
         */
        public Builder fromFile(Path path) {
            try {
                this.oauth = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Could not read file from path %s", path), e);
            }
            return this;
        }

        /**
         * Reads environment variable content into variable oauth
         *
         * @param env environment variable name for credentials data
         * @return object itself for chained calls
         */
        public Builder fromEnv(String env) {
            String token = System.getenv(env);
            if (Objects.isNull(token)) {
                throw new IllegalArgumentException(String.format("Environment variable %s is not set", env));
            }

            this.oauth = token;
            return this;
        }

        @Override
        protected CredentialProvider providerBuild() {
            if (oauth == null) {
                throw new IllegalStateException("build oauth credential provider without oauth token");
            }
            ManagedChannel channel = NettyChannelBuilder.forTarget(endpoint).userAgent(userAgent).build();
            return new OauthCredentialProvider(oauth, channel);
        }
    }


}
