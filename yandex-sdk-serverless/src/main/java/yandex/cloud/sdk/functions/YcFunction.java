package yandex.cloud.sdk.functions;

public interface YcFunction<T, R> {
    R handle(T event, Context context);
}