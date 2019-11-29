package yandex.cloud.sdk.utils;

import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.api.operation.OperationServiceOuterClass.GetOperationRequest;

import java.time.Duration;
import java.time.Instant;

public class OperationUtils {
    public static Operation wait(OperationServiceBlockingStub operationService, Operation operation, Duration timeout)
            throws InterruptedException, OperationTimeoutException {
        return OperationUtils.wait(operationService, operation, timeout, Duration.ofMillis(500));
    }

    public static Operation wait(OperationServiceBlockingStub operationService, Operation operation,
                                 Duration timeout, Duration pollInterval)
            throws InterruptedException, OperationTimeoutException {
        if (operation.getDone()) {
            return operation;
        }

        operation = get(operationService, operation);
        if (operation.getDone()) {
            return operation;
        }

        Instant deadline = Instant.now().plus(timeout);
        while (!operation.getDone() && Instant.now().isBefore(deadline)) {
            Thread.sleep(pollInterval.toMillis());
            operation = get(operationService, operation);
        }
        if (operation.getDone()) {
            return operation;
        }
        throw new OperationTimeoutException(operation.getId());
    }

    private static Operation get(OperationServiceBlockingStub operationService, Operation operation) {
        return operationService.get(GetOperationRequest.newBuilder()
                .setOperationId(operation.getId())
                .build());
    }
}
