package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.io.injection.InjectionModule;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new AppRoot(stage, createInjector(new InjectionModule()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
