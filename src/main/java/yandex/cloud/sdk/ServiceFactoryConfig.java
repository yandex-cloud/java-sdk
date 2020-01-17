package yandex.cloud.sdk;

import yandex.cloud.sdk.auth.Credentials;

import java.time.Duration;

/**
 * Contains configuration for {@link ServiceFactory}.
 */
public class ServiceFactoryConfig {
    /**
     * Credentials to authenticate a client
     */
    private Credentials credentials;
    /**
     * Configured Yandex.Cloud API endpoint
     */
    private String endpoint;
    /**
     * Configured Yandex.Cloud API port
     */
    private int port;
    /**
     * Configured default timeout for gRPC calls
     */
    private Duration requestTimeout;

    /**
     * Constructs <code>ServiceFactoryConfig</code> with given credentials, endpoint, port and timeout
     * @param credentials credentials to authenticate a client
     * @param endpoint Yandex.Cloud API endpoint
     * @param port Yandex.Cloud API port
     * @param requestTimeout default timeout for gRPC calls
     */
    private ServiceFactoryConfig(Credentials credentials, String endpoint, int port, Duration requestTimeout) {
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

    /**
     * Creates builder for <code>ServiceFactoryConfig</code>
     * @return {@link ServiceFactoryConfigBuilder} object
     */
    public static ServiceFactoryConfigBuilder builder() {
        return new ServiceFactoryConfigBuilder();
    }

    /**
     * @return credentials to authenticate a client
     */
    public Credentials getCredentials() {
        return this.credentials;
    }

    /**
     * @return Yandex.Cloud API endpoint
     */
    public String getEndpoint() {
        return this.endpoint;
    }

    /**
     * @return Yandex.Cloud API port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return default timeout for gRPC calls
     */
    public Duration getRequestTimeout() {
        return this.requestTimeout;
    }

    @Override
    public String toString() {
        return "ServiceFactoryConfig{" +
                "credentials=" + credentials +
                ", endpoint='" + endpoint + '\'' +
                ", port=" + port +
                ", requestTimeout=" + requestTimeout +
                '}';
    }

    public static class ServiceFactoryConfigBuilder {
        private static final String DEFAULT_ENDPOINT = "api.cloud.yandex.net";
        /**
         * Default Yandex.Cloud API port
         */
        private static final int DEFAULT_PORT = 443;
        /**
         * Default timeout for gRPC calls (no timeout)
         */
        private static final Duration DEFAULT_REQUEST_TIMEOUT = null;

        /**
         * Credentials to authenticate a client
         */
        private Credentials credentials;
        /**
         * Configured Yandex.Cloud API endpoint
         */
        private String endpoint = DEFAULT_ENDPOINT;
        /**
         * Configured Yandex.Cloud API port
         */
        private int port = DEFAULT_PORT;
        /**
         * Configured default timeout for gRPC calls
         */
        private Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;

        private ServiceFactoryConfigBuilder() {}

        public ServiceFactoryConfigBuilder credentials(Credentials credentials) {
            this.credentials = credentials;
            return this;
        }

        /**
         * @param endpoint Yandex.Cloud API endpoint
         * @return object itself for chained calls
         */
        public ServiceFactoryConfigBuilder endpoint(String endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * @param port Yandex.Cloud API port
         * @return object itself for chained calls
         */
        public ServiceFactoryConfigBuilder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * @param requestTimeout default timeout for gRPC calls
         * @return object itself for chained calls
         */
        public ServiceFactoryConfigBuilder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * Creates {@link ServiceFactoryConfig}
         * @return {@link ServiceFactoryConfig} with specified parameters
         */
        public ServiceFactoryConfig build() {
            return new ServiceFactoryConfig(credentials, endpoint, port, requestTimeout);
        }

        @Override
        public String toString() {
            return "ServiceFactoryConfigBuilder{" +
                    "credentials=" + credentials +
                    ", endpoint='" + endpoint + '\'' +
                    ", port=" + port +
                    ", requestTimeout=" + requestTimeout +
                    '}';
        }
    }
}
