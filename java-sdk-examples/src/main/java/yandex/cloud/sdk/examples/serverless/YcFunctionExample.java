package yandex.cloud.sdk.examples.serverless;

import yandex.cloud.sdk.functions.Context;
import yandex.cloud.sdk.functions.YcFunction;

public class YcFunctionExample implements YcFunction<Integer, String> {

    private Long fibonacci(Integer i) {
        if (i <= 2) {
            return 1L;
        }
        return fibonacci(i - 1) + fibonacci(i - 2);
    }

    /**
     * NOTE: you must run this function with integration=raw,
     * see https://cloud.yandex.ru/docs/functions/concepts/function-invoke#http
     *
     * @param event integer number i
     * @param context function context, see https://cloud.yandex.ru/docs/functions/lang/java/context
     * @return i-th fibonacci number
     */
    @Override
    public String handle(Integer event, Context context) {
        String id = context.getFunctionId();
        Long fib = fibonacci(event);
        return String.format("Function %s, %dth fibonacci number is %d", id, event, fib);
    }
}
