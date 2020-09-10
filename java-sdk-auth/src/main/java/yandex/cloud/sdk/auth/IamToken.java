package yandex.cloud.sdk.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * IAM token - short-lived access token issued after authentification.
 *
 * @see <a href="https://cloud.yandex.com/docs/iam/concepts/authorization/iam-token">https://cloud.yandex.com/docs/iam/concepts/authorization/iam-token</a> - IAM token documentation
 */
public class IamToken {
    /**
     * Stored IAM token
     */
    private final String token;

    private final Instant expiresAt;

    private final Instant updateAt;

    /**
     * Constructs <code>IamToken</code> object from specified string
     *
     * @param token     IAM token string representation
     * @param expiresAt point in time when token will be expired
     */
    public IamToken(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;

        Instant now = Instant.now();
        long expiresIn = expiresAt.getEpochSecond() - now.getEpochSecond();
        Instant updateAt = now.plus((long) (expiresIn * 0.1), ChronoUnit.SECONDS);
        if (updateAt.isBefore(now)) {
            updateAt = now;
        }
        this.updateAt = updateAt;
    }

    /**
     * @return IAM token
     */
    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IamToken iamToken = (IamToken) o;
        return Objects.equals(token, iamToken.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return String.format("IamToken{" +
                "token='%s'" +
                '}', token != null ? "***" : "null");
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getUpdateAt() {
        return updateAt;
    }
}
