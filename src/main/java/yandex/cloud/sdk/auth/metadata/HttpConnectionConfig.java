package yandex.cloud.sdk.auth.metadata;

import java.util.Objects;

public class HttpConnectionConfig {
    public static final HttpConnectionConfig DEFAULT = new HttpConnectionConfig(500, 60 * 1000);

    private final int connectTimeoutMs;
    private final int readTimeoutMs;

    public HttpConnectionConfig(int connectTimeoutMs, int readTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

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
