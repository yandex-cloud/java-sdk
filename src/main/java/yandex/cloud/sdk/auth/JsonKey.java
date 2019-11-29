package yandex.cloud.sdk.auth;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import yandex.cloud.sdk.auth.jwt.Jwt;
import yandex.cloud.sdk.auth.jwt.JwtCreator;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class JsonKey implements Credentials {
    private final static Duration EXPIRATION_OFFSET = Duration.ofMinutes(1);

    private final ServiceAccountKey serviceAccountKey;
    private final JwtCreator jwtCreator;
    private volatile Jwt jwt = null;

    public JsonKey(String key) {
        jwtCreator = new JwtCreator();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            serviceAccountKey = objectMapper.readValue(key, ServiceAccountKey.class);
        } catch (JsonParseException | JsonMappingException e) {
            throw new InvalidJsonKeyException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServiceAccountKey getServiceAccountKey() {
        return serviceAccountKey;
    }

    public Jwt getJwt() {
        if (jwt == null || Instant.now().plus(EXPIRATION_OFFSET).isAfter(jwt.getExpireAt())) {
            jwt = jwtCreator.generateJwt(serviceAccountKey);
        }

        return jwt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JsonKey jsonKey = (JsonKey) o;
        return Objects.equals(serviceAccountKey, jsonKey.serviceAccountKey) &&
                Objects.equals(jwt, jsonKey.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceAccountKey, jwt);
    }

    @Override
    public String toString() {
        return String.format("JsonKey{" +
                "serviceAccountKey=" + serviceAccountKey +
                ", jwt='%s'" +
                '}', jwt != null ? "***" : "null");
    }
}
