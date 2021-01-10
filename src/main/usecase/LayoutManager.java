package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.io.EventConnection;

import java.util.Map;

import static main.usecase.Predicate.*;

public class LayoutManager extends EventConnection implements EventListener {

    private final Scene scene;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.layoutMap = layoutMap;
    }

    @Override
    public void listen(Event e) {
        //TODO: This won't allow a player to bet the last of their money.
        if (e.is(BALANCE_UPDATED)) {
            if (e.getAccount().getBalance() <= 0 ) {
                System.out.println("You are out of money! Please leave the casino...");
                System.exit(0);
            }
        } else if (e.is(LAYOUT_CHANGED)) {
            scene.setRoot(layoutMap.get(e.getLayout()));
        }
    }
}
