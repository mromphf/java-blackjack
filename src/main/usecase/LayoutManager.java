package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.usecase.eventing.AlertListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HOME;

public class LayoutManager extends EventConnection implements LayoutListener, AlertListener {

    private final Scene scene;
    private final Stage stage;
    private final Map<Layout, Parent> layoutMap;
    private final Stack<Layout> navHistory;

    public LayoutManager(Stage stage, Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.stage = stage;
        this.layoutMap = layoutMap;
        this.navHistory = new Stack<>();
    }

    public void initializeLayout() {
        onChangeLayout(HOME);

        stage.setScene(scene);
        stage.setTitle("Blackjack");
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();
    }

    @Override
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
            scene.setRoot(layoutMap.get(navHistory.peek()));
        } else {
            navHistory.add(layout);
            scene.setRoot(layoutMap.get(layout));
        }
    }
}
