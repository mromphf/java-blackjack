package main.usecase;

import main.domain.Action;

public interface ActionListener {
    void onActionTaken(Action action);
    void onBetPlaced(int amount);
}
