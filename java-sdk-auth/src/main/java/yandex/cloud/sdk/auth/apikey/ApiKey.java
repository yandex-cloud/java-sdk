package yandex.cloud.sdk.auth.apikey;

import yandex.cloud.sdk.auth.jwt.Jwt;
import yandex.cloud.sdk.auth.jwt.JwtCreator;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * API key - private key used to authenticate service accounts.
 *
 * @see <a href="https://cloud.yandex.com/docs/iam/concepts/authorization/api-key">https://cloud.yandex.com/docs/iam/concepts/authorization/api-key</a> - API key documentation
 */
public class ApiKey {
    /**
     * This offset defines JWT regeneration time (expiration time minus EXPIRATION_OFFSET)
     */
    private final static Duration EXPIRATION_OFFSET = Duration.ofMinutes(1);

    /**
     * Stored API key of service account
     */
    private final ServiceAccountKey serviceAccountKey;
    /**
     * {@link JwtCreator} generates JWT tokens for stored API key
     */
    private final JwtCreator jwtCreator;
    /**
     * Cached generated JWT
     */
    private volatile Jwt jwt = null;

    /**
     * Constructs <code>ApiKey</code> object from {@link ServiceAccountKey} object that contains API key data
     *
     * @param key <code>ServiceAccountKey</code> object that contains API key data
     */
    public ApiKey(ServiceAccountKey key) {
        this.serviceAccountKey = key;
        this.jwtCreator = new JwtCreator();
    }

    private ApiKey(ServiceAccountKey key, JwtCreator jwtCreator) {
        this.serviceAccountKey = key;
        this.jwtCreator = jwtCreator;
    }

    /**
     * Creates copy of this <code>ApiKey</code> object with given {@link JwtCreator}. Useful when <code>JwtCreator</code> with custom configuration is needed.
     *
     * @param jwtCreator custom <code>JwtCreator</code> object
     * @return copy of this <code>ApiKey</code> object with given <code>JwtCreator</code>
     */
    public ApiKey withJwtCreator(JwtCreator jwtCreator) {
        return new ApiKey(this.serviceAccountKey, jwtCreator);
    }

    /**
     * @return Stored API key of service account
     */
    public ServiceAccountKey getServiceAccountKey() {
        return serviceAccountKey;
    }

    /**
     * Returns cached JWT or generates a new one when it is soon to be expired
     *
     * @return JWT
     */
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
        ApiKey apiKey = (ApiKey) o;
        return Objects.equals(serviceAccountKey, apiKey.serviceAccountKey) &&
                Objects.equals(jwt, apiKey.jwt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serviceAccountKey, jwt);
    }

    @Override
    public String toString() {
        return String.format("ApiKey{" +
                "serviceAccountKey=" + serviceAccountKey +
                ", jwt='%s'" +
                '}', jwt != null ? "***" : "null");
    }
}
