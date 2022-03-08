package main.adapter.ui.bet;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.adapter.graphics.ImageReelAnimation;
import main.domain.Account;
import main.domain.Bet;
import main.domain.Snapshot;
import main.usecase.AccountCache;
import main.usecase.Game;
import main.usecase.Layout;
import main.usecase.Transactor;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static javafx.application.Platform.runLater;
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;


public class BetController extends EventConnection implements Initializable, LayoutListener, SnapshotListener {

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

    private final AccountCache accountCache;
    private final Game game;
    private final Transactor transactor;
    private final static int MAX_BET = 500;
    private final UUID key = randomUUID();

    private ImageReelAnimation animation;
    private int bet = 0;


    @Inject
    public BetController(Game game, Transactor transactor, AccountCache accountCache) {
        this.accountCache = accountCache;
        this.game = game;
        this.transactor = transactor;
    }

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

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        final Optional<Account> account = accountCache.getLastSelectedAccount();

        if (event.is(LAYOUT_CHANGED) && account.isPresent())  {
            final int balance = account.get().getBalance();

            runLater(() -> {
                btnDeal.setDisable(bet > balance || bet <= 0);
                lblBet.setText(format("$%s", bet));
                lblBalance.setText(format("Balance: $%s", balance));
            });
        }
    }

    @FXML
    private void onDeal() {
        final Optional<Account> account = accountCache.getLastSelectedAccount();

        if (account.isPresent()) {
            final UUID accountKey = account.get().getKey();
            final Bet betByAccount = Bet.of(now(), accountKey, bet);

            game.onBetEvent(betByAccount);
            transactor.onBetEvent(betByAccount);
            eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, GAME));

            bet = 0;
        }
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
    public void onGameUpdate(Snapshot snapshot) {
        prgDeck.setProgress(snapshot.getDeckProgress());
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        final Optional<Account> selectedAccount = accountCache.getLastSelectedAccount();

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            this.bet = Math.min(MAX_BET, this.bet + amount);
        } else {
            this.bet = Math.max(0, this.bet - amount);
        }

        if (selectedAccount.isPresent()) {
            final int balance = selectedAccount.get().getBalance();

            btnDeal.setDisable(bet > balance || bet <= 0);
            lblBet.setText("$" + bet);
        }
    }
}
