package main.usecase;

import main.domain.Account;
import main.domain.Action;

public interface ActionListener {
    void onActionTaken(Action action);
    void onBetPlaced(Account account, int amount);
}
