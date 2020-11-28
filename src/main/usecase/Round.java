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
            hands.get("player").add(deck.pop());
            roundListeners.forEach(l -> l.onUpdate(gameState()));

            if (bust(hands.get("player"))) {
                outcomeListeners.forEach(l -> l.onShowdown(gameState()));
                outcomeListeners.forEach(OutcomeListener::onBust);
            }
        }
    }

    @Override
    public void onDealerTurn() {
        while (score(hands.get("dealer")) < 16) {
            if (deck.isEmpty()) {
                System.out.println("No more cards! Quitting...");
                System.exit(0);
            }
            hands.get("dealer").add(deck.pop());
        }

        outcomeListeners.forEach(l -> l.onShowdown(gameState()));

        if (gameState().playerHasWon) {
            outcomeListeners.forEach(OutcomeListener::onPlayerWins);
        } else if(gameState().isPush) {
            outcomeListeners.forEach(OutcomeListener::onPush);
        } else {
            outcomeListeners.forEach(OutcomeListener::onDealerWins);
        }
    }

    private GameState gameState() {
        return new GameState(
                bet,
                deck.size(),
                hands.get("player").size() > 2,
                bust(hands.get("player")),
                push(hands.get("player"), hands.get("dealer")),
                Rules.playerWins(hands.get("player"), hands.get("dealer")),
                hands.get("dealer"),
                hands.get("player")
        );
    }
}
