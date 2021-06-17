package main.io.bet;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.domain.Account;
import main.domain.Bet;
import main.io.EventConnection;
import main.usecase.Layout;
import main.usecase.eventing.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.*;


public class BetController extends EventConnection implements Initializable, BalanceListener, LayoutListener {

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
    private int bet = 0;
    private int balance = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnBet1.setOnMouseClicked(event -> onBet(event, 1));
        btnBet5.setOnMouseClicked(event -> onBet(event, 5));
        btnBet10.setOnMouseClicked(event -> onBet(event, 10));
        btnBet25.setOnMouseClicked(event -> onBet(event, 25));
        btnBet100.setOnMouseClicked(event -> onBet(event, 100));
    }

    @FXML
    private void onDeal() {
        final Account selectedAccount = eventNetwork.fulfill(ACCOUNT_SELECTED).getAccount();
        final Bet betByAccount = Bet.of(LocalDateTime.now(), selectedAccount.getKey(), bet);
        eventNetwork.onBetEvent(new Event<>(BET_PLACED, betByAccount));
        eventNetwork.onLayoutEvent(new Event<>(LAYOUT_CHANGED, GAME));
        bet = 0;
    }

    @FXML
    public void onQuit() {
        eventNetwork.onLayoutEvent(new Event<>(LAYOUT_CHANGED, HOME));
        bet = 0;
    }

    @FXML
    public void onHistory() {
        eventNetwork.onLayoutEvent(new Event<>(LAYOUT_CHANGED, HISTORY));
    }

    @Override
    public void onBalanceUpdated() {
        this.balance = eventNetwork.fulfill(CURRENT_BALANCE).getCurrentBalance();

        btnDeal.setDisable(bet > balance || bet <= 0);
        lblBet.setText("$" + bet);
        lblBalance.setText(String.format("Balance: $%s", balance));
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == BET) {
            onBalanceUpdated();
        }
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
