package main.usecase;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.usecase.eventing.AlertListener;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.util.Map;
import java.util.Stack;

import static main.adapter.injection.Bindings.LAYOUT_MAP;
import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HOME;

public class LayoutManager extends EventConnection implements LayoutListener, AlertListener {

    private final Scene scene;
    private final Stage stage;
    private final Map<Layout, Parent> layoutMap;
    private final Stack<Layout> navHistory = new Stack<>();

    @Inject
    public LayoutManager(Stage stage, Scene scene, @Named(LAYOUT_MAP) Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.stage = stage;
        this.layoutMap = layoutMap;
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
