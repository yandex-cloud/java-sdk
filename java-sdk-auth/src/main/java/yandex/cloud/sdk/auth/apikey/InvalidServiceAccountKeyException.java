package yandex.cloud.sdk.auth.apikey;

/**
 * Signals that service account key is invalid. It can mean that JSON representation of a key is malformed.
 */
public class InvalidServiceAccountKeyException extends RuntimeException {
    private static final long serialVersionUID = 2554206034874573980L;
    public InvalidServiceAccountKeyException(Throwable cause) {
        super(cause);
    }
}
