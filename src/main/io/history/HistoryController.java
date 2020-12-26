package main.io.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import main.domain.Account;
import main.io.RootController;
import main.usecase.NavListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HistoryController extends RootController implements Initializable, NavListener {

    @FXML
    public Label lblAccount;

    @FXML
    public void onHome() {
        navListeners.forEach(NavListener::onStopPlaying);
    }

    @Override
    public void onViewHistory(Account account) {
        lblAccount.setText(account.getName());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @Override
    public void onStartNewRound(int bet) {}

    @Override
    public void onMoveToBettingTable() {}

    @Override
    public void onMoveToBettingTable(Account account) {}

    @Override
    public void onStopPlaying() {}
}
