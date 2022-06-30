package main.adapter.ui.bet;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import main.adapter.graphics.ImageReelAnimation;
import main.domain.model.Account;
import main.usecase.*;
import main.usecase.LayoutListener;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;
import static javafx.application.Platform.runLater;
import static javafx.scene.input.MouseButton.PRIMARY;
import static main.usecase.Layout.*;


public class BetController implements Initializable, LayoutListener {

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

    private final SelectionService selectionService;
    private final static int MAX_BET = 500;
    private final LayoutManager layoutManager;

    private final Game game;
    private ImageReelAnimation animation;
    private int bet = 0;


    @Inject
    public BetController(
                         SelectionService selectionService,
                         Game game,
                         LayoutManager layoutManger) {
        this.selectionService = selectionService;
        this.layoutManager = layoutManger;
        this.game = game;
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
            final Optional<Account> account = selectionService.getCurrentlySelectedAccount();

            if (account.isPresent()) {
                final int balance = account.get().getBalance();
                runLater(() -> refreshUI(balance));
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @FXML
    private void onDeal() {
        final Optional<Account> account = selectionService.getCurrentlySelectedAccount();

        if (account.isPresent()) {
            game.placeBet(bet);
            layoutManager.onLayoutEvent(GAME);
            bet = 0;
        } else {
            throw new IllegalStateException();
        }
    }

    @FXML
    public void onQuit() {
        layoutManager.onLayoutEvent(HOME);
        prgDeck.setProgress(100f);
        bet = 0;
    }

    @FXML
    public void onHistory() {
        layoutManager.onLayoutEvent(HOME);
    }

    @FXML
    public void onClickScroller() {
        animation.switchDirection();
    }

    private void onBet(MouseEvent mouseEvent, int amount) {
        if (mouseEvent.getButton() == PRIMARY) {
            bet = min(MAX_BET, bet + amount);
        } else {
            bet = max(0, bet - amount);
        }

        selectionService.getCurrentlySelectedAccount()
                .ifPresent(account -> refreshUI(account.getBalance()));
    }

    private void refreshUI(int balance) {
        btnDeal.setDisable(bet > balance || bet <= 0);
        lblBet.setText("$" + bet);
        lblBalance.setText(format("Balance: $%s", balance));
        prgDeck.setProgress(game.deckProgress());
    }
}
