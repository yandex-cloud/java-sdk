package yandex.cloud.sdk.functions.exception;

public class HeaderNotFoundException extends RuntimeException {
    private final String message;
    private final String type;

    public HeaderNotFoundException(final String header) {
        this.message = String.format("Header not found: %s. [ERR_HEADER_NOT_FOUND]", header);
        this.type = getClass().getSimpleName();
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}