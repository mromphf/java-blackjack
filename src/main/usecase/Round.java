package main.usecase;

import main.AppRoot;
import main.BetListener;
import main.Layout;
import main.domain.Card;
import main.domain.Rules;

import java.util.*;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Round {

    private final AppRoot appRoot;
    private final Stack<Card> deck;
    private Map<String, List<Card>> hands;
    private int bet;
    private Collection<BetListener> betListeners;

    public Round(AppRoot appRoot, Stack<Card> deck) {
        this.appRoot = appRoot;
        this.deck = deck;
        this.betListeners = new ArrayList<>();
        this.hands = new HashMap<String, List<Card>>() {{
            put("dealer", new ArrayList<>());
            put("player", new ArrayList<>());
        }};
    }

    public void registerBetListeners(Collection<BetListener> betListeners) {
        this.betListeners = betListeners;
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

    public void placeBet() {
        appRoot.setLayout(Layout.BET);
    }

    public void start(int bet) {
        this.bet = bet;
        hands = openingHand(deck);
        appRoot.setLayout(Layout.GAME);
        betListeners.forEach(BetListener::onBetPlaced);
    }

    public void hit() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        } else {
            hands.get("player").add(deck.pop());
        }
    }

    public void dealerTurn() {
        while (score(hands.get("dealer")) < 16) {
            if (deck.isEmpty()) {
                System.out.println("No more cards! Quitting...");
                System.exit(0);
            }
            hands.get("dealer").add(deck.pop());
        }
    }
}
