package main.usecase;


import main.domain.model.Card;
import main.domain.model.TableView;

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
    public void newRoundStarted(TableView tableView) {
        saveDeckIfNew(tableView);
    }

    @Override
    public void onUpdate(TableView tableView) {
        if (startOfRound.test(tableView)) {
            stateRepository.saveNewRound(tableView);
        } else {
            stateRepository.saveLastActionTaken(tableView);
        }

        for (Card c : tableView.dealerHand()) {
            if (!cardsDrawn.contains(c)) {
                stateRepository.saveDealerCard(
                        tableView.timestamp(),
                        c.key()
                );

                cardsDrawn.add(c);
            }
        }

        for (Card c : tableView.allPlayerCards()) {
            if (!cardsDrawn.contains(c)) {
                stateRepository.saveCardDrawn(
                        tableView.timestamp(),
                        tableView.playerHand().key(),
                        c.key(),
                        tableView.playerAccountKey(),
                        tableView.roundKey()
                );

                cardsDrawn.add(c);
            }
        }

        saveDeckIfNew(tableView);
    }

    private void saveDeckIfNew(TableView tableView) {
        if (!deckSet.contains(tableView.deckKey())) {
            deckSet.add(tableView.deckKey());
            stateRepository.saveNewDeck(tableView);
            stateRepository.saveNewCards(tableView.deck());
        }
    }
}
