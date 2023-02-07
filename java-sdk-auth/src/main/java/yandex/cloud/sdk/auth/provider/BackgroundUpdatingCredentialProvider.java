package yandex.cloud.sdk.auth.provider;

import yandex.cloud.sdk.auth.IamToken;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Updates IAM token in separate thread when the token expiration is close.
 */
class BackgroundUpdatingCredentialProvider implements CredentialProvider {
    private final CredentialProvider delegate;
    private final long delay;
    private final double jitter;
    private final ScheduledExecutorService updateScheduler;

    private transient volatile IamToken iamToken = new IamToken("", Instant.MIN);

    BackgroundUpdatingCredentialProvider(CredentialProvider provider, long delay, double jitter, ScheduledExecutorService updateScheduler) {
        this.delegate = provider;
        this.delay = delay;
        this.jitter = jitter;
        this.updateScheduler = updateScheduler;
        updateAndScheduleNext();
    }

    @Override
    public IamToken get() {
        if (Instant.now().isBefore(iamToken.getExpiresAt())) {
            return iamToken;
        }
        throw new UnavailableIamTokenException("IAM token is expired");
    }

    private void updateAndScheduleNext() {
        Duration nextUpdate;
        try {
            IamToken iamToken = delegate.get();
            this.iamToken = iamToken;
            nextUpdate = Duration.between(Instant.now(), iamToken.getUpdateAt());
        } catch (Exception e) {
            if (Thread.currentThread().isInterrupted()) {
                throw e;
            }
            nextUpdate = Duration.ofMillis((long) (delay * (1 + jitter * (Math.random() * 2 - 1))));
        }
        updateScheduler.schedule(this::updateAndScheduleNext, nextUpdate.toNanos(), TimeUnit.NANOSECONDS);
    }

}
