package com.malte3d.suturo.sme.ui.viewmodel;

import com.malte3d.suturo.commons.ddd.event.domain.DomainEventHandler;
import com.malte3d.suturo.commons.javafx.service.CompletableFutureTask;
import com.malte3d.suturo.commons.javafx.service.GlobalExecutor;
import com.malte3d.suturo.commons.javafx.service.UiService;
import com.malte3d.suturo.commons.messages.Messages;
import com.malte3d.suturo.sme.application.service.settings.SettingsService;
import com.malte3d.suturo.sme.domain.model.application.settings.Settings;
import com.malte3d.suturo.sme.domain.model.application.settings.advanced.DebugMode;
import com.malte3d.suturo.sme.ui.viewmodel.editor.Editor;
import com.malte3d.suturo.sme.ui.viewmodel.editor.EditorViewModel;
import javafx.application.HostServices;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.concurrent.Executor;

@Slf4j
@Getter
public class MainViewModel extends UiService {

    private final DomainEventHandler domainEventHandler;

    private final EditorViewModel editorViewModel;

    private final Provider<HostServices> hostServices;

    private final SettingsService settingsService;

    @Inject
    public MainViewModel(
            @NonNull @GlobalExecutor Executor executor,
            @NonNull DomainEventHandler domainEventHandler,
            @NonNull EditorViewModel editorViewModel,
            @NonNull Provider<HostServices> hostServices,
            @NonNull SettingsService settingsService) {

        super(executor);

        this.domainEventHandler = domainEventHandler;

        this.editorViewModel = editorViewModel;

        this.hostServices = hostServices;
        this.settingsService = settingsService;

        registerEventConsumer();
    }

    private void registerEventConsumer() {

    }

    /**
     * @return a {@link CompletableFutureTask} that loads the 3D-Editor and returns it.
     */
    public CompletableFutureTask<Editor> loadEditor() {

        return this.<Editor>createFutureTask()
                .withNotificationMessageOnError("Application.Main.Editor.Initialization.Error")
                .withLoggerMessageOnError("Error while initializing 3D-Editor")
                .withTask(editorViewModel::initializeEditor);
    }


    /**
     * Raises an {@link ExitApplicationEvent} to exit the application.
     */
    public void exitApplication() {
        domainEventHandler.raise(new ExitApplicationEvent());
    }

    /**
     * Opens the URL of the copyright owner in the default browser.
     */
    public void openCopyrightOwnerUrl() {
        hostServices.get().showDocument(Messages.getString("Application.Help.About.CopyrightOwnerUrl"));
    }

    /**
     * Toggles the debug mode and saves the new mode to the application settings.
     */
    public void toggleDebugMode() {

        this.<DebugMode>createFutureTask()
                .withLoggerMessageOnError("Error while saving debug mode to settings")
                .withTask(() -> {

                    final Settings currentSettings = settingsService.get();
                    DebugMode newDebugMode = DebugMode.of(!currentSettings.getAdvanced().getDebugMode().isEnabled());

                    Settings newSettings = currentSettings.withAdvanced(currentSettings.getAdvanced().withDebugMode(newDebugMode));

                    settingsService.save(newSettings);

                    return newDebugMode;
                });
    }

}
