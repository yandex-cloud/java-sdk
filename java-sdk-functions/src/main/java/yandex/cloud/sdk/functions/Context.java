package yandex.cloud.sdk.functions;

public interface Context {
    String getRequestId();
    String getFunctionName();
    String getFunctionVersion();
    int getMemoryLimit();
    String getLogGroupName();
    String getStreamName();
    String getTokenJson();
}

