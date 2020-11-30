package main.usecase;

import main.domain.StateSnapshot;

public interface OutcomeListener {
    void onDealerWins(StateSnapshot stateSnapshot);
    void onPlayerWins(StateSnapshot stateSnapshot);
    void onBust(StateSnapshot stateSnapshot);
    void onPush(StateSnapshot stateSnapshot);
}
