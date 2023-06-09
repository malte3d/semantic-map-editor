package com.malte3d.suturo.commons.javafx.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;

/**
 * Service for UI related tasks.
 *
 * <p>
 * This service is used to execute {@link CompletableFutureTask}s asynchronously on the given {@link Executor} thread(s).
 * </p>
 */
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UiService {

    @NonNull
    private final Executor executor;

    public <T> CompletableFutureTaskBuilder<T> createFutureTask() {
        return new CompletableFutureTaskBuilder<>(executor);
    }

}
