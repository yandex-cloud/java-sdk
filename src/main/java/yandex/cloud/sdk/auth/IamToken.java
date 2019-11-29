package yandex.cloud.sdk.auth;

import java.util.Objects;

public class IamToken implements Credentials {
    private final String token;

    public IamToken(String token) {
        this.token = token;
    }

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
