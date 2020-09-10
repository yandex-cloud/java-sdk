package yandex.cloud.sdk.examples.ai;

import yandex.cloud.api.ai.translate.v2.TranslationServiceGrpc;
import yandex.cloud.api.ai.translate.v2.TranslationServiceGrpc.TranslationServiceBlockingStub;
import yandex.cloud.api.ai.translate.v2.TranslationServiceOuterClass.TranslateRequest;
import yandex.cloud.api.ai.translate.v2.TranslationServiceOuterClass.TranslateResponse;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;

import java.time.Duration;

public class TranslateExample {
    private static final String MY_YC_FOLDER_ID = "<folder-id>";

    public static void main(String[] args) {
        // Configuration
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();
        TranslationServiceBlockingStub translationService = factory.create(TranslationServiceBlockingStub.class, TranslationServiceGrpc::newBlockingStub);

        // Translate texts from English to Russian
        String text = "hello world";
        TranslateResponse response = translationService.translate(buildTranslateRequest(text));
        String translation = response.getTranslations(0).getText();
        System.out.println(String.format("%s -> %s", text, translation));
    }

    private static TranslateRequest buildTranslateRequest(String text) {
        return TranslateRequest.newBuilder()
                .setSourceLanguageCode("en")
                .setTargetLanguageCode("ru")
                .setFormat(TranslateRequest.Format.PLAIN_TEXT)
                .addTexts(text)
                .setFolderId(MY_YC_FOLDER_ID)
                .build();
    }
}
