package yandex.cloud.sdk.auth.jwt;

import java.time.Instant;
import java.util.Objects;

public class Jwt {
    private final String token;
    private final Instant expireAt;

    public Jwt(String token, Instant expireAt) {
        this.token = token;
        this.expireAt = expireAt;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jwt jwt = (Jwt) o;
        return Objects.equals(token, jwt.token) &&
                Objects.equals(expireAt, jwt.expireAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, expireAt);
    }

    @Override
    public String toString() {
        return String.format("Jwt{" +
                "token='%s'" +
                ", expireAt=" + expireAt +
                '}', token != null ? "***" : "null");
    }
}
