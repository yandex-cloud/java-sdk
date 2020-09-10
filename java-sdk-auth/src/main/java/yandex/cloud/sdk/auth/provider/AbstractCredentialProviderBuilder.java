package yandex.cloud.sdk.auth.provider;

public abstract class AbstractCredentialProviderBuilder<BUILDER extends AbstractCredentialProviderBuilder<BUILDER>>
        implements CredentialProvider.Builder {
    private AuthUpdater updater = null;
    private boolean cached = true;

    public BUILDER withDefaultBackgroundUpdater() {
        this.updater = AuthUpdater.builder().stopOnRuntimeShutdown().build();
        //noinspection unchecked
        return (BUILDER) this;
    }

    public BUILDER withBackgroundUpdater(AuthUpdater updater) {
        this.updater = updater;
        //noinspection unchecked
        return (BUILDER) this;
    }

    public BUILDER withBackgroundUpdater(AuthUpdater.Builder updater) {
        this.updater = updater.build();
        //noinspection unchecked
        return (BUILDER) this;
    }

    public BUILDER enableCache() {
        this.cached = true;
        //noinspection unchecked
        return (BUILDER) this;
    }

    public BUILDER disableCache() {
        this.cached = false;
        //noinspection unchecked
        return (BUILDER) this;
    }

    @Override
    public CredentialProvider build() {
        CredentialProvider provider = providerBuild();
        if (updater != null) {
            provider = updater.wrapProvider(provider);
        }
        if (cached) {
            provider = new CachingCredentialProvider(provider);
        }
        return provider;
    }

    protected abstract CredentialProvider providerBuild();

}
