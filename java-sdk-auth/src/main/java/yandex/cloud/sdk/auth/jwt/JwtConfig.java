package yandex.cloud.sdk.auth.jwt;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuration class for {@link JwtCreator}.
 */
public class JwtConfig {
    /**
     * Endpoint value used in generated JWT audience field
     */
    private final String endpoint;
    /**
     * TTL value for generated JWT
     */
    private final Duration ttl;

    /**
     * Constructs a <code>JwtConfig</code> with specified endpoint and TTL
     * @param endpoint value used in generated JWT audience field
     * @param ttl TTL of generated JWT
     */
    private JwtConfig(String endpoint, Duration ttl) {
        this.endpoint = endpoint;
        this.ttl = ttl;
    }

    /**
     * Creates builder for <code>JwtConfig</code>
     * @return {@link JwtConfigBuilder} object
     */
    public static JwtConfigBuilder builder() {
        return new JwtConfigBuilder();
    }

    /**
     * @return endpoint value used in generated JWT audience field
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * @return TTL of generated JWT
     */
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

    /**
     * Builder class for {@link JwtConfig}.
     */
    public static class JwtConfigBuilder {
        /**
         * Endpoint value used in generated JWT audience field
         */
        private String endpoint;
        /**
         * TTL value for generated JWT
         */
        private Duration ttl;

        private JwtConfigBuilder() {}

        /**
         * @param endpoint endpoint value used in generated JWT audience field
         * @return object itself for chained calls
         */
        public JwtConfigBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * @param ttl TTL value for generated JWT
         * @return object itself for chained calls
         */
        public JwtConfigBuilder ttl(Duration ttl) {
            this.ttl = ttl;
            return this;
        }

        /**
         * Creates {@link JwtConfig}
         * @return {@link JwtConfig} with specified endpoint and TTL
         */
        public JwtConfig build() {
            return new JwtConfig(endpoint, ttl);
        }
    }
}
