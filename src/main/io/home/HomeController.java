package main.io.home;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import main.domain.Account;
import main.io.RootController;
import main.usecase.TransactionListener;

import java.net.URL;
import java.util.*;

public class HomeController extends RootController implements Initializable, TransactionListener {

    @FXML
    private ListView<Account> lstAccounts;

    @FXML
    private Label lblBalance;

    @FXML
    private Button btnPlay;

    private final Map<UUID, Account> accountMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

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
    public void onBalanceChanged(Account account) {
        accountMap.put(account.getKey(), account);
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
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
