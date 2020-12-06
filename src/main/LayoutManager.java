package main;

import javafx.scene.Parent;
import javafx.scene.Scene;
import main.usecase.NavListener;

import java.util.Map;

public class LayoutManager implements NavListener  {

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
    public void onStopPlaying() {
        scene.setRoot(layoutMap.get(Layout.HOME));
    }
}
