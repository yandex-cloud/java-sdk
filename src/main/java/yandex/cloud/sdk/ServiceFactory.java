package yandex.cloud.sdk;

import io.grpc.CallCredentials;
import io.grpc.Channel;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import yandex.cloud.sdk.auth.Auth;
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

    private final ServiceFactoryConfig config;
    private final ChannelFactory channelFactory;

    public ServiceFactory(ServiceFactoryConfig config) {
        this.config = config;
        this.channelFactory = new ChannelFactory(config.getEndpoint(), config.getPort());
    }

    /**
     * Creates gRPC stub of given class with no timeout
     * @param clazz gRPC stub class
     * @param service function that will be used to create gRPC stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service) {
        return create(clazz, service, null);
    }

    /**
     * Creates gRPC stub of given class with given timeout
     * @param clazz gRPC stub class
     * @param service function that will be used to create gRPC stub
     * @param timeout timeout for gRPC calls that will be enforced on created stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service, Duration timeout) {
        ManagedChannel channel = channelFactory.getChannel(clazz);
        return create(channel, service, timeout);
    }

    /**
     * Creates gRPC stub of given class with no timeout
     * @param channel <code>ManagedChannel</code> used to create a stub
     * @param service function that will be used to create gRPC stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service) {
        return create(channel, service, null);
    }

    /**
     * Creates gRPC stub of given class with given timeout
     * @param channel <code>ManagedChannel</code> used to create a stub
     * @param service function that will be used to create gRPC stub
     * @param timeout timeout for gRPC calls that will be enforced on created stub
     * @param <SERVICE> indicates a gRPC stub class
     * @return gRPC stub of given class
     */
    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service, Duration timeout) {
        return service.apply(channel)
                .withCallCredentials(createCredsSupplier())
                .withInterceptors(createInterceptors(timeout));
    }

    /**
     * Adds authentication headers to gRPC stub object
     * @return {@link CallCredentials} object with authentication data from {@link ServiceFactory#config}
     */
    private CallCredentials createCredsSupplier() {
        return new CallCredentials() {

            @Override
            public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier applier) {
                String iamToken;
                try {
                    iamToken = Auth.exchangeToken(config.getCredentials(), channelFactory).getToken();
                } catch (StatusRuntimeException e) {
                    applier.fail(e.getStatus());
                    return;
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
     * @param timeout timeout for gRPC calls that will be enforced on a stub
     * @return array of {@link ClientInterceptor} objects for a stub
     */
    private ClientInterceptor[] createInterceptors(Duration timeout) {
        List<ClientInterceptor> interceptors = new ArrayList<>();

        if (timeout != null) {
            interceptors.add(DeadlineClientInterceptor.fromDuration(timeout));
        } else if (config.getRequestTimeout() != null) {
            interceptors.add(DeadlineClientInterceptor.fromDuration(config.getRequestTimeout()));
        }
        interceptors.add(new RequestIdInterceptor());

        return interceptors.toArray(new ClientInterceptor[0]);
    }
}
