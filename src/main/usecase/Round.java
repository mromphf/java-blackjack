package main.usecase;

import main.AppRoot;
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
    private int bet = 0;

    public Round(AppRoot appRoot, Stack<Card> deck, Map<String, List<Card>> hands) {
        this.appRoot = appRoot;
        this.deck = deck;
        this.hands = hands;
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
        this.bet = 0;
        appRoot.setLayout(Layout.BET);
    }

    public void start(int bet) {
        this.bet = bet;
        appRoot.setLayout(Layout.GAME);
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

    public void reset() {
        this.hands = openingHand(deck);
    }
}
