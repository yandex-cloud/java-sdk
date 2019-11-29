package yandex.cloud.sdk;

import yandex.cloud.sdk.auth.Credentials;

import java.time.Duration;

public class Config {
    static private final String DEFAULT_ENDPOINT = "api.cloud.yandex.net";
    static private final int DEFAULT_PORT = 443;
    static private final Duration DEFAULT_REQUEST_TIMEOUT = null;

    // Credentials are used to authenticate the client.
    private Credentials credentials;
    private String endpoint = DEFAULT_ENDPOINT;
    private int port = DEFAULT_PORT;
    private Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;

    private Config(Credentials credentials, String endpoint, int port, Duration requestTimeout) {
        this.credentials = credentials;

        if (endpoint != null && !"".equals(endpoint.trim())) {
            this.endpoint = endpoint.trim();
        }

        if (port > 0 && port < 65536) {
            this.port = port;
        } else {
            throw new IllegalArgumentException(String.format("Port should be between 1 and 65536, received: %s", port));
        }

        this.requestTimeout = requestTimeout;
    }

    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    public Credentials getCredentials() {
        return this.credentials;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public int getPort() {
        return this.port;
    }

    public Duration getRequestTimeout() {
        return this.requestTimeout;
    }

    @Override
    public String toString() {
        return "Config{" +
                "credentials=" + credentials +
                ", endpoint='" + endpoint + '\'' +
                ", port=" + port +
                ", requestTimeout=" + requestTimeout +
                '}';
    }

    public static class ConfigBuilder {
        private Credentials credentials;
        private String endpoint = DEFAULT_ENDPOINT;
        private int port = DEFAULT_PORT;
        private Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;

        ConfigBuilder() {
        }

        public Config.ConfigBuilder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        public Config.ConfigBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Config.ConfigBuilder port(int port) {
            this.port = port;
            return this;
        }

        public Config.ConfigBuilder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Config build() {
            return new Config(credentials, endpoint, port, requestTimeout);
        }

        @Override
        public String toString() {
            return "ConfigBuilder{" +
                    "credentials=" + credentials +
                    ", endpoint='" + endpoint + '\'' +
                    ", port=" + port +
                    ", requestTimeout=" + requestTimeout +
                    '}';
        }
    }
}
