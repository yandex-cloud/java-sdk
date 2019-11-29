package yandex.cloud.sdk.auth;

import io.grpc.ManagedChannel;
import yandex.cloud.api.iam.v1.IamTokenServiceGrpc;
import yandex.cloud.api.iam.v1.IamTokenServiceOuterClass;
import yandex.cloud.sdk.ChannelFactory;
import yandex.cloud.sdk.auth.metadata.ServiceAccountTokenSupplier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

public class Auth {
    public static IamToken iamToken(String token) {
        return new IamToken(token);
    }

    public static OauthToken oauthToken(String token) {
        return new OauthToken(token);
    }

    public static JsonKey jsonKey(String key) {
        return new JsonKey(key);
    }

    public static Credentials fromFile(Function<String, Credentials> tokenProvider, Path file) throws IOException {
        String token = new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim();
        return tokenProvider.apply(token);
    }

    public static Credentials fromEnv(Function<String, Credentials> tokenProvider, String env) {
        String token = System.getenv(env);
        if (Objects.isNull(token)) {
            throw new RuntimeException(String.format("Environment variable %s is not set", env));
        }

        return tokenProvider.apply(token);
    }

    public static IamToken fromMetadata() throws IOException {
        ServiceAccountTokenSupplier tokenSupplier = new ServiceAccountTokenSupplier();
        return tokenSupplier.get();
    }

    public static IamToken exchangeToken(Credentials credentials, ChannelFactory factory) {
        if (credentials instanceof OauthToken) {
            return exchangeToken((OauthToken) credentials, factory);
        } else if (credentials instanceof JsonKey) {
            return exchangeToken((JsonKey) credentials, factory);
        } else if (credentials instanceof IamToken) {
            return (IamToken) credentials;
        } else {
            throw new RuntimeException(String.format("Unknown credentials type: %s", credentials.getClass().getSimpleName()));
        }
    }

    public static IamToken exchangeToken(OauthToken token, ChannelFactory factory) {
        ManagedChannel channel = factory.create(IamTokenServiceGrpc.IamTokenServiceBlockingStub.class);
        String oauth = token.getToken();

        IamTokenServiceOuterClass.CreateIamTokenResponse response = IamTokenServiceGrpc.newBlockingStub(channel).create(
                IamTokenServiceOuterClass.CreateIamTokenRequest.newBuilder().setYandexPassportOauthToken(oauth).build());

        return new IamToken(response.getIamToken());
    }

    public static IamToken exchangeToken(JsonKey key, ChannelFactory factory) {
        ManagedChannel channel = factory.create(IamTokenServiceGrpc.IamTokenServiceBlockingStub.class);
        String jwt = key.getJwt().getToken();

        IamTokenServiceOuterClass.CreateIamTokenResponse response = IamTokenServiceGrpc.newBlockingStub(channel).create(
                IamTokenServiceOuterClass.CreateIamTokenRequest.newBuilder().setJwt(jwt).build());
        return new IamToken(response.getIamToken());
    }
}
