package yandex.cloud.sdk;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.AbstractStub;
import yandex.cloud.api.endpoint.ApiEndpointOuterClass.ApiEndpoint;
import yandex.cloud.api.endpoint.ApiEndpointServiceGrpc;
import yandex.cloud.api.endpoint.ApiEndpointServiceOuterClass;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ChannelFactory {
    static private final String DEFAULT_ENDPOINT = "api.cloud.yandex.net";
    static private final int DEFAULT_PORT = 443;
    static private final Map<String, String> DEFAULT_ENDPOINT_MAP = new HashMap<String, String>() {{
        put("operation", "operation.api.cloud.yandex.net:443");
        put("compute", "compute.api.cloud.yandex.net:443");
        put("iam", "iam.api.cloud.yandex.net:443");
        put("resourcemanager", "resource-manager.api.cloud.yandex.net:443");
        put("resource-manager", "resource-manager.api.cloud.yandex.net:443");
        put("mdb-clickhouse", "mdb.api.cloud.yandex.net:443");
        put("managed-clickhouse", "mdb.api.cloud.yandex.net:443");
        put("mdb-mongodb", "mdb.api.cloud.yandex.net:443");
        put("managed-mongodb", "mdb.api.cloud.yandex.net:443");
        put("mdb-postgresql", "mdb.api.cloud.yandex.net:443");
        put("managed-postgresql", "mdb.api.cloud.yandex.net:443");
        put("mdb-redis", "mdb.api.cloud.yandex.net:443");
        put("managed-redis", "mdb.api.cloud.yandex.net:443");
        put("mdb-mysql", "mdb.api.cloud.yandex.net:443");
        put("managed-mysql", "mdb.api.cloud.yandex.net:443");
        put("vpc", "vpc.api.cloud.yandex.net:443");
        put("container-registry", "container-registry.api.cloud.yandex.net:443");
        put("load-balancer", "load-balancer.api.cloud.yandex.net:443");
        put("serverless-functions", "serverless-functions.api.cloud.yandex.net:443");
        put("serverless-triggers", "serverless-triggers.api.cloud.yandex.net:443");
        put("k8s", "mks.api.cloud.yandex.net:443");
        put("managed-kubernetes", "mks.api.cloud.yandex.net:443");
        put("iot-devices", "iot-devices.api.cloud.yandex.net:443");
        put("kms", "kms.api.cloud.yandex.net:443");
        put("endpoint", "api.cloud.yandex.net:443");
        put("storage", "storage.yandexcloud.net:443");
        put("serialssh", "serialssh.cloud.yandex.net:9600");
        put("ai-translate", "translate.api.cloud.yandex.net:443");
        put("ai-vision", "vision.api.cloud.yandex.net:443");
        put("ai-stt", "transcribe.api.cloud.yandex.net:443");
        put("ai-speechkit", "transcribe.api.cloud.yandex.net:443");
    }};

    private final Map<String, String> endpointMap;
    private Map<String, ManagedChannel> channelCache;

    public ChannelFactory(String endpoint, int port) {
        if (DEFAULT_ENDPOINT.equals(endpoint) && DEFAULT_PORT == port) {
            this.endpointMap = DEFAULT_ENDPOINT_MAP;
        } else {
            this.endpointMap = createEndpointMapping(endpoint, port);
        }
        this.channelCache = new ConcurrentHashMap<>();
    }

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

    public ManagedChannel create(Class<? extends io.grpc.stub.AbstractStub> clazz) {
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
