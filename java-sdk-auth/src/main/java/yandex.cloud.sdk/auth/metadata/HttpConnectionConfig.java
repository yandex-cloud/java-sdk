package yandex.cloud.sdk.auth.metadata;

import java.util.Objects;

/**
 * Configuration class for {@link java.net.HttpURLConnection} that is used in {@link InstanceMetadataService}.
 */
public class HttpConnectionConfig {
    /**
     * Default configuration
     */
    public static final HttpConnectionConfig DEFAULT = new HttpConnectionConfig(500, 60 * 1000);

    /**
     * Timeout on connect
     */
    private final int connectTimeoutMs;
    /**
     * Timeout on read
     */
    private final int readTimeoutMs;

    /**
     * Constructs a <code>HttpConnectionConfig</code> with given connect and read timeouts
     * @param connectTimeoutMs connect timeout in milliseconds
     * @param readTimeoutMs read timeout in milliseconds
     */
    public HttpConnectionConfig(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    /**
     * @return timeout on connect
     */
    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    /**
     * @return timeout on read
     */
    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpConnectionConfig that = (HttpConnectionConfig) o;
        return connectTimeoutMs == that.connectTimeoutMs &&
                readTimeoutMs == that.readTimeoutMs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectTimeoutMs, readTimeoutMs);
    }

    @Override
    public String toString() {
        return "HttpConnectionConfig{" +
                "connectTimeoutMs=" + connectTimeoutMs +
                ", readTimeoutMs=" + readTimeoutMs +
                '}';
    }
}
