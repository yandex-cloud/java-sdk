package yandex.cloud.sdk.auth;

import yandex.cloud.sdk.auth.provider.ApiKeyCredentialProvider;
import yandex.cloud.sdk.auth.provider.ComputeEngineCredentialProvider;
import yandex.cloud.sdk.auth.provider.IamTokenCredentialProvider;
import yandex.cloud.sdk.auth.provider.OauthCredentialProvider;

/**
 * Helper functions to work with {@link yandex.cloud.sdk.auth.provider.CredentialProvider}.
 */
public class Auth {
    private Auth() {/*noninstantiable helper class*/}

    /**
     * Creates an {@link OauthCredentialProvider.Builder} to build a provider that provides credentials from oauth token
     *
     * @return <code>OauthCredentialProvider.Builder</code> object
     */
    public static OauthCredentialProvider.Builder oauthTokenBuilder() {
        return OauthCredentialProvider.builder();
    }

    /**
     * Creates an {@link ApiKeyCredentialProvider.Builder} to build a provider that provides credentials from service account's api key
     *
     * @return <code>ApiKeyCredentialProvider.Builder</code> object
     */
    public static ApiKeyCredentialProvider.Builder apiKeyBuilder() {
        return ApiKeyCredentialProvider.builder();
    }

    /**
     * Creates a {@link ComputeEngineCredentialProvider.Builder} to build a provider that uses a compute metadata server
     *
     * @return <code>ComputeEngineCredentialProvider.Builder</code> object
     */
    public static ComputeEngineCredentialProvider.Builder computeEngineBuilder() {
        return ComputeEngineCredentialProvider.builder();
    }

    /**
     * Creates a {@link IamTokenCredentialProvider.Builder} to build a provider that provides credentials from iam token
     *
     * @return <code>ComputeEngineCredentialProvider.Builder</code> object
     */
    public static IamTokenCredentialProvider.Builder iamTokenBuilder() {
        return IamTokenCredentialProvider.builder();
    }
}
