package main.io.home;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import main.domain.Account;
import main.io.EventConnection;
import main.usecase.*;
import main.usecase.EventListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static main.usecase.Layout.BET;
import static main.usecase.Layout.HISTORY;
import static main.usecase.Predicate.*;

public class HomeController extends EventConnection implements EventListener, Initializable {

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

    @FXML
    private Button btnHistory;

    private final Map<UUID, Account> accountMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onPlay() {
        eventNetwork.post(new Event(ACCOUNT_SELECTED, selectedAccount()));
        eventNetwork.post(new Event(LAYOUT_CHANGED, BET));
    }

    @FXML
    public void onRequestHistory() {
        eventNetwork.post(new Event(ACCOUNT_SELECTED, selectedAccount()));
        eventNetwork.post(new Event(LAYOUT_CHANGED, HISTORY));
    }

    @FXML
    public void onDelete() {
        accountMap.remove(selectedAccount().getKey());
        populateAccountList();
        eventNetwork.post(new Event(ACCOUNT_DELETED, selectedAccount()));
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    public void onClickList(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            onPlay();
        } else {
            btnPlay.setDisable(selectedAccount() == null);
            btnDelete.setDisable(selectedAccount() == null);
            btnHistory.setDisable(selectedAccount() == null);
        }
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
        populateAccountList();

        eventNetwork.post(new Event(ACCOUNT_OPENED, account));
    }

    @Override
    public void listen(Event e) {
        if (e.is(BALANCE_UPDATED)) {
            final Account account = e.getAccount();
            accountMap.put(account.getKey(), account);
            populateAccountList();
        } else if (e.is(ACCOUNTS_LOADED)) {
            e.getAccounts().forEach(a -> accountMap.put(a.getKey(), a));
            populateAccountList();
        }
    }

    public Account selectedAccount() {
        return lstAccounts.getSelectionModel().getSelectedItem();
    }

    public void populateAccountList() {
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
    }
}
