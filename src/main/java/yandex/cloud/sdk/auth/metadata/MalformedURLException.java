package yandex.cloud.sdk.auth.metadata;

public class MalformedURLException extends RuntimeException {
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
