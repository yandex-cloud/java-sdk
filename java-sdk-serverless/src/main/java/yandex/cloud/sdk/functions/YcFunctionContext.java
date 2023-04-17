package yandex.cloud.sdk.functions;

import yandex.cloud.sdk.functions.exception.HeaderNotFoundException;
import java.net.http.HttpHeaders;

public class YcFunctionContext implements Context {
    public static final String HEADER_REQUEST_ID = "Lambda-Runtime-Aws-Request-Id";
    public static final String HEADER_FUNCTION_NAME = "Lambda-Runtime-Function-Name";
    public static final String HEADER_FUNCTION_VERSION_ID = "Lambda-Runtime-Function-Version";
    public static final String HEADER_MEMORY_LIMIT = "Lambda-Runtime-Memory-Limit";
    public static final String HEADER_TOKEN_JSON = "Lambda-Runtime-Token-Json";

    private final String requestId;
    private final String functionId;
    private final String functionVersionId;
    private final int memoryLimit;
    private final String tokenJson;


    public YcFunctionContext(final HttpHeaders headers) {
        this.requestId = headers.firstValue(HEADER_REQUEST_ID).orElseThrow(() -> new HeaderNotFoundException(HEADER_REQUEST_ID));
        this.functionId = headers.firstValue(HEADER_FUNCTION_NAME).orElseThrow(() -> new HeaderNotFoundException(HEADER_FUNCTION_NAME));
        this.functionVersionId = headers.firstValue(HEADER_FUNCTION_VERSION_ID).orElseThrow(() -> new HeaderNotFoundException(HEADER_FUNCTION_VERSION_ID));
        this.memoryLimit = Integer.parseInt(headers.firstValue(HEADER_MEMORY_LIMIT).orElseThrow(() -> new HeaderNotFoundException(HEADER_MEMORY_LIMIT)));
        this.tokenJson = headers.firstValue(HEADER_TOKEN_JSON).orElse("");
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getFunctionId() {
        return functionId;
    }

    @Override
    public String getFunctionVersionId() {
        return functionVersionId;
    }

    @Override
    public int getMemoryLimit() {
        return memoryLimit;
    }

    @Override
    public String getTokenJson() {
        return tokenJson;
    }
}