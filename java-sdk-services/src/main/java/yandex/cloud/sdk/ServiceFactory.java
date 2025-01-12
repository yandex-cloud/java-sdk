package yandex.cloud.sdk;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import yandex.cloud.sdk.auth.provider.CredentialProvider;
import yandex.cloud.sdk.grpc.interceptors.DataLoggingEnabledInterceptor;
import yandex.cloud.sdk.grpc.interceptors.DeadlineClientInterceptor;
import yandex.cloud.sdk.grpc.interceptors.RequestIdInterceptor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Factory class that creates gRPC stubs of given class.
 * It retrieves corresponding {@link ManagedChannel} object, adds authentication headers and required interceptors.
 */
public class ServiceFactory {
    static private final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final CredentialProvider credentialProvider;
    private final Duration requestTimeout;
    private final boolean dataLoggingEnabled;
    private final ChannelFactory channelFactory;

    private ServiceFactory(CredentialProvider credentialProvider, Duration requestTimeout, boolean dataLoggingEnabled, ChannelFactory channelFactory) {
        this.credentialProvider = credentialProvider;
        this.requestTimeout = requestTimeout;
        this.dataLoggingEnabled = dataLoggingEnabled;
        this.channelFactory = channelFactory;
    }

    /**
     * Creates builder for <code>ServiceFactory</code>
     *
     * @return {@link ServiceFactoryBuilder} object
     */
    public static ServiceFactoryBuilder builder() {
        return new ServiceFactoryBuilder();
    }

    /**
     * Creates gRPC stub of given class with no timeout
     *
     * @param clazz     gRPC stub class
     * @param service   function that will be used to create gRPC stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service) {
        return create(clazz, service, null);
    }

    /**
     * Creates gRPC stub of given class with given timeout
     *
     * @param clazz     gRPC stub class
     * @param service   function that will be used to create gRPC stub
     * @param timeout   timeout for gRPC calls that will be enforced on created stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service, Duration timeout) {
        ManagedChannel channel = channelFactory.getChannel(clazz);
        return create(channel, service, timeout);
    }

    /**
     * Creates gRPC stub of given class with no timeout
     *
     * @param channel   <code>ManagedChannel</code> used to create a stub
     * @param service   function that will be used to create gRPC stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service) {
        return create(channel, service, null);
    }

    /**
     * Creates gRPC stub of given class with given timeout
     *
     * @param channel   <code>ManagedChannel</code> used to create a stub
     * @param service   function that will be used to create gRPC stub
     * @param timeout   timeout for gRPC calls that will be enforced on created stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service, Duration timeout) {
        SERVICE serviceStub = service.apply(channel);
        serviceStub = serviceStub.withInterceptors(createInterceptors(timeout));
        if (credentialProvider != null) {
            serviceStub = serviceStub.withCallCredentials(createCallCredentials());
        }
        return serviceStub;
    }

    /**
     * Adds authentication headers to gRPC stub object
     *
     * @return {@link CallCredentials} object with authentication data from {@link this.credentialProvider}
     */
    private CallCredentials createCallCredentials() {
        return new CallCredentials() {

            @Override
            public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier applier) {
                String iamToken;
                try {
                    iamToken = credentialProvider.get().getToken();
                } catch (Exception e) {
                    applier.fail(Status.UNAUTHENTICATED.withDescription(e.getMessage()));
                    return;
                }

                Metadata metadata = new Metadata();
                metadata.put(AUTHORIZATION_KEY, "Bearer " + iamToken);
                applier.apply(metadata);
            }

            @Override
            public void thisUsesUnstableApi() {
            }
        };
    }

    /**
     * Adds required gRPC interceptors
     *
     * @param timeout timeout for gRPC calls that will be enforced on a stub
     * @return array of {@link ClientInterceptor} objects for a stub
     */
    private ClientInterceptor[] createInterceptors(Duration timeout) {
        List<ClientInterceptor> interceptors = new ArrayList<>();

        if (timeout != null) {
            interceptors.add(DeadlineClientInterceptor.fromDuration(timeout));
        } else if (requestTimeout != null) {
            interceptors.add(DeadlineClientInterceptor.fromDuration(requestTimeout));
        }
        interceptors.add(new RequestIdInterceptor());
        interceptors.add(new DataLoggingEnabledInterceptor(dataLoggingEnabled));

        return interceptors.toArray(new ClientInterceptor[0]);
    }

    public static class ServiceFactoryBuilder {
        /**
         * Default timeout for gRPC calls (no timeout)
         */
        private static final Duration DEFAULT_REQUEST_TIMEOUT = null;

        /**
         * Credentials to authenticate a client
         */
        private CredentialProvider credentialProvider;
        /**
         * Configured Yandex.Cloud API channel factory
         */
        private ChannelFactory channelFactory = new ChannelFactory(ChannelFactory.DEFAULT_ENDPOINT);
        /**
         * Configured default timeout for gRPC calls
         */
        private Duration requestTimeout = DEFAULT_REQUEST_TIMEOUT;

        private boolean dataLoggingEnabled = true;

        private ServiceFactoryBuilder() {
        }

        public ServiceFactoryBuilder credentialProvider(CredentialProvider credentialProvider) {
            this.credentialProvider = credentialProvider;
            return this;
        }

        public ServiceFactoryBuilder credentialProvider(CredentialProvider.Builder credentialProviderBuilder) {
            this.credentialProvider = credentialProviderBuilder.build();
            return this;
        }

        /**
         * @param endpoint Yandex.Cloud API endpoint
         * @return object itself for chained calls
         */
        public ServiceFactoryBuilder endpoint(String endpoint) {
            this.channelFactory = new ChannelFactory(endpoint);
            return this;
        }

        /**
         * @param channelFactory custom channel factory
         * @return object itself for chained calls
         */
        public ServiceFactoryBuilder channelFactory(ChannelFactory channelFactory) {
            this.channelFactory = channelFactory;
            return this;
        }

        /**
         * @param requestTimeout default timeout for gRPC calls
         * @return object itself for chained calls
         */
        public ServiceFactoryBuilder requestTimeout(Duration requestTimeout) {
            this.requestTimeout = requestTimeout;
            return this;
        }

        /**
         * @param dataLoggingEnabled Disabling request logging.
         * @return object itself for chained calls
         */
        public ServiceFactoryBuilder requestTimeout(boolean dataLoggingEnabled) {
            this.dataLoggingEnabled = dataLoggingEnabled;
            return this;
        }

        /**
         * Creates {@link ServiceFactory}
         *
         * @return {@link ServiceFactory} with specified parameters
         */
        public ServiceFactory build() {
            return new ServiceFactory(credentialProvider, requestTimeout, dataLoggingEnabled, channelFactory);
        }

        @Override
        public String toString() {
            return "ServiceFactoryConfigBuilder{" +
                    ", requestTimeout=" + requestTimeout +
                    '}';
        }
    }
}
