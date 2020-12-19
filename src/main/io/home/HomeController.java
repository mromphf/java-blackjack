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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class HomeController extends RootController implements Initializable, TransactionListener {

    @FXML
    private ListView<Account> lstAccounts;

    @FXML
    private Label lblBalance;

    @FXML
    private Button btnPlay;

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
        final List<Account> newItems = lstAccounts.getItems().stream()
                .filter(a -> !a.getKey().equals(account.getKey()))
                .collect(Collectors.toList());

        newItems.add(account);

        lstAccounts.setItems(FXCollections.observableList(newItems));
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
