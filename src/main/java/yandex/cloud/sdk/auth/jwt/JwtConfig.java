package yandex.cloud.sdk.auth.jwt;

import java.time.Duration;
import java.util.Objects;

public class JwtConfig {
    private final String endpoint;
    private final Duration ttl;

    public JwtConfig(String endpoint, Duration ttl) {
        this.endpoint = endpoint;
        this.ttl = ttl;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Duration getTtl() {
        return ttl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtConfig jwtConfig = (JwtConfig) o;
        return Objects.equals(endpoint, jwtConfig.endpoint) &&
                Objects.equals(ttl, jwtConfig.ttl);
    }

    @Override
    public String toString() {
        return "JwtConfig{" +
                "endpoint='" + endpoint + '\'' +
                ", ttl=" + ttl +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(endpoint, ttl);
    }

    public class JwtConfigBuilder {
        private String endpoint;
        private Duration ttl;

        public JwtConfigBuilder setEndpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public JwtConfigBuilder setTtl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        public JwtConfig createJwtConfig() {
            return new JwtConfig(endpoint, ttl);
        }
    }
}
