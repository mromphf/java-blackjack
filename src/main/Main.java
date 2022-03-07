package main;

import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;
import main.adapter.blackjack.ImageMap;
import main.adapter.injection.BaseInjectionModule;
import main.adapter.injection.ConfigInjectionModule;
import main.adapter.injection.FXMLInjectionModule;
import main.adapter.storage.FileSystem;

import static com.google.inject.Guice.createInjector;
import static java.lang.Thread.currentThread;
import static main.adapter.storage.FileSystem.directoryMap;
import static main.adapter.storage.FileSystem.resourceMap;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
       final Injector configInjector = createInjector(new ConfigInjectionModule(new FileSystem(directoryMap())));
       final Injector baseInjector = createInjector(new BaseInjectionModule(directoryMap()));
       final Injector fxmlInjector = createInjector(new FXMLInjectionModule(stage, baseInjector, resourceMap()));

       final AppRoot appRoot = new AppRoot(configInjector, baseInjector, fxmlInjector);

        appRoot.init();
    }

    public static void main(String[] args) {
        currentThread().setName("Main Thread");

        ImageMap.load();

        launch(args);
    }
}
