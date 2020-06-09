package yandex.cloud.sdk.grpc.interceptors;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.MethodDescriptor;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * An interceptor that enforces given deadline for a call.
 */
public class DeadlineClientInterceptor implements ClientInterceptor {
    /**
     * Timeout after which a call fails if it is still not completed
     */
    private final Duration timeout;

    /**
     * Constructs a <code>DeadlineClientInterceptor</code> object with given timeout
     * @param timeout timeout after which a call fails if it is still not completed
     */
    private DeadlineClientInterceptor(Duration timeout) {
        this.timeout = timeout;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
            MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
        CallOptions callOptionsWithDeadline = timeout != null ?
                callOptions.withDeadlineAfter(getTimeoutInMillis(), TimeUnit.MILLISECONDS) : callOptions;
        return next.newCall(method, callOptionsWithDeadline);
    }

    /**
     * Create a <code>DeadlineClientInterceptor</code> with given timeout
     * @param timeout timeout after which a call fails if it is still not completed
     * @return <code>DeadlineClientInterceptor</code> object
     */
    public static DeadlineClientInterceptor fromDuration(Duration timeout) {
        return new DeadlineClientInterceptor(timeout);
    }

    /**
     * Converts <code>Duration</code> object to milliseconds value
     * @return milliseconds value of {@link DeadlineClientInterceptor#timeout}
     */
    private long getTimeoutInMillis() {
        return timeout != null ? timeout.toMillis() : 0L;
    }
}
