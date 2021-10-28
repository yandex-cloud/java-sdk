package yandex.cloud.sdk.examples.certificatemanager;

import yandex.cloud.api.certificatemanager.v1.CertificateContentServiceGrpc;
import yandex.cloud.api.certificatemanager.v1.CertificateContentServiceOuterClass.GetCertificateContentRequest;
import yandex.cloud.api.certificatemanager.v1.CertificateContentServiceOuterClass.GetCertificateContentResponse;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;

import java.time.Duration;

import static java.util.Objects.requireNonNull;

public class DownloadCertificateExample {

    private static final String CERTIFICATE_ID = "<certificate-id>";

    public static void main(String[] args) {
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();

        CertificateContentServiceGrpc.CertificateContentServiceBlockingStub certificateContentServiceStub = factory.create(CertificateContentServiceGrpc.CertificateContentServiceBlockingStub.class, CertificateContentServiceGrpc::newBlockingStub);

        GetCertificateContentRequest request = GetCertificateContentRequest.newBuilder()
                .setCertificateId(requireNonNull(CERTIFICATE_ID))
                .build();

        GetCertificateContentResponse response = certificateContentServiceStub.get(request);

        System.out.println(String.format("Got certificate with id %s and chain \n %s ", response.getCertificateId(), response.getCertificateChainList()));
    }
}
