package main.io.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import main.AppRoot;
import main.io.bet.BetController;
import main.io.blackjack.BlackjackController;
import main.io.history.HistoryController;
import main.io.home.HomeController;
import main.io.registration.RegistrationController;
import main.usecase.Layout;
import main.usecase.LayoutManager;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.UUID.randomUUID;
import static main.usecase.Layout.*;

public class FXMLInjectionModule extends AbstractModule {

    final Injector baseInjector;
    final Map<Layout, FXMLLoader> resourceMap;

    public FXMLInjectionModule(Injector baseInjector, Map<Layout, FXMLLoader> resourceMap) {
        this.baseInjector = baseInjector;
        this.resourceMap = resourceMap;
    }

    @Override
    public void configure() {
        HistoryController historyController = baseInjector.getInstance(HistoryController.class);

        resourceMap.get(HISTORY).setControllerFactory(params -> historyController);

        resourceMap.keySet().forEach(k -> {
            try {
                resourceMap.get(k).load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        final Map<Layout, Parent> layoutMap = resourceMap.keySet()
                .stream()
                .collect(Collectors.toMap(layout -> layout, layout -> resourceMap.get(layout).getRoot()));

        final Scene scene = new Scene(layoutMap.get(HOME));

        bind(BetController.class)
                .toInstance(resourceMap.get(BET).getController());

        bind(BlackjackController.class)
                .toInstance(resourceMap.get(GAME).getController());

        bind(HistoryController.class)
                .toInstance(historyController);

        bind(HomeController.class)
                .toInstance(resourceMap.get(HOME).getController());

        bind(RegistrationController.class)
                .toInstance(resourceMap.get(REGISTRATION).getController());

        bind(Scene.class).toInstance(scene);

        bind(LayoutManager.class)
                .toInstance(new LayoutManager(randomUUID(), AppRoot.stage, scene, layoutMap));
    }
}
