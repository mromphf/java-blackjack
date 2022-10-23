package main.usecase;

import main.domain.model.Action;
import main.domain.model.Bets;
import main.domain.model.Table;

public interface Game {
    Table peek();
    Table submitAction(Action action);
    void placeBets(Bets bets);
}
