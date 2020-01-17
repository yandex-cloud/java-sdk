package yandex.cloud.sdk.auth;

import java.util.Objects;

/**
 * OAuth token - long-lived token used to authenticate users.
 * @see <a href="https://cloud.yandex.com/docs/iam/concepts/authorization/oauth-token">https://cloud.yandex.com/docs/iam/concepts/authorization/oauth-token</a>
 * - OAuth token documentation
 */
public class OauthToken implements Credentials {
    /**
     * Stored OAuth token
     */
    private final String token;

    /**
     * Constructs <code>OauthToken</code> object from specified string
     * @param token OAuth token string representation
     */
    public OauthToken(String token) {
        this.token = token;
    }

    /**
     * @return OAuth token
     */
    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OauthToken that = (OauthToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return String.format("OauthToken{" +
                "token='%s'" +
                '}', token != null ? "***" : "null");
    }
}
