package main.io.home;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.domain.Account;
import main.io.RootController;
import main.io.storage.AccountStorage;
import main.usecase.TransactionListener;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeController extends RootController implements Initializable, TransactionListener {

    @FXML
    private ListView<Account> lstAccounts;

    @FXML
    private Label lblBalance;

    @FXML
    private Button btnPlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lstAccounts.setItems(FXCollections.observableList(AccountStorage.getAll()));
    }

    @FXML
    public void onPlay() {
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
        navListeners.forEach(l -> l.onMoveToBettingTable(selectedAccount));
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    public void onClickList() {
        refreshBalanceLabel();
    }

    @Override
    public void onBalanceChanged(int balance) {
        lstAccounts.refresh();
        refreshBalanceLabel();
    }

    private void refreshBalanceLabel() {
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
        btnPlay.setDisable(selectedAccount == null);
        if (selectedAccount != null ) {
            lblBalance.setText(String.format("Balance: $%s", selectedAccount.getBalance()));
        }
    }
}
