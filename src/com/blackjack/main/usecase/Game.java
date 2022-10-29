package com.blackjack.main.usecase;

import com.blackjack.main.domain.model.Action;
import com.blackjack.main.domain.model.Bets;
import com.blackjack.main.domain.model.TableView;

public interface Game {
    TableView peek();
    TableView submitAction(Action action);
    void placeBets(Bets bets);
}
