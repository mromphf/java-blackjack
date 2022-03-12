package main;

import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.injection.EventingInjectionModule;
import main.adapter.injection.FXMLInjectionModule;
import main.adapter.storage.FileSystem;
import main.adapter.ui.blackjack.ImageMap;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;
import static main.adapter.storage.FileSystem.directoryMap;
import static main.adapter.storage.FileSystem.resourceMap;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        final FileSystem fileSystem = new FileSystem(directoryMap());

        final BaseInjectionModule baseInjectionModule = new BaseInjectionModule(fileSystem);
        final Injector baseInjector = createInjector(baseInjectionModule);

        final FXMLInjectionModule fxmlInjectionModule = new FXMLInjectionModule(stage, baseInjector, resourceMap());
        final Injector fxmlInjector = createInjector(fxmlInjectionModule);

        final EventingInjectionModule eventingInjectionModule = new EventingInjectionModule(baseInjector, fxmlInjector);
        final Injector eventingInjector = createInjector(eventingInjectionModule);

        final AppRoot appRoot = new AppRoot(baseInjector, fxmlInjector, eventingInjector);

        appRoot.init();
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");

        ImageMap.load();

        launch(args);
    }
}
