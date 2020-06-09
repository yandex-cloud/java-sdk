package yandex.cloud.sdk.auth.provider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Encapsulates background update settings for credential providers
 */
public class AuthUpdater {
    private final CancellableContext cancellableContext;
    private final boolean stopOnRuntimeShutdown;
    private final ScheduledExecutorService updateScheduler;
    private final long delay;
    private final double jitter;

    private AuthUpdater(CancellableContext cancellableContext, boolean stopOnRuntimeShutdown, ScheduledExecutorService updateScheduler, long delay, double jitter) {
        this.cancellableContext = cancellableContext;
        this.stopOnRuntimeShutdown = stopOnRuntimeShutdown;
        this.updateScheduler = updateScheduler;
        this.delay = delay;
        this.jitter = jitter;
    }

    public static Builder builder() {
        return new Builder();
    }

    BackgroundUpdatingCredentialProvider wrapProvider(CredentialProvider delegate) {
        if (cancellableContext != null) {
            cancellableContext.setRunnable(updateScheduler::shutdownNow);
        }
        if (stopOnRuntimeShutdown) {
            Runtime.getRuntime().addShutdownHook(new Thread(updateScheduler::shutdownNow));
        }
        return new BackgroundUpdatingCredentialProvider(delegate, delay, jitter, updateScheduler);
    }

    public static class Builder {
        private ScheduledExecutorService updateScheduler;
        private boolean stopOnRuntimeShutdown;
        private CancellableContext cancellableContext;
        private long delay = 100;
        private double jitter = 0.2;

        private Builder() {
        }

        public Builder scheduledExecutorService(ScheduledExecutorService updateScheduler) {
            this.updateScheduler = updateScheduler;
            return this;
        }

        public Builder stopOnRuntimeShutdown() {
            this.stopOnRuntimeShutdown = true;
            return this;
        }

        public Builder cancellableContext(CancellableContext context) {
            this.cancellableContext = context;
            return this;
        }

        public Builder retryImmediately() {
            this.delay = 0L;
            this.jitter = 0.0;
            return this;
        }

        public Builder retryDelay(long delay, double jitter) {
            this.delay = delay;
            this.jitter = jitter;
            return this;
        }

        public AuthUpdater build() {
            if (updateScheduler == null && cancellableContext == null && !stopOnRuntimeShutdown) {
                throw new IllegalStateException("Trying to build background updating credential provider without stopping policy");
            }

            if (updateScheduler == null) {
                updateScheduler = Executors.newSingleThreadScheduledExecutor();
            }
            return new AuthUpdater(cancellableContext, stopOnRuntimeShutdown, updateScheduler, delay, jitter);
        }
    }

    public static class CancellableContext {
        Runnable runnable;

        public void cancel() {
            if (runnable != null) {
                runnable.run();
            }
        }

        private void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }
    }
}
