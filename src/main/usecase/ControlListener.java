package main.usecase;

import main.domain.Action;

public interface ControlListener {
    void onActionTaken(Action action);
    void onSettleHand();
}
