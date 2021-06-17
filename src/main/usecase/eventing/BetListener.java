package main.usecase.eventing;

import main.domain.Bet;

public interface BetListener {
    void onBetEvent(Event<Bet> event);
}
