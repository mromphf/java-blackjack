package main.usecase;

import main.domain.model.Action;
import main.domain.model.Bets;
import main.domain.model.Table;

public interface Game {
    double deckProgress();
    Table peek();
    Table action(Action action);
    void bet(Bets bets);
}
