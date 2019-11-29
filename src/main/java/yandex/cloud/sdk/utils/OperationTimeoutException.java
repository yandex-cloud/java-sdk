package yandex.cloud.sdk.utils;

public class OperationTimeoutException extends RuntimeException {
    private final String id;

    public OperationTimeoutException(String id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Operation time out. Operation getId: " + id;
    }
}
