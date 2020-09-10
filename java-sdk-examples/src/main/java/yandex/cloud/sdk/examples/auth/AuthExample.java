package yandex.cloud.sdk.examples.auth;

import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;
import yandex.cloud.sdk.auth.provider.AuthUpdater;
import yandex.cloud.sdk.auth.provider.CredentialProvider;
import yandex.cloud.sdk.auth.provider.OauthCredentialProvider;

import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AuthExample {
    public static void main(String[] args) {
        { // From oauth
            CredentialProvider oauthProvider1 = OauthCredentialProvider.builder()
                    .fromEnv("TOKEN")
                    .build();

            CredentialProvider oauthProvider2 = OauthCredentialProvider.builder()
                    .fromFile(Paths.get("oauth.txt"))
                    .build();
        }

        { // From compute engine (metadata server)
            CredentialProvider defaultComputeEngine = Auth.computeEngineBuilder().build();
        }

        { // From api-key
            CredentialProvider provider1 = Auth.apiKeyBuilder()
                    .serviceAccountKey(new ServiceAccountKey("keyId", "serviceAccountId", "createdAt", "keyAlgorithm", "publicKey", "privateKey"))
                    .build();

            CredentialProvider provider2 = Auth.apiKeyBuilder()
                    .fromFile(Paths.get("sa.json"))
                    .build();
        }

        { // From plain iam token
            CredentialProvider iamTokenProvider = Auth.iamTokenBuilder()
                    .token("CggVAg******MjE=")
                    .build();
        }


        // ***** Customizing ****

        { // Background update
            CredentialProvider computeEngineWithDefaultUpdater = Auth.computeEngineBuilder()
                    .withDefaultBackgroundUpdater()
                    .build();

            CredentialProvider computeEngineWithUpdater1 = Auth.computeEngineBuilder()
                    .withBackgroundUpdater(AuthUpdater.builder()
                            .retryImmediately()
                            .stopOnRuntimeShutdown())
                    .build();


            CredentialProvider computeEngineWithUpdater2 = Auth.computeEngineBuilder()
                    .withBackgroundUpdater(AuthUpdater.builder()
                            .stopOnRuntimeShutdown()
                            .retryImmediately())
                    .build();

            AuthUpdater.CancellableContext cancellableContext = new AuthUpdater.CancellableContext();
            CredentialProvider computeEngineWithBackground3 = Auth.computeEngineBuilder()
                    .withBackgroundUpdater(AuthUpdater.builder()
                            .retryDelay(100, 0.5)
                            .cancellableContext(cancellableContext))
                    .build();
            // do something
            cancellableContext.cancel();

            ScheduledExecutorService customExecutorService = Executors.newScheduledThreadPool(1);
            CredentialProvider computeEngineWithBackground4 = Auth.computeEngineBuilder()
                    .withBackgroundUpdater(AuthUpdater.builder()
                            .scheduledExecutorService(customExecutorService)
                            .build())
                    .build();
            customExecutorService.shutdownNow();

        }

        { // Caching (enabled for all providers by default)

            CredentialProvider provider1 = Auth.computeEngineBuilder()
                    .disableCache()
                    .build();

            CredentialProvider provider2 = Auth.computeEngineBuilder()
                    .enableCache() // do nothing, because it's default for now
                    .build();

        }
    }
}
