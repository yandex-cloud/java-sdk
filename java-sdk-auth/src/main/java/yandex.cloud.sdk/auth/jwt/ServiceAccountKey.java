package yandex.cloud.sdk.auth.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

/**
 * Stores service account API-key info.
 * @see <a href="https://cloud.yandex.com/docs/iam/concepts/authorization/api-key">https://cloud.yandex.com/docs/iam/concepts/authorization/api-key</a>
 */
public class ServiceAccountKey {
    /**
     * id of service account key
     */
    private final String keyId;
    /**
     * id of service account
     */
    private final String serviceAccountId;
    /**
     * key creation time
     */
    private final String createdAt;
    /**
     * key creation algorithm
     */
    private final String keyAlgorithm;
    /**
     * public part of key
     */
    private final PublicKey publicKey;
    /**
     * private part of key
     */
    private final PrivateKey privateKey;

    /**
     * Constructor used to parse JSON structure with key
     * @param keyId ID of API-key
     * @param serviceAccountId ID of service account
     * @param createdAt time of key creation
     * @param keyAlgorithm algorithm used for public-private pair genearation (usually RSA_2048)
     * @param publicKey generated public key
     * @param privateKey generated private key
     */
    @JsonCreator
    public ServiceAccountKey(
            @JsonProperty("id") String keyId,
            @JsonProperty("service_account_id") String serviceAccountId,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("key_algorithm") String keyAlgorithm,
            @JsonProperty("public_key") String publicKey,
            @JsonProperty("private_key") String privateKey) {
        this.keyId = keyId;
        this.serviceAccountId = serviceAccountId;
        this.createdAt = createdAt;
        this.keyAlgorithm = keyAlgorithm;
        this.publicKey = SecurityUtils.createRsaPublicKey(publicKey);
        this.privateKey = SecurityUtils.createRsaPrivateKey(privateKey);
    }

    /**
     * @return ID of API-key
     */
    public String getKeyId() {
        return keyId;
    }

    /**
     * @return ID of service account
     */
    public String getServiceAccountId() {
        return serviceAccountId;
    }

    /**
     * @return time of key creation
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @return algorithm used for public-private pair genearation (usually RSA_2048)
     */
    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    /**
     * @return generated public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * @return generated private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceAccountKey that = (ServiceAccountKey) o;
        return Objects.equals(keyId, that.keyId) &&
                Objects.equals(serviceAccountId, that.serviceAccountId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(keyAlgorithm, that.keyAlgorithm) &&
                Objects.equals(publicKey, that.publicKey) &&
                Objects.equals(privateKey, that.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyId, serviceAccountId, createdAt, keyAlgorithm, publicKey, privateKey);
    }

    @Override
    public String toString() {
        return "ServiceAccountKey{" +
                "keyId='" + keyId + '\'' +
                ", serviceAccountId='" + serviceAccountId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", keyAlgorithm='" + keyAlgorithm + '\'' +
                ", publicKey=" + publicKey +
                '}';
    }
}
