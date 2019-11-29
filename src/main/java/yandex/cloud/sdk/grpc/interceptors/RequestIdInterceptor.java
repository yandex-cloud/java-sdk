package yandex.cloud.sdk.grpc.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;

import java.util.UUID;

public class RequestIdInterceptor implements ClientInterceptor  {
    public static final Metadata.Key<String> CLIENT_REQUEST_ID = Metadata.Key.of("x-client-request-id", Metadata.ASCII_STRING_MARSHALLER);
    public static final Metadata.Key<String> CLIENT_TRACE_ID = Metadata.Key.of("x-client-trace-id", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        String requestId = UUID.randomUUID().toString();
        String traceId = UUID.randomUUID().toString();

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(CLIENT_REQUEST_ID, requestId);
                headers.put(CLIENT_TRACE_ID, traceId);
                super.start(responseListener, headers);
            }
        };
    }
}
