package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import main.io.EventConnection;
import main.usecase.eventing.AlertListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.LayoutListener;

import java.util.Map;
import java.util.Stack;

import static main.usecase.Layout.BACK;
import static main.usecase.eventing.Predicate.LAYOUT_ALERT;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;

public class LayoutManager extends EventConnection implements LayoutListener, AlertListener {

    private final Stack<Layout> navHistory;
    private final Scene scene;
    private final Stage stage;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Stage stage, Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.stage = stage;
        this.layoutMap = layoutMap;
        this.navHistory = new Stack<>();
    }

    @Override
    public void onAlertEvent(Event<Alert> event) {
        if (event.is(LAYOUT_ALERT)) {
            event.getData().initOwner(stage);
        }
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED)) {
            onChangeLayout(event.getData());
        }
    }

    public void onChangeLayout(Layout layout) {
        if (layout == BACK) {
            navHistory.pop();
            scene.setRoot(layoutMap.get(navHistory.peek()));
        } else {
            navHistory.add(layout);
            scene.setRoot(layoutMap.get(layout));
        }
    }
}
