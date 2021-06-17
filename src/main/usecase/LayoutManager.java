package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.io.EventConnection;
import main.usecase.eventing.EventListener;
import main.usecase.eventing.Message;

import java.util.Map;
import java.util.Stack;

import static main.usecase.Layout.BACK;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;

public class LayoutManager extends EventConnection implements EventListener {

    private final Stack<Layout> navHistory;
    private final Scene scene;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.layoutMap = layoutMap;
        this.navHistory = new Stack<>();
    }

    @Override
    public void onEvent(Message message) {
        if (message.is(LAYOUT_CHANGED)) {
            onChangeLayout(message.getLayout());
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
