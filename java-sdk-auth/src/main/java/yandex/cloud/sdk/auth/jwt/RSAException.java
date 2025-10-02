package yandex.cloud.sdk.auth.jwt;

/**
 * Signals that RSA key creation has failed.
 */
public class RSAException extends RuntimeException {
    private static final long serialVersionUID = 5445323889184953110L;

    public RSAException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "RSA key creation failed";
    }
}
