package com.malte3d.suturo.commons.javafx;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class CompletableFutureTaskBuilder<T> {

    @NonNull
    private final Executor executor;

    private String loggerErrorMessage = "";
    private Object[] loggerErrorMessageArgs;

    private String errorMessageKey;

    public CompletableFutureTaskBuilder<T> withLoggerMessageOnError(@NonNull String errorMessage, Object... args) {
        this.loggerErrorMessage = errorMessage;
        this.loggerErrorMessageArgs = args;
        return this;
    }

    public CompletableFutureTaskBuilder<T> withErrorMessageKey(@NonNull String messageKey) {
        this.errorMessageKey = messageKey;
        return this;
    }

    public CompletableFutureTask<T> withTask(@NonNull Supplier<T> task) {
        return new CompletableFutureTask<>(new UiServiceTask<>(task, loggerErrorMessage, loggerErrorMessageArgs), executor);
    }
}
