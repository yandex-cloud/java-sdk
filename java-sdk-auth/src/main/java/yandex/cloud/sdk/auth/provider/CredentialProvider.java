package yandex.cloud.sdk.auth.provider;

import yandex.cloud.sdk.auth.IamToken;

/**
 * Retrieves {@link IamToken} used in all API calls.
 */
public interface CredentialProvider {
    IamToken get();

    interface Builder {
        CredentialProvider build();
    }
}
