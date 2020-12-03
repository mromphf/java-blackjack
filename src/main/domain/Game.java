package main.domain;

import main.usecase.Outcome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;

import static main.domain.Deck.openingHand;
import static main.domain.Rules.*;

public class Game {

    private Collection<Card> dealerHand;
    private Collection<Card> playerHand;
    private final Stack<Card> deck;
    private int balance;
    private int bet;

    public Game(int balance, Stack<Card> deck) {
        this.balance = balance;
        this.bet = 0;
        this.deck = deck;
        this.dealerHand = new ArrayList<>();
        this.playerHand = new ArrayList<>();
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public void dealOpeningHand() {
        try {
            Map<String, Collection<Card>> openingHand = openingHand(deck);
            playerHand = openingHand.get("player");
            dealerHand = openingHand.get("dealer");
        } catch (IllegalArgumentException ex) {
            System.out.println("Not enough cards to deal new hand! Quitting...");
            System.exit(0);
        }
    }

    public void doubleBet() {
        bet *= 2;
    }

    public void winBet() {
        balance += bet;
    }

    public void loseBet() {
        balance -= bet;
    }

    public void addCardToDealerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        dealerHand.add(deck.pop());
    }

    public void addCardToPlayerHand() {
        if (deck.isEmpty()) {
            System.out.println("No more cards! Quitting...");
            System.exit(0);
        }
        playerHand.add(deck.pop());
    }

    public Outcome determineOutcome() {
        if (playerWins(playerHand, dealerHand)) {
            winBet();
            return Outcome.WIN;
        } else if(isPush(playerHand, dealerHand)) {
            return Outcome.PUSH;
        } else if (isBust(playerHand)) {
            loseBet();
            return Outcome.BUST;
        } else {
            loseBet();
            return Outcome.LOSE;
        }
    }

    public boolean playerBusted() {
        return isBust(playerHand);
    }

    public boolean outOfMoney() {
        return balance <= 0;
    }

    public boolean dealerShouldHit() {
        return score(dealerHand) < 16;
    }

    public Snapshot getSnapshot() {
        final boolean atLeastOneCardDrawn = playerHand.size() > 2;
        return new Snapshot(balance, bet, deck.size(), atLeastOneCardDrawn, dealerHand, playerHand);
    }
}
