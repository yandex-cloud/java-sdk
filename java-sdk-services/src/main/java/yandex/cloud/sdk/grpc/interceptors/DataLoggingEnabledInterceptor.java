package yandex.cloud.sdk.grpc.interceptors;

import io.grpc.*;

public class DataLoggingEnabledInterceptor implements ClientInterceptor {

    private static final Metadata.Key<String> DATA_LOGGING_ENABLE = Metadata.Key.of("x-data-logging-enabled", Metadata.ASCII_STRING_MARSHALLER);

    private final boolean dataLoggingEnabled;

    public DataLoggingEnabledInterceptor(boolean dataLoggingEnabled) {
        this.dataLoggingEnabled = dataLoggingEnabled;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(DATA_LOGGING_ENABLE, Boolean.valueOf(dataLoggingEnabled).toString());
                super.start(responseListener, headers);
            }
        };
    }

}
