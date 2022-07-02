package main.usecase;

import com.google.inject.Inject;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static main.usecase.Screen.BACK;
import static main.usecase.Screen.HOME;

public class ScreenSupervisor implements ScreenManagement, AlertService {

    private Scene scene;

    private final Stage stage;
    private final Stack<Screen> navHistory = new Stack<>();
    private final Map<Screen, Parent> sceneMap = new HashMap<>();
    private final Map<Screen, ScreenObserver> screenObservers;

    @Inject
    public ScreenSupervisor(Stage stage, Map<Screen, ScreenObserver> screenMap) {
        this.stage = stage;
        this.screenObservers = screenMap;
    }

    public void initializeLayout(Map<Screen, Parent> sceneMap) {
        this.sceneMap.putAll(sceneMap);

        this.scene = new Scene(sceneMap.get(HOME));

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

        switchToScreen(HOME);
    }

    @Override
    public void issueAlert(Alert event) {
        event.initOwner(stage);
    }

    @Override
    public void switchTo(Screen screen) {
        switchToScreen(screen);
    }

    private void switchToScreen(Screen screen) {
        if (screen == BACK && navHistory.size() < 1) {
            throw new IllegalStateException();
        }

        if (sceneMap.isEmpty()) {
            throw new IllegalStateException();
        }

        if (screen == BACK) {
            navHistory.pop();
            final Screen previousScreen = navHistory.peek();

            scene.setRoot(sceneMap.get(previousScreen));
            screenObservers.get(previousScreen).onScreenChanged();

        } else {
            navHistory.add(screen);
            scene.setRoot(sceneMap.get(screen));
            screenObservers.get(screen).onScreenChanged();
        }
    }
}
