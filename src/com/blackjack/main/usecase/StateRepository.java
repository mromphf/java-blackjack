package com.blackjack.main.usecase;

import com.blackjack.main.domain.model.Deck;
import com.blackjack.main.domain.model.TableView;

import java.time.LocalDateTime;
import java.util.UUID;

public interface StateRepository {
    void saveNewRound(final TableView tableView);
    void saveNewDeck(final TableView tableView);
    void saveNewCards(final Deck deck);
    void saveCardDrawn(final LocalDateTime timestamp,
                       final UUID handKey, final UUID cardKey,
                       final UUID accountKey, final UUID roundKey);
    void saveDealerCard(final LocalDateTime timestamp, final UUID cardKey);
    void saveLastActionTaken(TableView tableView);
}
