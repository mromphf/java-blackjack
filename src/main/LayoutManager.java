package main;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.domain.Account;
import main.usecase.NavListener;
import main.usecase.TransactionListener;

import java.util.Map;

public class LayoutManager implements NavListener, TransactionListener {

    private final Scene scene;
    private final Map<Layout, Parent> layoutMap;

    public LayoutManager(Scene scene, Map<Layout, Parent> layoutMap) {
        this.scene = scene;
        this.layoutMap = layoutMap;
    }

    @Override
    public void onStartNewRound(int bet) {
        scene.setRoot(layoutMap.get(Layout.GAME));
    }

    @Override
    public void onMoveToBettingTable() {
        scene.setRoot(layoutMap.get(Layout.BET));
    }

    @Override
    public void onMoveToBettingTable(Account account) {
        onMoveToBettingTable();
    }

    @Override
    public void onStopPlaying() {
        scene.setRoot(layoutMap.get(Layout.HOME));
    }

    @Override
    public void onBalanceChanged(Account account) {
        //TODO: This won't allow a player to bet the last of their money.
        if (account.getBalance() <= 0 ) {
            System.out.println("You are out of money! Please leave the casino...");
            System.exit(0);
        }
    }
}
