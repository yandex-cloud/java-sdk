package yandex.cloud.sdk.auth.metadata;

/**
 * Signals that some error occurred during metadata retrieval
 */
public class MetadataException extends RuntimeException {
    private static final long serialVersionUID = -7429709782348361734L;
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
