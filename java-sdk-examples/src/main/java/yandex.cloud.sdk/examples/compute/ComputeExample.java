package yandex.cloud.sdk.examples.compute;

import yandex.cloud.api.compute.v1.ImageOuterClass.Image;
import yandex.cloud.api.compute.v1.ImageServiceGrpc;
import yandex.cloud.api.compute.v1.ImageServiceGrpc.ImageServiceBlockingStub;
import yandex.cloud.api.compute.v1.ImageServiceOuterClass.GetImageLatestByFamilyRequest;
import yandex.cloud.api.compute.v1.InstanceOuterClass.Instance;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc;
import yandex.cloud.api.compute.v1.InstanceServiceGrpc.InstanceServiceBlockingStub;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.AttachedDiskSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.AttachedDiskSpec.DiskSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.CreateInstanceMetadata;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.CreateInstanceRequest;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.DeleteInstanceRequest;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.ListInstancesRequest;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.NetworkInterfaceSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.PrimaryAddressSpec;
import yandex.cloud.api.compute.v1.InstanceServiceOuterClass.ResourcesSpec;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.sdk.Platform;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.Zone;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;
import java.util.List;

public class ComputeExample {
    private static final String MY_YC_FOLDER_ID = "<folder-id>";
    private static final String MY_YC_CENTRAL1_B_SUBNET_ID = "<subnet-id>";
    private static final String YC_STANDARD_IMAGES = "standard-images";
    private static final String YC_UBUNTU_IMAGE_FAMILY = "ubuntu-1804";

    public static void main(String[] args) throws Exception {
        // Configuration
        ServiceFactory factory = ServiceFactory.builder()
                .credentialProvider(Auth.oauthTokenBuilder().fromEnv("YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();
        InstanceServiceBlockingStub instanceService = factory.create(InstanceServiceBlockingStub.class, InstanceServiceGrpc::newBlockingStub);
        OperationServiceBlockingStub operationService = factory.create(OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub);
        ImageServiceBlockingStub imageService = factory.create(ImageServiceBlockingStub.class, ImageServiceGrpc::newBlockingStub);

        // Get latest Ubuntu 18 image
        Image image = imageService.getLatestByFamily(buildGetLatestByFamilyRequest());

        // Create instance
        Operation createOperation = instanceService.create(buildCreateInstanceRequest(image.getId()));
        System.out.println("Create instance request sent");

        // Wait for instance creation
        String instanceId = createOperation.getMetadata().unpack(CreateInstanceMetadata.class).getInstanceId();
        OperationUtils.wait(operationService, createOperation, Duration.ofMinutes(5));
        System.out.println(String.format("Created with id %s", instanceId));

        // List instances in the folder
        List<Instance> instances = instanceService.list(buildListInstancesRequest()).getInstancesList();
        instances.forEach(System.out::println);
        System.out.println("Listed instances");

        // Delete created instance
        Operation deleteOperation = instanceService.delete(buildDeleteInstanceRequest(instanceId));
        System.out.println("Delete instance request sent");

        // Wait for instance deletion
        OperationUtils.wait(operationService, deleteOperation, Duration.ofMinutes(1));
        System.out.println(String.format("Deleted instance %s", instanceId));
    }

    private static GetImageLatestByFamilyRequest buildGetLatestByFamilyRequest() {
        return GetImageLatestByFamilyRequest.newBuilder()
                .setFolderId(YC_STANDARD_IMAGES)
                .setFamily(YC_UBUNTU_IMAGE_FAMILY)
                .build();
    }

    private static CreateInstanceRequest buildCreateInstanceRequest(String imageId) {
        return CreateInstanceRequest.newBuilder()
                .setFolderId(MY_YC_FOLDER_ID)
                .setName("ubuntu")
                .setZoneId(Zone.RU_CENTRAL1_B.getId())
                .setPlatformId(Platform.STANDARD_V1.getId())
                .setResourcesSpec(ResourcesSpec.newBuilder().setCores(1).setMemory(1024 * 1024 * 1024))
                .setBootDiskSpec(AttachedDiskSpec.newBuilder()
                        .setDiskSpec(DiskSpec.newBuilder()
                                .setImageId(imageId)
                                .setSize(10L * 1024 * 1024 * 1024)))
                .addNetworkInterfaceSpecs(NetworkInterfaceSpec.newBuilder()
                        .setSubnetId(MY_YC_CENTRAL1_B_SUBNET_ID)
                        .setPrimaryV4AddressSpec(PrimaryAddressSpec.getDefaultInstance())
                ).build();
    }

    private static ListInstancesRequest buildListInstancesRequest() {
        return ListInstancesRequest.newBuilder().setFolderId(MY_YC_FOLDER_ID).build();
    }

    private static DeleteInstanceRequest buildDeleteInstanceRequest(String instanceId) {
        return DeleteInstanceRequest.newBuilder().setInstanceId(instanceId).build();
    }
}
