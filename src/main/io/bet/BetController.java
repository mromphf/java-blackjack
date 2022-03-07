package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.domain.Account;
import main.domain.Bet;
import main.domain.Snapshot;
import main.io.graphics.ImageReelAnimation;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static javafx.application.Platform.runLater;
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.*;


public class BetController extends EventConnection implements Initializable, AccountListener, SnapshotListener {

    @FXML
    private Canvas cvsScroller;

    @FXML
    private ProgressBar prgDeck;

    @FXML
    private Label lblBet;

    @FXML
    private Label lblBalance;

    @FXML
    public Button btnDeal;

    @FXML
    public Button btnBet1;

    @FXML
    public Button btnBet5;

    @FXML
    public Button btnBet10;

    @FXML
    public Button btnBet25;

    @FXML
    public Button btnBet100;

    private final static int MAX_BET = 500;
    private final UUID key = randomUUID();
    private ImageReelAnimation animation;
    private int bet = 0;
    private int balance = 0;

    private Account selectedAccount;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final GraphicsContext graphics = cvsScroller.getGraphicsContext2D();

        prgDeck.setProgress(100);

        btnBet1.setOnMouseClicked(event -> onBet(event, 1));
        btnBet5.setOnMouseClicked(event -> onBet(event, 5));
        btnBet10.setOnMouseClicked(event -> onBet(event, 10));
        btnBet25.setOnMouseClicked(event -> onBet(event, 25));
        btnBet100.setOnMouseClicked(event -> onBet(event, 100));

        animation = new ImageReelAnimation(graphics, true);

        new Thread(() -> animation.start(), "Bet Screen Animation Thread").start();
    }

    @FXML
    private void onDeal() {
        final UUID accountKey = selectedAccount.getKey();
        final Bet betByAccount = Bet.of(now(), accountKey, bet);

        eventNetwork.onBetEvent(new Event<>(key, now(), BET_PLACED, betByAccount));
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, GAME));

        bet = 0;
    }

    @FXML
    public void onQuit() {
        eventNetwork.onLayoutChangedTo(HOME, key);
        prgDeck.setProgress(100f);
        bet = 0;
    }

    @FXML
    public void onHistory() {
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, HISTORY));
    }

    @FXML
    public void onClickScroller() {
        animation.switchDirection();
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(CURRENT_BALANCE)) {
            this.balance = event.getData().getBalance();

            runLater(() -> {
                btnDeal.setDisable(bet > balance || bet <= 0);
                lblBet.setText(format("$%s", bet));
                lblBalance.setText(format("Balance: $%s", balance));
            });
        } else if (event.is(ACCOUNT_SELECTED)) {
            this.selectedAccount = event.getData();
        }
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        prgDeck.setProgress(snapshot.getDeckProgress());
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            this.bet = Math.min(MAX_BET, this.bet + amount);
        } else {
            this.bet = Math.max(0, this.bet - amount);
        }
        btnDeal.setDisable(bet > balance || bet <= 0);
        lblBet.setText("$" + bet);
    }
}
