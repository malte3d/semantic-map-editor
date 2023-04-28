package com.malte3d.suturo.sme.ui;

import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.jme3.util.LWJGLBufferAllocator;
import com.malte3d.suturo.commons.ddd.event.domain.DomainEventPublisher;
import com.malte3d.suturo.commons.javafx.CompletableFutureTask;
import com.malte3d.suturo.commons.javafx.FxmlLoaderUtil;
import com.malte3d.suturo.commons.javafx.GlobalExecutor;
import com.malte3d.suturo.commons.messages.Messages;
import com.malte3d.suturo.sme.domain.model.application.settings.advanced.DebugMode;
import com.malte3d.suturo.sme.domain.service.application.settings.SettingsRepository;
import com.malte3d.suturo.sme.ui.util.UiResources;
import com.malte3d.suturo.sme.ui.view.MainView;
import com.malte3d.suturo.sme.ui.viewmodel.ExitApplicationEvent;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import lombok.NonNull;
import org.lwjgl.system.Configuration;
import org.scenicview.ScenicView;

import java.util.Objects;
import java.util.concurrent.Executor;

public class MainApplication extends Application implements Provider<HostServices> {

    private static Injector injector;

    private final DomainEventPublisher domainEventPublisher;

    private final MainApplicationViewFactory viewFactory;
    private final SettingsRepository settingsRepository;

    private CompletableFutureTask<Parent> mainViewFuture;

    public MainApplication() {

        Preconditions.checkNotNull(injector, "MainApplication has to be launched with the Injector initialized!");

        Executor globalExecutor = injector.getInstance(Key.get(Executor.class, GlobalExecutor.class));

        this.domainEventPublisher = injector.getInstance(DomainEventPublisher.class);

        this.viewFactory = new MainApplicationViewFactory(globalExecutor, injector);
        this.settingsRepository = injector.getInstance(SettingsRepository.class);

        registerEvents();
    }

    private void registerEvents() {
        domainEventPublisher.register(ExitApplicationEvent.class, MainApplication::exit);
    }

    @Override
    public void init() {

        FxmlLoaderUtil.init(injector);
        mainViewFuture = viewFactory.loadView(MainView.class);
    }

    @Override
    public void start(Stage stage) {

        DebugMode debugMode = settingsRepository.load().advancedSettings().debugMode();

        stage.setTitle(Messages.getString("Application.Name"));
        stage.getIcons().add(UiResources.APP_ICON);
        stage.setOnCloseRequest(windowEvent -> exit());

        mainViewFuture.thenConsume(mainView -> {

            Scene scene = new Scene(mainView, 1200, 800);
            scene.setRoot(mainView);

            JMetro jMetro = new JMetro(scene, Style.LIGHT);
            jMetro.getOverridingStylesheets().add(Objects.requireNonNull(MainView.class.getResource("sme-base.css")).toExternalForm());

            stage.setScene(scene);
            stage.show();

            if (debugMode.isEnabled())
                ScenicView.show(mainView);

            stage.toFront();
        });
    }

    public static void exit() {
        Platform.exit();
        System.exit(0);
    }

    public static void launch(@NonNull MainApplicationOptions options) {

        MainApplication.injector = options.getInjector();

        setupJfxLwjglEnvironment();

        launch(options.getArgs());
    }

    /**
     * Some general settings for JFX/LWJGL for maximum compatibility
     */
    private static void setupJfxLwjglEnvironment() {

        Configuration.GLFW_CHECK_THREAD0.set(false); // need to disable to work on macOS
        Configuration.MEMORY_ALLOCATOR.set("jemalloc"); // use jemalloc
        System.setProperty("prism.lcdtext", "false"); // force antialiasing in JavaFX fonts
        System.setProperty(LWJGLBufferAllocator.PROPERTY_CONCURRENT_BUFFER_ALLOCATOR, "true");
    }

    @Override
    public HostServices get() {
        return getHostServices();
    }
}
