package yandex.cloud.sdk.auth.metadata;

/**
 * Signals that some error occurred during metadata retrieval
 */
public class MetadataException extends RuntimeException {
    /**
     * Metadata key that was failed to retrieve
     */
    private final String key;

    public MetadataException(String key) {
        this.key = key;
    }

    public MetadataException(String key, Throwable cause) {
        super(cause);
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "Failed to retrieve metadata value: " + key;
    }

    public String getKey() {
        return key;
    }
}
