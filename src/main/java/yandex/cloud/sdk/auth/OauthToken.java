package yandex.cloud.sdk.auth;

import java.util.Objects;

public class OauthToken implements Credentials {
    private final String token;

    public OauthToken(String token) {
        this.token = token;
    }

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
