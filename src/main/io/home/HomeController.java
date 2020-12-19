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
import main.usecase.AccountListener;
import main.usecase.MemoryListener;
import main.usecase.TransactionListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

public class HomeController extends RootController implements Initializable, TransactionListener, MemoryListener {

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

    @FXML
    private Button btnDelete;

    private final Map<UUID, Account> accountMap = new HashMap<>();

    private final Queue<AccountListener> accountListeners = new ArrayDeque<>();

    public void registerAccountListener(AccountListener accountListener) {
        accountListeners.add(accountListener);
    }

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
        btnDelete.setDisable(selectedAccount == null);
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
        final Account account = new Account(uuid, name, balance, now);

        accountMap.put(uuid, account);

        txtName.setText("");
        accountCreationControls.setVisible(false);
        listControls.setVisible(true);
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));

        accountListeners.forEach(l -> l.onNewAccountOpened(account));
    }

    @FXML
    public void onDelete() {
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();

        accountMap.remove(selectedAccount.getKey());
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
        accountListeners.forEach(l -> l.onAccountDeleted(selectedAccount));
    }

    @Override
    public void onAccountUpdated(Account account) {
        accountMap.put(account.getKey(), account);
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
    }

    @Override
    public void onAccountsLoaded(Collection<Account> accounts) {
        for (Account account : accounts) {
            accountMap.put(account.getKey(), account);
        }
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
    }
}
