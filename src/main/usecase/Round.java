package main.usecase;

import main.domain.Card;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Round implements ControlListener {

    private final Stack<Card> deck;
    private final Collection<GameStateListener> gameStateListeners;
    private final Collection<OutcomeListener> outcomeListeners;
    private Map<String, Collection<Card>> hands;
    private int balance;
    private int bet;

    public Round(Stack<Card> deck, int balance) {
        this.deck = deck;
        this.balance = balance;
        this.gameStateListeners = new ArrayList<>();
        this.outcomeListeners = new ArrayList<>();
        this.hands = new HashMap<String, Collection<Card>>() {{
            put("dealer", new ArrayList<>());
            put("player", new ArrayList<>());
        }};
    }

    public void registerGameStateListener(GameStateListener gameStateListener) {
        gameStateListeners.add(gameStateListener);
    }

    public void registerOutcomeListener(OutcomeListener outcomeListener) {
        outcomeListeners.add(outcomeListener);
    }

    @Override
    public void onStartNewRound() {
        hands = openingHand(deck);
        gameStateListeners.forEach(gameStateListener -> gameStateListener.onUpdate(gameState()));
    }

    @Override
    public void onBetPlaced(int bet) {
        this.bet = bet;
        gameStateListeners.forEach(gameStateListener -> gameStateListener.onUpdate(gameState()));
    }

    @Override
    public void onMoveToBettingTable() {
        bet = 0;
        gameStateListeners.forEach(gameStateListener -> gameStateListener.onUpdate(gameState()));
        if (balance <= 0) {
            System.out.println("Balance has reached $0.00! Please leave the casino.");
            System.exit(0);
        }
    }

    @Override
    public void onHit() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        } else {
            final Collection<Card> playerHand = hands.get("player");
            playerHand.add(deck.pop());
            gameStateListeners.forEach(l -> l.onUpdate(gameState()));

            if (isBust(playerHand)) {
                balance -= bet;
                outcomeListeners.forEach(l -> l.onBust(gameState()));
            }
        }
    }

    @Override
    public void onDealerTurn() {
        final Collection<Card> playerHand = hands.get("player");
        final Collection<Card> dealerHand = hands.get("dealer");

        while (score(dealerHand) < 16) {
            if (deck.isEmpty()) {
                System.out.println("No more cards! Quitting...");
                System.exit(0);
            }
            dealerHand.add(deck.pop());
        }

        if (playerWins(playerHand, dealerHand)) {
            balance += bet;
            outcomeListeners.forEach(l -> l.onPlayerWins(gameState()));
        } else if(isPush(playerHand, dealerHand)) {
            outcomeListeners.forEach(l -> l.onPush(gameState()));
        } else if (isBust(playerHand)) {
            balance -= bet;
            outcomeListeners.forEach(l -> l.onBust(gameState()));
        } else {
            balance -= bet;
            outcomeListeners.forEach(l -> l.onDealerWins(gameState()));
        }
    }

    @Override
    public void onDouble() {
        bet *= 2;
        onHit();
        onDealerTurn();
    }

    private GameState gameState() {
        final boolean atLeastOneCardDrawn = hands.get("player").size() > 2;
        return new GameState(bet, balance, deck.size(), atLeastOneCardDrawn, hands.get("dealer"), hands.get("player") );
    }
}
