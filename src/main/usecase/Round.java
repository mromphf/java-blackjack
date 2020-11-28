package main.usecase;

import main.AppRoot;
import main.Layout;
import main.domain.Card;
import main.domain.Rules;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Round implements ControlListener {

    private final AppRoot appRoot;
    private final Stack<Card> deck;
    private final Collection<RoundListener> roundListeners;
    private Map<String, List<Card>> hands;
    private int bet;

    public Round(AppRoot appRoot, Stack<Card> deck) {
        this.appRoot = appRoot;
        this.deck = deck;
        this.roundListeners = new ArrayList<>();
        this.hands = new HashMap<String, List<Card>>() {{
            put("dealer", new ArrayList<>());
            put("player", new ArrayList<>());
        }};
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

    public void registerBetListener(RoundListener roundListener) {
        this.roundListeners.add(roundListener);
    }

    public boolean playerBusted() {
        return bust(hands.get("player"));
    }

    public boolean playerHasWon() {
        return Rules.playerWins(hands.get("player"), hands.get("dealer"));
    }

    public boolean isPush() {
        return push(hands.get("player"), hands.get("dealer"));
    }

    public int getBet() {
        return bet;
    }

    public List<Card> getHand(String player) {
        return hands.get(player);
    }

    public int numCardsRemaining() {
        return deck.size();
    }

    public void moveToBettingTable() {
        onMoveToBettingTable();
    }

    public void hit() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        } else {
            hands.get("player").add(deck.pop());
            roundListeners.forEach(roundListener -> roundListener.onUpdate(gameState()));
        }
    }

    public void dealerTurn() {
        while (score(hands.get("dealer")) < 16) {
            if (deck.isEmpty()) {
                System.out.println("No more cards! Quitting...");
                System.exit(0);
            }
            hands.get("dealer").add(deck.pop());
            roundListeners.forEach(roundListener -> roundListener.onUpdate(gameState()));
        }
    }

    private GameState gameState() {
        return new GameState(bet);
    }
}
