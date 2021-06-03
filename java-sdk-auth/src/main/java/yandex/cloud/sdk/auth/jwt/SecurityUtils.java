package yandex.cloud.sdk.auth.jwt;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Helper functions to create RSA keys from string representation.
 */
public class SecurityUtils {
    /**
     * Algorithm used to decode keys (currently {@value RSA_ALGORITHM_FAMILY})
     */
    public static final String RSA_ALGORITHM_FAMILY = "RSA";

    private SecurityUtils() {/*noninstantiable helper class*/}

    /**
     * Creates RSA private key from its string representation
     * @param keyPem string representation of RSA private key
     * @return RSA private key
     * @throws RSAException if an exception occurs during creation of RSA public key
     */
    public static PrivateKey createRsaPrivateKey(String keyPem) {
        try {
            PemObject pemObject = new   PemReader(new StringReader(keyPem)).readPemObject();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_FAMILY);
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RSAException(e);
        }
    }

    /**
     * Creates RSA public key from its string representation
     * @param keyPem string representation of RSA public key
     * @return RSA public key
     * @throws RSAException if an exception occurs during creation of RSA public key
     */
    public static PublicKey createRsaPublicKey(String keyPem) {
        try (PemReader pemReader = new PemReader(new StringReader(keyPem))) {
            PemObject pemObject = pemReader.readPemObject();
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pemObject.getContent());
            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_FAMILY);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new RSAException(e);
        }
    }
}

