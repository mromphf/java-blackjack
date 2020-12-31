package main.usecase;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.domain.Account;
import main.io.EventListener;

import java.util.Map;

public class LayoutManager extends EventListener implements NavListener, BalanceListener {

    private final Scene scene;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.layoutMap = layoutMap;
    }

    @Override
    public void onChangeLayout(Layout layout) {
        scene.setRoot(layoutMap.get(layout));
    }

    @Override
    public void onChangeLayout(Layout layout, Account account) {
        scene.setRoot(layoutMap.get(layout));
    }

    @Override
    public void onBalanceUpdated(Account account) {
        //TODO: This won't allow a player to bet the last of their money.
        if (account.getBalance() <= 0 ) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
        }
    }
}
