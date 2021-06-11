package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.domain.Account;
import main.io.EventConnection;

import java.util.*;

import static main.usecase.Layout.BACK;

public class LayoutManager extends EventConnection implements NavListener {

    private final Stack<Layout> navHistory;
    private final Scene scene;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.layoutMap = layoutMap;
        this.navHistory = new Stack<>();
    }

    @Override
    public void onChangeLayout(Layout layout) {
        if (layout == BACK) {
            navHistory.pop();
            scene.setRoot(layoutMap.get(navHistory.peek()));
        } else {
            navHistory.add(layout);
            scene.setRoot(layoutMap.get(layout));
        }
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        onChangeLayout(layout);
    }
}
