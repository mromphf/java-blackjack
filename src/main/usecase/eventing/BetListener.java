package main.usecase.eventing;

import main.domain.Bet;
import main.common.Identifiable;

public interface BetListener extends Identifiable {
    void onBetEvent(Event<Bet> event);
}
