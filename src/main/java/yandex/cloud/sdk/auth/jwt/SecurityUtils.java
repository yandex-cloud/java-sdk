package yandex.cloud.sdk.auth.jwt;

import com.google.protobuf.ByteString;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtils {
    public static final String RSA_ALGORITHM_FAMILY = "RSA";
    public static final String RSA_SHA384_ALGORITHM_NAME = "SHA384withRSA";

    public static PrivateKey createRsaPrivateKey(String keyPem)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        PemObject pemObject = new PemReader(new StringReader(keyPem)).readPemObject();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_FAMILY);
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey createRsaPublicKey(String keyPem)
            throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        PemObject pemObject = new PemReader(new StringReader(keyPem)).readPemObject();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM_FAMILY);
        return keyFactory.generatePublic(keySpec);
    }

    public static ByteString sign(PrivateKey privateKey, byte[] contentToSign)
            throws InvalidKeyException, SignatureException, NoSuchAlgorithmException{
        Signature signature = Signature.getInstance(RSA_SHA384_ALGORITHM_NAME);
        signature.initSign(privateKey);
        signature.update(contentToSign);
        return ByteString.copyFrom(signature.sign());
    }
}

