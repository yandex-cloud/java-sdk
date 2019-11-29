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

public class ServiceFactory {
    static private final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private final Config config;
    private final ChannelFactory channelFactory;

    public ServiceFactory(Config config) {
        this.config = config;
        this.channelFactory = new ChannelFactory(config.getEndpoint(), config.getPort());
    }

    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service) {
        return create(clazz, service, null);
    }

    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(Class<SERVICE> clazz, Function<Channel, SERVICE> service, Duration timeout) {
        ManagedChannel channel = channelFactory.create(clazz);
        return create(channel, service, timeout);
    }

    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service) {
        return create(channel, service, null);
    }

    public <SERVICE extends io.grpc.stub.AbstractStub<SERVICE>> SERVICE create(ManagedChannel channel, Function<Channel, SERVICE> service, Duration timeout) {
        return service.apply(channel)
                .withCallCredentials(createCredsSupplier())
                .withInterceptors(createInterceptors(timeout));
    }

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
