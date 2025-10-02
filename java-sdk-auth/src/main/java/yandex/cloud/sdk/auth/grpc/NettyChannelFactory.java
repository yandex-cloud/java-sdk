package yandex.cloud.sdk.auth.grpc;

import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;

class NettyChannelFactory implements ManagedChannelFactory {
    public NettyChannelFactory() { }

    @Override
    public ManagedChannel newManagedChannel(String target, String userAgent) {
        return NettyChannelBuilder.forTarget(target).userAgent(userAgent).build();
    }
}
