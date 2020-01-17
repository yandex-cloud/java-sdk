package yandex.cloud.sdk.auth;

/**
 * Signals that service account key is invalid. It can mean that JSON representation of a key is malformed.
 */
public class InvalidServiceAccountKeyException extends RuntimeException {
    public InvalidServiceAccountKeyException(Throwable cause) {
        super(cause);
    }
}
