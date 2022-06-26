package main.usecase;

import com.google.inject.Inject;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.usecase.eventing.LayoutListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HOME;

public class LayoutManager implements LayoutListener {

    private Scene scene;

    private final Stage stage;
    private final Stack<Layout> navHistory = new Stack<>();
    private final Map<Layout, Parent> sceneMap = new HashMap<>();
    private final Map<Layout, LayoutListener> listenerMap = new HashMap<>();

    @Inject
    public LayoutManager(Stage stage) {
        this.stage = stage;
    }

    public void initializeListeners(Map<Layout, LayoutListener> listenerMap) {
        this.listenerMap.putAll(listenerMap);
    }

    public void initializeLayout(Map<Layout, Parent> sceneMap) {
        this.sceneMap.putAll(sceneMap);

        this.scene = new Scene(sceneMap.get(HOME));

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();

        onChangeLayout(HOME);
    }

    public void onAlertEvent(Alert event) {
        event.initOwner(stage);
    }

    @Override
    public void onLayoutEvent(Layout event) {
        onChangeLayout(event);
    }

    private void onChangeLayout(Layout layout) {
        if (layout == BACK) {
            navHistory.pop();
            scene.setRoot(sceneMap.get(navHistory.peek()));
            listenerMap.get(navHistory.peek()).onLayoutEvent(layout);
        } else {
            navHistory.add(layout);
            scene.setRoot(sceneMap.get(navHistory.peek()));
            listenerMap.get(layout).onLayoutEvent(layout);
        }
    }
}
