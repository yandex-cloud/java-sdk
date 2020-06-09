package yandex.cloud.sdk.utils;

import yandex.cloud.api.operation.OperationOuterClass.Operation;
import yandex.cloud.api.operation.OperationServiceGrpc.OperationServiceBlockingStub;
import yandex.cloud.api.operation.OperationServiceOuterClass.GetOperationRequest;

import java.time.Duration;
import java.time.Instant;

/**
 * Helper functions to work with operations.
 */
public class OperationUtils {
    /**
     * Waits for an operation completion for the given timeout.
     * @param operationService gRPC stub to communicate with a server
     * @param operation an operation that should be completed
     * @param timeout operation completion timeout
     * @return the same operation but in completed state
     * @throws InterruptedException if any thread has interrupted the current thread.
     * @throws OperationTimeoutException if operation was not completed within the given timeout
     */
    public static Operation wait(OperationServiceBlockingStub operationService, Operation operation, Duration timeout)
            throws InterruptedException, OperationTimeoutException {
        return OperationUtils.wait(operationService, operation, timeout, Duration.ofMillis(500));
    }

    /**
     * Waits for an operation completion for the given timeout checking operation state with the given interval.
     * @param operationService gRPC stub to communicate with a server
     * @param operation an operation that should be completed
     * @param timeout operation completion timeout
     * @param pollInterval the length of time to wait between operation state checks
     * @return the same operation but in completed state
     * @throws InterruptedException if any thread has interrupted the current thread.
     * @throws OperationTimeoutException if operation was not completed within the given timeout
     */
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

    /**
     * Gets updated state of a given operation
     * @param operationService gRPC stub that communicates with server
     * @param operation an operation that should be completed
     * @return updated operation
     */
    private static Operation get(OperationServiceBlockingStub operationService, Operation operation) {
        return operationService.get(GetOperationRequest.newBuilder()
                .setOperationId(operation.getId())
                .build());
    }
}
