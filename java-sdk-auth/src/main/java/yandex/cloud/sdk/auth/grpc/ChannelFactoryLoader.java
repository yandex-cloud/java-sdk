package yandex.cloud.sdk.auth.grpc;

import java.lang.reflect.InvocationTargetException;


/**
 *
 * @author Aleksandr Gorshenin
 */
class ChannelFactoryLoader {
    private ChannelFactoryLoader() { }

    public static ManagedChannelFactory getInstance() {
        return FactoryLoader.factory;
    }

    private static class FactoryLoader {
        private static final String SHADED_DEPS = "io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder";
        private static final String NETTY_DEPS = "io.grpc.netty.NettyChannelBuilder";

        private static ManagedChannelFactory factory;

        static {
            boolean ok = tryLoad(SHADED_DEPS, ShadedNettyChannelFactory.class)
                    || tryLoad(NETTY_DEPS, NettyChannelFactory.class);
            if (!ok) {
                throw new IllegalStateException("Cannot load any ManagedChannelFactory!! "
                        + "Classpath must contain grpc-netty or grpc-netty-shaded");
            }
        }

        private static boolean tryLoad(String name, Class<? extends ManagedChannelFactory> clazz) {
            try {
                Class.forName(name);
                factory = clazz.getConstructor().newInstance();
                return true;
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException |
                    InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
                return false;
            }
        }
    }
}
