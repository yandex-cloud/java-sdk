package yandex.cloud.sdk;

/**
 * Signals that {@link ChannelFactory} creation failed.
 */
public class ChannelFactoryCreationException extends RuntimeException {
    public ChannelFactoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
