package main.adapter.injection;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.adapter.ui.bet.BetController;
import main.adapter.ui.blackjack.BlackjackController;
import main.adapter.ui.history.HistoryController;
import main.adapter.ui.home.HomeController;
import main.adapter.ui.registration.RegistrationController;
import main.usecase.Layout;
import main.usecase.LayoutManager;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import static main.usecase.Layout.*;

public class FXMLInjectionModule extends AbstractModule {

    final Injector baseInjector;
    final Map<Layout, FXMLLoader> resourceMap;
    final Stage stage;

    public FXMLInjectionModule(Stage stage, Injector baseInjector, Map<Layout, FXMLLoader> resourceMap) {
        this.baseInjector = baseInjector;
        this.resourceMap = resourceMap;
        this.stage = stage;
    }

    @Override
    public void configure() {
        final HistoryController historyController = baseInjector.getInstance(HistoryController.class);
        final BlackjackController blackjackController = baseInjector.getInstance(BlackjackController.class);
        final BetController betController = baseInjector.getInstance(BetController.class);
        final HomeController homeController = baseInjector.getInstance(HomeController.class);

        resourceMap.get(HISTORY).setControllerFactory(params -> historyController);
        resourceMap.get(GAME).setControllerFactory(params -> blackjackController);
        resourceMap.get(BET).setControllerFactory(params -> betController);
        resourceMap.get(HOME).setControllerFactory(params -> homeController);

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

        bind(BlackjackController.class)
                .toInstance(blackjackController);

        bind(HomeController.class)
                .toInstance(resourceMap.get(HOME).getController());

        bind(RegistrationController.class)
                .toInstance(resourceMap.get(REGISTRATION).getController());

        bind(Scene.class).toInstance(scene);

        bind(LayoutManager.class)
                .toInstance(new LayoutManager(stage, scene, layoutMap));
    }
}
