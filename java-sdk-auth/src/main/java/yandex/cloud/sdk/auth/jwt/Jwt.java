package yandex.cloud.sdk.auth.jwt;

import java.time.Instant;
import java.util.Objects;

/**
 * Stores JWT (JSON Web Token). Can be created by {@link JwtCreator}.
 * @see <a href="https://tools.ietf.org/html/rfc7519">https://tools.ietf.org/html/rfc7519</a> - JWT specification
 */
public class Jwt {
    /**
     * JWT
     */
    private final String token;
    /**
     * Expiration time of JWT stored in <code>token</code>
     */
    private final Instant expireAt;

    /**
     * Constructs <code>Jwt</code> with specified token and expiration time
     *
     * @param token JWT
     * @param expireAt expiration time
     */
    public Jwt(String token, Instant expireAt) {
        this.token = token;
        this.expireAt = expireAt;
    }

    /**
     * @return stored JWT
     */
    public String getToken() {
        return token;
    }

    /**
     * @return expiration time of stored JWT
     */
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
