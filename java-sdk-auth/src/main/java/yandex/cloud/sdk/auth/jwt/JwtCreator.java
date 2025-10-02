package yandex.cloud.sdk.auth.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Generates JWT stored in {@link Jwt} object. Issuer of JWT is service account (id is used).
 * Token is signed by service account's private key using PS256 algorithm.
 * <br>
 * Audience and TTL can be specified using {@link JwtConfig}. Default audience is {@value DEFAULT_ENDPOINT}
 * and default TTL is 1 hour
 */
public class JwtCreator {
    /**
     * Default endpoint value used in generated JWT audience field
     */
    private static final String DEFAULT_ENDPOINT = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
    /**
     * Default TTL value for generated JWT
     */
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    /**
     * Endpoint value used in generated JWT audience field
     */
    private final String endpoint;
    /**
     * TTL value for generated JWT
     */
    private final Duration ttl;

    /**
     * Constructs a <code>JwtCreator</code> with default configuration
     */
    public JwtCreator() {
        this.endpoint = DEFAULT_ENDPOINT;
        this.ttl = DEFAULT_TTL;
    }

    /**
     * Constructs a <code>JwtCreator</code> with provided configuration
     * @param config configures endpoint and default TTL for generated JWTs
     */
    public JwtCreator(JwtConfig config) {
        if (config.getEndpoint() != null) {
            this.endpoint = config.getEndpoint();
        } else {
            this.endpoint = DEFAULT_ENDPOINT;
        }

        if (config.getTtl() != null) {
            this.ttl = config.getTtl();
        } else {
            this.ttl = DEFAULT_TTL;
        }
    }

    /**
     * Generates a JWT for specified service account with default TTL
     * @param serviceAccountKey key of service account. Service account acts as an issuer of generated token.
     * @return generated JWT
     */
    public Jwt generateJwt(ServiceAccountKey serviceAccountKey) {
        return generateJwt(serviceAccountKey, ttl);
    }

    /**
     * Generates a JWT for specified service account with specified TTL
     * @param serviceAccountKey key of service account. Service account acts as an issuer of generated token.
     * @param ttl TTL of generated JWT
     * @return generated JWT
     */
    public Jwt generateJwt(ServiceAccountKey serviceAccountKey, Duration ttl) {
        SignatureAlgorithm signatureAlgorithm = Jwts.SIG.PS256;

        Instant curInstant = Instant.now();
        Instant exp = curInstant.plus(ttl);

        JwtBuilder builder = Jwts.builder()
                .issuer(serviceAccountKey.getServiceAccountId())
                .issuedAt(Date.from(curInstant))
                .expiration(Date.from(exp))
                .audience().add(endpoint).and()
                .signWith(serviceAccountKey.getPrivateKey(), signatureAlgorithm);

        builder = builder.header().add("kid", serviceAccountKey.getKeyId()).and();
        return new Jwt(builder.compact(), exp);
    }
}

