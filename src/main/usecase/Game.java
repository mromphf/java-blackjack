package main.usecase;

import main.domain.model.Action;
import main.domain.model.Bets;
import main.domain.model.TableView;

public interface Game {
    TableView peek();
    TableView submitAction(Action action);
    void placeBets(Bets bets);
}
