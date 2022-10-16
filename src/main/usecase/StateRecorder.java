package main.usecase;


import main.domain.model.Table;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static main.domain.predicate.LowOrderPredicate.startOfRound;

public class StateRecorder implements TableObserver {

    private final StateRepository stateRepository;
    private final Set<UUID> deckSet;

    @Inject
    public StateRecorder(final StateRepository stateRepository) {
        this.stateRepository = stateRepository;
        this.deckSet = new HashSet<>();
    }

    @Override
    public void newRoundStarted(Table table) {
        if (!deckSet.contains(table.deckKey())) {
            deckSet.add(table.deckKey());
            stateRepository.saveNewDeck(table.deck());
        }
    }

    @Override
    public void onUpdate(Table table) {
        if (startOfRound.test(table)) {
            stateRepository.saveNewRound(table);
        } else {
            stateRepository.saveLastActionTaken(
                    table.timestamp(),
                    table.roundKey(),
                    table.playerAccountKey(),
                    table.lastActionTaken());
        }
    }
}
