package yandex.cloud.sdk.functions;

import java.lang.Deprecated;

public interface Context {
    String getRequestId();

    String getFunctionId();

    String getFunctionVersionId();

    int getMemoryLimit();

    String getTokenJson();
}