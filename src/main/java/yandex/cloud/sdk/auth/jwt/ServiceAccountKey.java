package yandex.cloud.sdk.auth.jwt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public class ServiceAccountKey {
    private final String keyId;
    private final String serviceAccountId;
    private final String createdAt;
    private final String keyAlgorithm;
    private final PublicKey publicKey;
    private final PrivateKey privateKey;

    @JsonCreator
    ServiceAccountKey(
            @JsonProperty("id") String keyId,
            @JsonProperty("service_account_id") String serviceAccountId,
            @JsonProperty("created_at") String createdAt,
            @JsonProperty("key_algorithm") String keyAlgorithm,
            @JsonProperty("public_key") String publicKey,
            @JsonProperty("private_key") String privateKey
    ) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        this.keyId = keyId;
        this.serviceAccountId = serviceAccountId;
        this.createdAt = createdAt;
        this.keyAlgorithm = keyAlgorithm;
        this.publicKey = SecurityUtils.createRsaPublicKey(publicKey);
        this.privateKey = SecurityUtils.createRsaPrivateKey(privateKey);
    }

    public String getKeyId() {
        return keyId;
    }

    public String getServiceAccountId() {
        return serviceAccountId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getKeyAlgorithm() {
        return keyAlgorithm;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

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

