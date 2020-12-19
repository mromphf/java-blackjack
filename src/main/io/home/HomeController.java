package main.io.home;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.io.RootController;
import main.usecase.TransactionListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class HomeController extends RootController implements Initializable, TransactionListener {

    @FXML
    public GridPane listControls;

    @FXML
    public GridPane accountCreationControls;

    @FXML
    public TextField txtName;

    @FXML
    private ListView<Account> lstAccounts;

    @FXML
    private Button btnOk;

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
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
        btnPlay.setDisable(selectedAccount == null);
    }

    @FXML
    public void onKeyTyped() {
        btnOk.setDisable(txtName.getText().length() < 3);
    }

    @FXML
    public void onNew() {
        btnOk.setDisable(txtName.getText().length() < 3);
        accountCreationControls.setVisible(true);
        listControls.setVisible(false);
    }

    @FXML
    public void onCancel() {
        accountCreationControls.setVisible(false);
        listControls.setVisible(true);
        txtName.setText("");
    }

    @FXML
    public void onCreate() {
        final UUID uuid = UUID.randomUUID();
        final String name = txtName.getText();
        final int balance = 200;
        final LocalDateTime now = LocalDateTime.now();

        accountMap.put(uuid, new Account(uuid, name, balance, now));

        txtName.setText("");
        accountCreationControls.setVisible(false);
        listControls.setVisible(true);
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
    }

    @Override
    public void onBalanceChanged(Account account) {
        accountMap.put(account.getKey(), account);
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
    }
}
