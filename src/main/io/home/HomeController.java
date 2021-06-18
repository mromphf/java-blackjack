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
import main.usecase.eventing.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

import static main.usecase.Layout.BET;
import static main.usecase.Layout.HISTORY;
import static main.usecase.eventing.Predicate.*;

public class HomeController extends EventConnection implements Initializable, AccountListener {

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
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();

        eventNetwork.onAccountEvent(new Event<>(LocalDateTime.now(), ACCOUNT_SELECTED, selectedAccount));
        eventNetwork.onLayoutEvent(new Event<>(LocalDateTime.now(), LAYOUT_CHANGED, BET));
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
            final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();
            btnPlay.setDisable(selectedAccount == null);
            btnDelete.setDisable(selectedAccount == null);
            btnHistory.setDisable(selectedAccount == null);
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
        final LocalDateTime now = LocalDateTime.now();
        final Account account = new Account(uuid, name, now);

        txtName.setText("");
        accountCreationControls.setVisible(false);
        listControls.setVisible(true);

        eventNetwork.onAccountEvent(new Event<>(LocalDateTime.now(), ACCOUNT_CREATED, account));
    }

    @FXML
    public void onDelete() {
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();

        accountMap.remove(selectedAccount.getKey());
        lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));

        eventNetwork.onAccountEvent(new Event<>(LocalDateTime.now(), ACCOUNT_DELETED, selectedAccount));
    }

    @FXML
    public void onRequestHistory() {
        final Account selectedAccount = lstAccounts.getSelectionModel().getSelectedItem();

        eventNetwork.onAccountEvent(new Event<>(LocalDateTime.now(), ACCOUNT_SELECTED, selectedAccount));
        eventNetwork.onLayoutEvent(new Event<>(LocalDateTime.now(), LAYOUT_CHANGED, HISTORY));
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(CURRENT_BALANCE)) {
            final Account account = event.getData();
            accountMap.put(account.getKey(), account);
            lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        if (event.is(ACCOUNTS_LOADED)) {
            for (Account account : event.getData()) {
                accountMap.put(account.getKey(), account);
            }
            lstAccounts.setItems(FXCollections.observableList(new ArrayList<>(accountMap.values())));
        }
    }
}
