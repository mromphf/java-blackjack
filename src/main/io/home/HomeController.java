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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class HomeController extends RootController implements Initializable, TransactionListener {

    @FXML
    private ListView<Account> lstAccounts;

    @FXML
    private Label lblBalance;

    @FXML
    private Button btnPlay;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Account> list = new ArrayList<Account>() {{
            add(new Account(UUID.randomUUID(), "StickyJibs", 200));
            add(new Account(UUID.randomUUID(), "Fast Browns", 100));
            add(new Account(UUID.randomUUID(), "Sum Uncles", 450));
        }};
        lstAccounts.setItems(FXCollections.observableList(list));
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
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
        btnPlay.setDisable(selectedAccount == null);
        if (selectedAccount != null ) {
            lblBalance.setText(String.format("Balance: $%s", selectedAccount.getBalance()));
        }
    }

    @Override
    public void onBalanceChanged(int balance) {
        lstAccounts.refresh();
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
        btnPlay.setDisable(selectedAccount == null);
        if (selectedAccount != null ) {
            lblBalance.setText(String.format("Balance: $%s", selectedAccount.getBalance()));
        }
    }
}
