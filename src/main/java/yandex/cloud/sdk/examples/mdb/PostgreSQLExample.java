package yandex.cloud.sdk.examples.mdb;

import yandex.cloud.api.mdb.postgresql.v1.ClusterOuterClass;
import yandex.cloud.api.mdb.postgresql.v1.ClusterServiceGrpc;
import yandex.cloud.api.mdb.postgresql.v1.ClusterServiceGrpc.ClusterServiceBlockingStub;
import yandex.cloud.api.mdb.postgresql.v1.ClusterServiceOuterClass;
import yandex.cloud.api.mdb.postgresql.v1.ClusterServiceOuterClass.CreateClusterRequest;
import yandex.cloud.api.mdb.postgresql.v1.ClusterServiceOuterClass.DeleteClusterRequest;
import yandex.cloud.api.mdb.postgresql.v1.DatabaseOuterClass;
import yandex.cloud.api.mdb.postgresql.v1.UserOuterClass;
import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.sdk.ServiceFactory;
import yandex.cloud.sdk.ServiceFactoryConfig;
import yandex.cloud.sdk.Zone;
import yandex.cloud.sdk.auth.Auth;
import yandex.cloud.sdk.utils.OperationUtils;

import java.time.Duration;

public class PostgreSQLExample {
    private static final String MY_YC_FOLDER_ID = "<folder-id>";
    private static final String MY_YC_NETWORK_ID = "<network-id>";

    public static void main(String[] args) throws Exception {
        // Configuration
        ServiceFactoryConfig config = ServiceFactoryConfig.builder()
                .credentials(Auth.fromEnv(Auth::oauthToken, "YC_OAUTH"))
                .requestTimeout(Duration.ofMinutes(1))
                .build();

        ServiceFactory factory = new ServiceFactory(config);
        ClusterServiceBlockingStub clusterService = factory.create(ClusterServiceBlockingStub.class, ClusterServiceGrpc::newBlockingStub);
        OperationServiceBlockingStub operationService = factory.create(OperationServiceBlockingStub.class, OperationServiceGrpc::newBlockingStub);

        // Create PostgreSQL cluster with 1 host. It may take a while.
        Operation createOperation = clusterService.create(buildCreateClusterRequest("mypg", "example-db"));
        System.out.println("Create PostgreSQL cluster request sent");

        // Wait for cluster creation
        String clusterId = createOperation.getMetadata().unpack(ClusterServiceOuterClass.CreateClusterMetadata.class).getClusterId();
        OperationUtils.wait(operationService, createOperation, Duration.ofMinutes(10));
        System.out.println(String.format("Created with id %s", clusterId));

        // Delete PostgreSQL cluster
        Operation deleteOperation = clusterService.delete(buildDeleteClusterRequest(clusterId));
        System.out.println("Delete PostgreSQL cluster request sent");

        // Wait for cluster creation
        OperationUtils.wait(operationService, deleteOperation, Duration.ofMinutes(5));
        System.out.println(String.format("Deleted cluster %s", clusterId));
    }

    private static CreateClusterRequest buildCreateClusterRequest(String clusterName, String dbName) {
        String user = "user";
        String password = "password";

        return CreateClusterRequest.newBuilder()
                .setFolderId(MY_YC_FOLDER_ID)
                .setName(clusterName)
                .setEnvironment(ClusterOuterClass.Cluster.Environment.PRODUCTION)
                .setConfigSpec(ClusterServiceOuterClass.ConfigSpec.newBuilder()
                        .setVersion("10")
                        .setResources(ClusterOuterClass.Resources.newBuilder()
                                .setResourcePresetId("s2.micro")
                                .setDiskSize(10L * 1024 * 1024 * 1024)
                                .setDiskTypeId("network-ssd")
                                .build())
                        .build())
                .addDatabaseSpecs(DatabaseOuterClass.DatabaseSpec.newBuilder()
                        .setName(dbName)
                        .setOwner(user)
                        .build())
                .addUserSpecs(UserOuterClass.UserSpec.newBuilder()
                        .setName(user)
                        .setPassword(password)
                        .addPermissions(UserOuterClass.Permission.newBuilder()
                                .setDatabaseName(dbName)
                                .build())
                        .build())
                .addHostSpecs(ClusterServiceOuterClass.HostSpec.newBuilder()
                        .setZoneId(Zone.RU_CENTRAL1_B.getId())
                        .setAssignPublicIp(false)
                        .build())
                .setNetworkId(MY_YC_NETWORK_ID)
                .build();
    }

    private static DeleteClusterRequest buildDeleteClusterRequest(String clusterId) {
        return DeleteClusterRequest.newBuilder()
                .setClusterId(clusterId)
                .build();
    }
}
