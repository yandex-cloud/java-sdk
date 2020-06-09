package yandex.cloud.sdk.examples.kms;

import com.google.protobuf.FieldMask;
import yandex.cloud.api.kms.v1.SymmetricKeyOuterClass;
import yandex.cloud.api.kms.v1.SymmetricKeyOuterClass.SymmetricKey;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceGrpc;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceGrpc.SymmetricKeyServiceBlockingStub;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.CreateSymmetricKeyMetadata;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.CreateSymmetricKeyRequest;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.DeleteSymmetricKeyRequest;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.GetSymmetricKeyRequest;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.RotateSymmetricKeyMetadata;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.RotateSymmetricKeyRequest;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.ScheduleSymmetricKeyVersionDestructionMetadata;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.ScheduleSymmetricKeyVersionDestructionRequest;
import yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.UpdateSymmetricKeyRequest;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

import static yandex.cloud.api.kms.v1.SymmetricKeyServiceOuterClass.DeleteSymmetricKeyMetadata;

public class SymmetricKeyExample {

    /**
     * pass your folder id to run example
     */
    private static final String MY_YC_FOLDER_ID = "<folder-id>";

    public static void main(String[] args) throws Exception {
        // Configuration
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();
        SymmetricKeyServiceBlockingStub symmetricKeyService = factory.create(SymmetricKeyServiceBlockingStub.class, SymmetricKeyServiceGrpc::newBlockingStub);
        OperationServiceBlockingStub operationService = factory.create(OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub);

        // Create symmetric key
        Operation createOperation = symmetricKeyService.create(buildCreateKeyRequest());
        System.out.println("Create symmetric key request sent");

        // Wait for instance creation, may ignore it
        String keyId = createOperation.getMetadata().unpack(CreateSymmetricKeyMetadata.class).getKeyId();
        OperationUtils.wait(operationService, createOperation, Duration.ofSeconds(5));
        System.out.println(String.format("Created key with id %s", keyId));

        // Get symmetric key
        SymmetricKey key = symmetricKeyService.get(buildGetRequest(keyId));
        System.out.println(String.format("Get symmetric key:\n%s", key.toString()));

        // Update symmetric key
        Operation updateOperation = symmetricKeyService.update(buildUpdateRequest(keyId));
        updateOperation = OperationUtils.wait(operationService, updateOperation, Duration.ofSeconds(5));
        SymmetricKey updatedKey = updateOperation.getResponse().unpack(SymmetricKey.class);
        System.out.println(String.format("Updated symmetric key:\n%s", updatedKey.toString()));

        String oldPrimaryVersionId = updatedKey.getPrimaryVersion().getId();

        // Rotate symmetric key version
        Operation rotateOperation = symmetricKeyService.rotate(buildRotateRequest(keyId));
        rotateOperation = OperationUtils.wait(operationService, rotateOperation, Duration.ofSeconds(5));
        String newPrimaryVersionId = rotateOperation.getMetadata()
                .unpack(RotateSymmetricKeyMetadata.class)
                .getNewPrimaryVersionId();
        System.out.println(String.format("Symmetric key rotated, new primary version id %s", newPrimaryVersionId));

        // Schedule version destruction
        Operation scheduleVersionDestructionOperation = symmetricKeyService
                .scheduleVersionDestruction(buildScheduleVersionDestructionRequest(keyId, oldPrimaryVersionId));
        OperationUtils.wait(operationService, scheduleVersionDestructionOperation, Duration.ofSeconds(5));
        String scheduledVersionId = scheduleVersionDestructionOperation.getMetadata()
                .unpack(ScheduleSymmetricKeyVersionDestructionMetadata.class)
                .getVersionId();
        System.out.println(String.format("Version %s scheduled for destruction", scheduledVersionId));

        // Delete symmetric key
        Operation deleteOperation = symmetricKeyService.delete(buildDeleteRequest(keyId));
        OperationUtils.wait(operationService, deleteOperation, Duration.ofSeconds(5));
        String deletedKeyId = deleteOperation.getMetadata()
                .unpack(DeleteSymmetricKeyMetadata.class)
                .getKeyId();
        System.out.println(String.format("Symmetric key with id %s deleted", deletedKeyId));
    }

    private static DeleteSymmetricKeyRequest buildDeleteRequest(String keyId) {
        return DeleteSymmetricKeyRequest.newBuilder()
                .setKeyId(keyId)
                .build();
    }

    private static ScheduleSymmetricKeyVersionDestructionRequest buildScheduleVersionDestructionRequest(
            String keyId,
            String versionId
    ) {
        return ScheduleSymmetricKeyVersionDestructionRequest.newBuilder()
                .setKeyId(keyId)
                .setVersionId(versionId)
                .setPendingPeriod(com.google.protobuf.Duration.newBuilder()
                        .setSeconds(Duration.ofHours(1).getSeconds()))
                .build();
    }

    private static RotateSymmetricKeyRequest buildRotateRequest(String keyId) {
        return RotateSymmetricKeyRequest.newBuilder()
                .setKeyId(keyId)
                .build();
    }

    private static UpdateSymmetricKeyRequest buildUpdateRequest(String keyId) {
        FieldMask updateMask = FieldMask.newBuilder()
                .addPaths("name")
                .addPaths("description")
                .addPaths("default_algorithm")
                .addPaths("rotation_period")
                .addPaths("status")
                .build();
        return UpdateSymmetricKeyRequest.newBuilder()
                .setUpdateMask(updateMask)
                .setKeyId(keyId)
                .setName("MY_UPDATED_KEY")
                .setDescription("MY_UPDATED_DESCRIPTION")
                .setDefaultAlgorithm(SymmetricKeyOuterClass.SymmetricAlgorithm.AES_128)
                .setRotationPeriod(com.google.protobuf.Duration.newBuilder()
                        .setSeconds(Duration.ofDays(30).getSeconds()))
                .setStatus(SymmetricKey.Status.INACTIVE)
                .build();
    }

    private static GetSymmetricKeyRequest buildGetRequest(String keyId) {
        return GetSymmetricKeyRequest.newBuilder()
                .setKeyId(keyId)
                .build();
    }

    private static CreateSymmetricKeyRequest buildCreateKeyRequest() {
        return CreateSymmetricKeyRequest.newBuilder()
                .setFolderId(MY_YC_FOLDER_ID)
                .setDefaultAlgorithm(SymmetricKeyOuterClass.SymmetricAlgorithm.AES_256)
                .setName("MY_NEW_KEY")
                .setDescription("MY_EXAMPLE_KEY")
                .setRotationPeriod(com.google.protobuf.Duration.newBuilder()
                        .setSeconds(Duration.ofDays(7).getSeconds()))
                .build();
    }
}
