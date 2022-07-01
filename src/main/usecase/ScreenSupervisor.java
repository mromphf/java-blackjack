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

public class ScreenSupervisor {

    private Scene scene;

    private final Stage stage;
    private final Stack<Screen> navHistory = new Stack<>();
    private final Map<Screen, Parent> sceneMap = new HashMap<>();
    private final Map<Screen, ScreenObserver> observerMap = new HashMap<>();

    @Inject
    public ScreenSupervisor(Stage stage) {
        this.stage = stage;
    }

    public void initializeListeners(Map<Screen, ScreenObserver> listenerMap) {
        this.observerMap.putAll(listenerMap);
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

    public void onAlertEvent(Alert event) {
        event.initOwner(stage);
    }

    public void switchTo(Screen screen) {
        switchToScreen(screen);
    }

    private void switchToScreen(Screen screen) {
        if (screen == BACK) {
            final Screen previousScreen = navHistory.pop();
            scene.setRoot(sceneMap.get(previousScreen));
            observerMap.get(previousScreen).onScreenChanged();
        } else {
            navHistory.add(screen);
            scene.setRoot(sceneMap.get(screen));
            observerMap.get(screen).onScreenChanged();
        }
    }
}
