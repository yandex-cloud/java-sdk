package yandex.cloud.sdk.auth.metadata;

/**
 * Signals that malformed URL has been found.
 */
public class MalformedURLException extends RuntimeException {
    private static final long serialVersionUID = -2080377029374713586L;
    /**
     * Malformed url that caused this exception
     */
    private final String url;

    public MalformedURLException(String url) {
        this.url = url;
    }

    public MalformedURLException(String url, Throwable cause) {
        super(cause);

        this.url = url;
    }

    @Override
    public String getMessage() {
        return "Malformed url: " + url;
    }
}
