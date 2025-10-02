package yandex.cloud.sdk.auth.grpc;

import io.grpc.ManagedChannel;

/**
 * @author Nikolay Perfilov
 * @author Aleksandr Gorshenin
 */
public interface ManagedChannelFactory {
    ManagedChannel newManagedChannel(String target, String userAgent);

    static ManagedChannelFactory getInstance() {
        return ChannelFactoryLoader.getInstance();
    }
}
