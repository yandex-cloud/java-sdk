package yandex.cloud.sdk.examples.kms;

import com.google.protobuf.ByteString;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceGrpc;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceGrpc.SymmetricCryptoServiceBlockingStub;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceOuterClass.SymmetricDecryptRequest;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceOuterClass.SymmetricDecryptResponse;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceOuterClass.SymmetricEncryptRequest;
import yandex.cloud.api.kms.v1.SymmetricCryptoServiceOuterClass.SymmetricEncryptResponse;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public class SymmetricCryptoExample {

    /**
     * pass your symmetric key id to run example
     *
     * @see https://cloud.yandex.ru/docs/kms/operations/key#create
     */
    private static final String MY_KMS_KEY_ID = "<symmetric-key-id>";

    public static void main(String[] args) {
        // Configuration
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();
        SymmetricCryptoServiceBlockingStub symmetricCryptoService = factory.create(SymmetricCryptoServiceBlockingStub.class, SymmetricCryptoServiceGrpc::newBlockingStub);

        String mySecretWord = "my_password";

        // encrypt secret
        SymmetricEncryptResponse encryptResponse = symmetricCryptoService.encrypt(buildEncryptRequest(mySecretWord));
        byte[] ciphertext = encryptResponse.getCiphertext().toByteArray();
        System.out.println(String.format(
                "My secret word \"%s\" encrypted with key %s version %s\nBase64 encoded ciphertext: %s",
                mySecretWord,
                encryptResponse.getKeyId(),
                encryptResponse.getVersionId(),
                Base64.getEncoder().encodeToString(ciphertext)));

        // decrypt ciphertext
        SymmetricDecryptResponse decryptResponse = symmetricCryptoService.decrypt(buildDecryptRequest(ciphertext));
        byte[] plaintext = decryptResponse.getPlaintext().toByteArray();
        String decryptedSecretWord = new String(plaintext, StandardCharsets.UTF_8);
        System.out.println(String.format("Decrypted secret word \"%s\"", decryptedSecretWord));
    }

    private static SymmetricDecryptRequest buildDecryptRequest(byte[] ciphertext) {
        return SymmetricDecryptRequest.newBuilder()
                .setKeyId(MY_KMS_KEY_ID)
                .setCiphertext(ByteString.copyFrom(ciphertext))
                .build();
    }

    private static SymmetricEncryptRequest buildEncryptRequest(String mySecretWord) {
        return SymmetricEncryptRequest.newBuilder()
                .setKeyId(MY_KMS_KEY_ID)
                .setPlaintext(ByteString.copyFrom(mySecretWord, StandardCharsets.UTF_8))
                .build();
    }
}
