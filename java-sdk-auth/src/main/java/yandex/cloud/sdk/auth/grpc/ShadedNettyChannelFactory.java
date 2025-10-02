package yandex.cloud.sdk.auth.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

class ShadedNettyChannelFactory implements ManagedChannelFactory {
    public ShadedNettyChannelFactory() { }

    @Override
    public ManagedChannel newManagedChannel(String target, String userAgent) {
        return NettyChannelBuilder.forTarget(target).userAgent(userAgent).build();
    }
}
