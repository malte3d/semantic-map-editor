package com.malte3d.suturo.sme.ui.viewmodel;

import com.malte3d.suturo.commons.javafx.CompletableFutureTaskBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Executor;

@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UiService {

    @NonNull
    private final Executor executor;

    public <T> CompletableFutureTaskBuilder<T> createFutureTask() {
        return new CompletableFutureTaskBuilder<>(executor);
    }

}
