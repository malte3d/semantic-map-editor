package com.malte3d.suturo.sme.ui.viewmodel.main;

import com.google.common.base.Preconditions;
import com.jme3.app.DebugKeysAppState;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AppState;
import com.malte3d.suturo.commons.ddd.event.domain.DomainEventHandler;
import com.malte3d.suturo.commons.javafx.fxml.FxmlViewFactory;
import com.malte3d.suturo.commons.javafx.service.CompletableFutureTask;
import com.malte3d.suturo.commons.javafx.service.GlobalExecutor;
import com.malte3d.suturo.commons.javafx.service.UiService;
import com.malte3d.suturo.commons.messages.Messages;
import com.malte3d.suturo.sme.application.service.settings.SettingsService;
import com.malte3d.suturo.sme.domain.model.application.settings.Settings;
import com.malte3d.suturo.sme.domain.model.application.settings.advanced.DebugMode;
import com.malte3d.suturo.sme.domain.model.application.settings.advanced.DebugModeChangedEvent;
import com.malte3d.suturo.sme.domain.model.application.settings.keymap.editor.CameraBehaviour;
import com.malte3d.suturo.sme.domain.model.application.settings.keymap.editor.CameraBehaviourChangedEvent;
import com.malte3d.suturo.sme.ui.viewmodel.editor.Editor;
import com.malte3d.suturo.sme.ui.viewmodel.editor.camera.CameraKeymap;
import com.malte3d.suturo.sme.ui.viewmodel.editor.camera.CameraKeymapBlender;
import com.malte3d.suturo.sme.ui.viewmodel.editor.camera.CameraKeymapCinema4D;
import javafx.application.HostServices;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Getter
public class MainViewModel extends UiService {

    private final DomainEventHandler domainEventHandler;

    private final FxmlViewFactory viewFactory;

    private final Provider<HostServices> hostServices;

    private final SettingsService settingsService;

    private Editor editor;

    @Inject
    public MainViewModel(
            @NonNull @GlobalExecutor Executor executor,
            @NonNull DomainEventHandler domainEventHandler,
            @NonNull FxmlViewFactory viewFactory,
            @NonNull Provider<HostServices> hostServices,
            @NonNull SettingsService settingsService) {

        super(executor);

        this.domainEventHandler = domainEventHandler;
        this.viewFactory = viewFactory;
        this.hostServices = hostServices;
        this.settingsService = settingsService;

        registerEventConsumer();
    }

    private void registerEventConsumer() {
        domainEventHandler.register(DebugModeChangedEvent.class, this::onDebugModeChanged);
        domainEventHandler.register(CameraBehaviourChangedEvent.class, this::onCameraBehaviourChanged);
    }

    /**
     * @return a {@link CompletableFutureTask} that loads the 3d-editor and returns it.
     */
    public CompletableFutureTask<Editor> loadEditor() {

        return this.<Editor>createFutureTask()
                .withNotificationMessageOnError("Application.Main.Editor.Initialization.Error")
                .withLoggerMessageOnError("Error while initializing 3D-Editor")
                .withTask(this::initializeEditor);
    }

    private Editor initializeEditor() {

        Settings settings = settingsService.get();

        List<AppState> initialAppStates = new ArrayList<>();

        if (settings.getAdvanced().getDebugMode().isEnabled()) {

            initialAppStates.add(new StatsAppState());
            initialAppStates.add(new DebugKeysAppState());
        }

        CameraBehaviour cameraBehaviour = settings.getKeymap().getCameraBehaviour();
        Class<? extends CameraKeymap> cameraKeymap = cameraBehaviour == CameraBehaviour.BLENDER ? CameraKeymapBlender.class : CameraKeymapCinema4D.class;

        this.editor = Editor.create(cameraKeymap, initialAppStates);

        return editor;
    }

    public void openSettings() {
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

    private void onDebugModeChanged(DebugModeChangedEvent event) {

        Preconditions.checkNotNull(editor, "Editor must be initialized before debug mode can be changed.");

        DebugMode debugMode = event.getNewDebugMode();

        if (debugMode.isEnabled()) {

            editor.getStateManager().attach(new StatsAppState());
            editor.getStateManager().attach(new DebugKeysAppState());

        } else {

            editor.getStateManager().detach(editor.getStateManager().getState(StatsAppState.class));
            editor.getStateManager().detach(editor.getStateManager().getState(DebugKeysAppState.class));
        }
    }


    private void onCameraBehaviourChanged(CameraBehaviourChangedEvent event) {

        Preconditions.checkNotNull(editor, "Editor must be initialized before camera behaviour can be changed.");

        CameraBehaviour cameraBehaviour = event.getNewCameraBehaviour();

        if (cameraBehaviour == CameraBehaviour.BLENDER)
            editor.setCameraKeymap(CameraKeymapBlender.class);
        else
            editor.setCameraKeymap(CameraKeymapCinema4D.class);
    }
}
