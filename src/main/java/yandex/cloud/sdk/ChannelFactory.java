package yandex.cloud.sdk;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractStub;
import yandex.cloud.api.endpoint.ApiEndpointOuterClass.ApiEndpoint;
import yandex.cloud.api.endpoint.ApiEndpointServiceGrpc;
import yandex.cloud.api.endpoint.ApiEndpointServiceOuterClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Factory class that creates {@link ManagedChannel} objects that corresponds to given gRPC stubs.
 */
public class ChannelFactory {
    private static final String DEFAULT_ENDPOINT = "api.cloud.yandex.net";
    private static final int DEFAULT_PORT = 443;

    /**
     * Service name to endpoint mapping.
     */
    private final Map<String, String> endpointMap;
    /**
     * Cached <code>ManagedChannel</code> objects that are reused for gRPC stubs of the same service
     */
    private final Map<String, ManagedChannel> channelCache;

    /**
     * Constructs <code>ChannelFactory</code> object for given endpoint and port.
     * Uses default service to endpoint mapping of default endpoint and port are given.
     * Otherwise, queries endpoint service to obtain actual service to endpoint mapping.
     * @param endpoint Yandex.Cloud API endpoint
     * @param port Yandex.Cloud API port
     */
    public ChannelFactory(String endpoint, int port) {
        if (DEFAULT_ENDPOINT.equals(endpoint) && DEFAULT_PORT == port) {
            this.endpointMap = ServiceToEndpointMapping.map;
        } else {
            this.endpointMap = createEndpointMapping(endpoint, port);
        }
        this.channelCache = new ConcurrentHashMap<>();
    }

    /**
     * Queries endpoint service and creates a service to endpoint mapping from the response.
     * @param endpoint Yandex.Cloud API endpoint
     * @param port Yandex.Cloud API port
     * @return service to endpoint mapping
     */
    private Map<String, String> createEndpointMapping(String endpoint, int port) {
        ApiEndpointServiceOuterClass.ListApiEndpointsResponse response;
        ManagedChannel channel = null;

        try {
            channel = NettyChannelBuilder.forAddress(endpoint, port).build();
            ApiEndpointServiceGrpc.ApiEndpointServiceBlockingStub stub = ApiEndpointServiceGrpc.newBlockingStub(channel);
            response = stub.list(ApiEndpointServiceOuterClass.ListApiEndpointsRequest.newBuilder().build());

        } catch (StatusRuntimeException e) {
            throw new ChannelFactoryCreationException("Cannot retrieve endpoint list", e);
        } finally {
            if (channel != null) {
                channel.shutdown();
            }
        }

        return response.getEndpointsList().stream()
                .collect(Collectors.toMap(ApiEndpoint::getId, ApiEndpoint::getAddress));
    }

    /**
     * Provides a <code>ManagedChannel</code> object that corresponds to given gRPC stub.
     * Resolves gRPC stub class to endpoint and returns cached <code>ManagedChannel</code> object if available.
     * Otherwise, creates a new <code>ManagedChannel</code> object for the resolved endpoint and returns it.
     * @param clazz gRPC stub class
     * @return <code>ManagedChannel</code> object that corresponds to the given class
     */
    public ManagedChannel getChannel(Class<? extends io.grpc.stub.AbstractStub> clazz) {
        return channelCache.computeIfAbsent(resolveEndpoint(clazz), endpoint -> NettyChannelBuilder.forTarget(endpoint).build());
    }

    private String resolveEndpoint(Class<? extends AbstractStub> clazz) {
        if (!StubToServiceMapping.map.containsKey(clazz)) {
            throw new IllegalArgumentException(String.format("cannot find service related to class %s", clazz.getSimpleName()));
        }

        String service = StubToServiceMapping.map.get(clazz);
        if (!endpointMap.containsKey(service)) {
            throw new IllegalStateException(String.format("cannot find endpoint for service %s", service));
        }

        return endpointMap.get(service);
    }
}
