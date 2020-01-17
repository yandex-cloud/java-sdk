package yandex.cloud.sdk.auth;

import io.grpc.ManagedChannel;
import yandex.cloud.api.iam.v1.IamTokenServiceGrpc;
import yandex.cloud.api.iam.v1.IamTokenServiceOuterClass;
import yandex.cloud.sdk.ChannelFactory;
import yandex.cloud.sdk.auth.jwt.ServiceAccountKey;
import yandex.cloud.sdk.auth.metadata.ServiceAccountTokenSupplier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

/**
 * Helper functions to work with {@link Credentials}.
 */
public class Auth {
    private Auth() {/*noninstantiable helper class*/}

    /**
     * Creates an {@link IamToken} object from its string representation
     * @param token string representation of IAM token
     * @return <code>IamToken</code> object
     */
    public static IamToken iamToken(String token) {
        return new IamToken(token);
    }

    /**
     * Creates an {@link OauthToken} object from its string representation
     * @param token string representation of OAuth token
     * @return <code>OauthToken</code> object
     */
    public static OauthToken oauthToken(String token) {
        return new OauthToken(token);
    }

    /**
     * Creates an {@link ApiKey} object from its JSON representation
     * @param key JSON representation of API key
     * @return <code>ApiKey</code> object
     */
    public static ApiKey apiKey(String key) {
        return new ApiKey(key);
    }

    /**
     * Creates an {@link ApiKey} object from {@link ServiceAccountKey} object that contains API key data
     * @param key <code>ServiceAccountKey</code> object that contains API key data
     * @return <code>ApiKey</code> object
     */
    public static ApiKey apiKey(ServiceAccountKey key) {
        return new ApiKey(key);
    }

    /**
     * Reads file content and creates credentials using <code>tokenProvider</code>
     * @param tokenProvider defines an object created from the file content
     * @param file path to a file with credentials data
     * @return credentials created from a content of the given file
     * @throws IOException if an I/O exception occurs during file read
     */
    public static Credentials fromFile(Function<String, Credentials> tokenProvider, Path file) throws IOException {
        String token = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim();
        return tokenProvider.apply(token);
    }

    /**
     * Reads environment variable content and creates credentials using <code>tokenProvider</code>
     * @param tokenProvider defines an object created from the file content
     * @param env environment variable name for credentials data
     * @return credentials created from a content of the given environment variable
     */
    public static Credentials fromEnv(Function<String, Credentials> tokenProvider, String env) {
        String token = System.getenv(env);
        if (Objects.isNull(token)) {
            throw new RuntimeException(String.format("Environment variable %s is not set", env));
        }

        return tokenProvider.apply(token);
    }

    /**
     * Retrieves IAM token of service account linked to VM. Should be executed on Yandex.Cloud VM to work correctly.
     * @return IAM token of service account linked to VM
     * @throws IOException if an I/O exception occurs during request to metadata
     */
    public static IamToken fromMetadata() throws IOException {
        ServiceAccountTokenSupplier tokenSupplier = new ServiceAccountTokenSupplier();
        return tokenSupplier.get();
    }

    /**
     * Exchanges given credentials for IAM token. IAM token is used in all API calls other than token exchange.
     * If IAM token is given then the token itself is returned, no exchange happens.
     * @param credentials credential to exchange to IAM token
     * @param factory <code>ChannelFactory</code> that is used to make API calls to exchange token
     * @return IAM token
     */
    public static IamToken exchangeToken(Credentials credentials, ChannelFactory factory) {
        if (credentials instanceof OauthToken) {
            return exchangeToken((OauthToken) credentials, factory);
        } else if (credentials instanceof ApiKey) {
            return exchangeToken((ApiKey) credentials, factory);
        } else if (credentials instanceof IamToken) {
            return (IamToken) credentials;
        } else {
            throw new RuntimeException(String.format("Unknown credentials type: %s", credentials.getClass().getSimpleName()));
        }
    }

    /**
     * Exchanges OAuth token for IAM token. IAM token is used in all API calls other than token exchange.
     * @param token OAuth token to exchange to IAM token
     * @param factory <code>ChannelFactory</code> that is used to make API calls to exchange token
     * @return IAM token
     */
    public static IamToken exchangeToken(OauthToken token, ChannelFactory factory) {
        ManagedChannel channel = factory.getChannel(IamTokenServiceGrpc.IamTokenServiceBlockingStub.class);
        String oauth = token.getToken();

        IamTokenServiceOuterClass.CreateIamTokenResponse response = IamTokenServiceGrpc.newBlockingStub(channel).create(
                IamTokenServiceOuterClass.CreateIamTokenRequest.newBuilder().setYandexPassportOauthToken(oauth).build());

        return new IamToken(response.getIamToken());
    }

    /**
     * Exchanges API key for IAM token. IAM token is used in all API calls other than token exchange.
     * API key is exchanged by generating JWT and then exchanging JWT to IAM token.
     * @param key API key to exchange to IAM token
     * @param factory <code>ChannelFactory</code> that is used to make API calls to exchange token
     * @return IAM token
     */
    public static IamToken exchangeToken(ApiKey key, ChannelFactory factory) {
        ManagedChannel channel = factory.getChannel(IamTokenServiceGrpc.IamTokenServiceBlockingStub.class);
        String jwt = key.getJwt().getToken();

        IamTokenServiceOuterClass.CreateIamTokenResponse response = IamTokenServiceGrpc.newBlockingStub(channel).create(
                IamTokenServiceOuterClass.CreateIamTokenRequest.newBuilder().setJwt(jwt).build());
        return new IamToken(response.getIamToken());
    }
}
