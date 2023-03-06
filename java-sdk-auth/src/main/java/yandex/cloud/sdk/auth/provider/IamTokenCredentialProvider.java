package yandex.cloud.sdk.auth.provider;

import yandex.cloud.sdk.auth.IamToken;

import java.time.Duration;
import java.time.Instant;

/**
 * Returns stored IAM token as-is.
 */
public class IamTokenCredentialProvider implements CredentialProvider {
    private final IamToken iamToken;

    private IamTokenCredentialProvider(IamToken iamToken) {
        this.iamToken = iamToken;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        // Nothing
    }

    @Override
    public IamToken get() {
        return iamToken;
    }

    public static class Builder implements CredentialProvider.Builder {
        private static final Duration DEFAULT_IAM_TOKEN_TTL = Duration.ofHours(1);
        private IamToken iamToken;

        private Builder() {
        }

        public Builder token(IamToken iamToken) {
            this.iamToken = iamToken;
            return this;
        }

        public Builder token(String token) {
            this.iamToken = new IamToken(token, Instant.now().plus(DEFAULT_IAM_TOKEN_TTL));
            return this;
        }

        @Override
        public CredentialProvider build() {
            if (iamToken == null) {
                throw new IllegalStateException("build iam token credential provider without iam token");
            }
            return new IamTokenCredentialProvider(iamToken);
        }
    }
}
