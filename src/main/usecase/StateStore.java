package main.usecase;


import main.domain.model.Card;
import main.domain.model.Table;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static main.domain.predicate.LowOrderPredicate.startOfRound;

public class StateStore implements TableObserver {

    private final StateRepository stateRepository;
    private final Set<UUID> deckSet;
    private final Set<Card> cardsDrawn;

    @Inject
    public StateStore(final StateRepository stateRepository) {
        this.stateRepository = stateRepository;

        this.cardsDrawn = new HashSet<>();
        this.deckSet = new HashSet<>();
    }

    @Override
    public void newRoundStarted(Table table) {
        saveDeckIfNew(table);
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

        for (Card c : table.allPlayerCards()) {
            if (!cardsDrawn.contains(c)) {
                stateRepository.saveCardDrawn(
                        table.timestamp(),
                        table.playerHand().key(),
                        c.key(),
                        table.playerAccountKey(),
                        table.roundKey()
                );

                cardsDrawn.add(c);
            }
        }


        saveDeckIfNew(table);
    }

    private void saveDeckIfNew(Table table) {
        if (!deckSet.contains(table.deckKey())) {
            deckSet.add(table.deckKey());
            stateRepository.saveNewDeck(table.deck());
        }
    }
}
