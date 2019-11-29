package yandex.cloud.sdk.auth.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class JwtCreator {
    private static final String DEFAUL_ENDPOINT = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);

    private final String endpoint;
    private final Duration ttl;

    public JwtCreator() {
        this.endpoint = DEFAUL_ENDPOINT;
        this.ttl = DEFAULT_TTL;
    }

    public JwtCreator(JwtConfig config) {
        if (config.getEndpoint() != null) {
            this.endpoint = config.getEndpoint();
        } else {
            this.endpoint = DEFAUL_ENDPOINT;
        }

        if (config.getTtl() != null) {
            this.ttl = config.getTtl();
        } else {
            this.ttl = DEFAULT_TTL;
        }
    }

    public Jwt generateJwt(ServiceAccountKey serviceAccountKey) {
        return generateJwt(serviceAccountKey, ttl);
    }

    public Jwt generateJwt(ServiceAccountKey serviceAccountKey, Duration ttl) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.PS256;

        Instant curInstant = Instant.now();
        Instant exp = curInstant.plus(ttl);

        JwtBuilder builder = Jwts.builder()
                .setIssuer(serviceAccountKey.getServiceAccountId())
                .setIssuedAt(Date.from(curInstant))
                .setExpiration(Date.from(exp))
                .setAudience(endpoint)
                .signWith(serviceAccountKey.getPrivateKey(), signatureAlgorithm);

        builder.setHeaderParam("kid", serviceAccountKey.getKeyId());
        return new Jwt(builder.compact(), exp);
    }
}

