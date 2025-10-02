package yandex.cloud.sdk.auth.provider;

/**
 * Signals that error occured while trying to retrieve or exchange IAM token.
 */
public class UnavailableIamTokenException extends RuntimeException {
    private static final long serialVersionUID = -7760851849002652906L;
    public UnavailableIamTokenException(String message) {
        super(message);
    }

    public UnavailableIamTokenException(Throwable cause) {
        super(cause);
    }
}
