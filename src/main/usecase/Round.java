package main.usecase;

import main.AppRoot;
import main.Layout;
import main.domain.Card;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Round implements ControlListener {

    private final AppRoot appRoot;
    private final Stack<Card> deck;
    private final Collection<RoundListener> roundListeners;
    private final Collection<OutcomeListener> outcomeListeners;
    private Map<String, List<Card>> hands;
    private int bet;

    public Round(AppRoot appRoot, Stack<Card> deck) {
        this.appRoot = appRoot;
        this.deck = deck;
        this.roundListeners = new ArrayList<>();
        this.outcomeListeners = new ArrayList<>();
        this.hands = new HashMap<String, List<Card>>() {{
            put("dealer", new ArrayList<>());
            put("player", new ArrayList<>());
        }};
    }

    public void registerRoundListener(RoundListener roundListener) {
        this.roundListeners.add(roundListener);
    }

    public void registerOutcomeListener(OutcomeListener outcomeListener) {
        outcomeListeners.add(outcomeListener);
    }

    @Override
    public void onStartGame() {
        hands = openingHand(deck);
        appRoot.setLayout(Layout.GAME);
        roundListeners.forEach(roundListener -> roundListener.onUpdate(gameState()));
    }

    @Override
    public void onBetPlaced(int bet) {
        this.bet = bet;
        roundListeners.forEach(roundListener -> roundListener.onUpdate(gameState()));
    }

    @Override
    public void onMoveToBettingTable() {
        bet = 0;
        appRoot.setLayout(Layout.BET);
        roundListeners.forEach(roundListener -> roundListener.onUpdate(gameState()));
    }

    @Override
    public void onHit() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        } else {
            final List<Card> playerHand = hands.get("player");
            playerHand.add(deck.pop());
            roundListeners.forEach(l -> l.onUpdate(gameState()));

            if (bust(playerHand)) {
                outcomeListeners.forEach(l -> l.onBust(gameState()));
            }
        }
    }

    @Override
    public void onDealerTurn() {
        final List<Card> playerHand = hands.get("player");
        final List<Card> dealerHand = hands.get("dealer");

        while (score(dealerHand) < 16) {
            if (deck.isEmpty()) {
                System.out.println("No more cards! Quitting...");
                System.exit(0);
            }
            dealerHand.add(deck.pop());
        }

        if (playerWins(playerHand, dealerHand)) {
            outcomeListeners.forEach(l -> l.onPlayerWins(gameState()));
        } else if(push(playerHand, dealerHand)) {
            outcomeListeners.forEach(l -> l.onPush(gameState()));
        } else if (bust(playerHand)) {
            outcomeListeners.forEach(l -> l.onBust(gameState()));
        } else {
            outcomeListeners.forEach(l -> l.onDealerWins(gameState()));
        }
    }

    private GameState gameState() {
        final boolean cardDrawn = hands.get("player").size() > 2;
        return new GameState(bet, deck.size(), cardDrawn, hands.get("dealer"), hands.get("player") );
    }
}
