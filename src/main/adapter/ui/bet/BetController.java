package main.adapter.ui.bet;

import com.google.inject.Inject;
import com.google.inject.name.Named;
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
import main.adapter.injection.Bindings;
import main.domain.model.Account;
import main.domain.model.Snapshot;
import main.usecase.AccountService;
import main.usecase.Layout;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;
import main.usecase.eventing.SnapshotListener;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static javafx.application.Platform.runLater;
import static main.domain.model.Transaction.transaction;
import static main.usecase.Layout.*;


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

    private final AccountService accountService;
    private final static int MAX_BET = 500;
    private final int maxCards;

    private ImageReelAnimation animation;
    private int bet = 0;


    @Inject
    public BetController(AccountService accountService, @Named(Bindings.MAX_CARDS) int maxCards) {
        this.accountService = accountService;
        this.maxCards = maxCards;
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
    public void onLayoutEvent(Layout event) {
        if (event == BET)  {
            final Optional<Account> account = accountService.getCurrentlySelectedAccount();

            if (account.isPresent()) {
                final int balance = account.get().getBalance();
                runLater(() -> refreshUI(balance, bet));
            }
        }
    }

    @FXML
    private void onDeal() {
        final Optional<Account> account = accountService.getCurrentlySelectedAccount();

        if (account.isPresent()) {
            final UUID accountKey = account.get().getKey();
            final String description = "BET";
            final int betVal = (bet * -1);

            eventNetwork.onTransactionIssued(transaction(now(), accountKey, description, betVal));
            eventNetwork.onLayoutEvent(GAME);

            bet = 0;
        }
    }

    @FXML
    public void onQuit() {
        eventNetwork.onLayoutEvent(HOME);
        prgDeck.setProgress(100f);
        bet = 0;
    }

    @FXML
    public void onHistory() {
        eventNetwork.onLayoutEvent(HISTORY);
    }

    @FXML
    public void onClickScroller() {
        animation.switchDirection();
    }

    @Override
    public void onGameUpdate(Snapshot snapshot) {
        prgDeck.setProgress((float) snapshot.getDeckSize() / maxCards);
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        final Optional<Account> selectedAccount = accountService.getCurrentlySelectedAccount();

        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            this.bet = Math.min(MAX_BET, this.bet + amount);
        } else {
            this.bet = Math.max(0, this.bet - amount);
        }

        if (selectedAccount.isPresent()) {
            final int balance = selectedAccount.get().getBalance();

            refreshUI(balance, bet);
        }
    }

    private void refreshUI(int balance, int bet) {
        btnDeal.setDisable(bet > balance || bet <= 0);
        lblBet.setText("$" + bet);
        lblBalance.setText(format("Balance: $%s", balance));
    }
}
