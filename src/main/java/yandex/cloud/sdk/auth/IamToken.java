package yandex.cloud.sdk.auth;

import java.util.Objects;

/**
 * IAM token - short-lived access token issued after authentification.
 * @see <a href="https://cloud.yandex.com/docs/iam/concepts/authorization/iam-token">https://cloud.yandex.com/docs/iam/concepts/authorization/iam-token</a> - IAM token documentation
 */
public class IamToken implements Credentials {
    /**
     * Stored IAM token
     */
    private final String token;

    /**
     * Constructs <code>IamToken</code> object from specified string
     * @param token IAM token string representation
     */
    public IamToken(String token) {
        this.token = token;
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
}
