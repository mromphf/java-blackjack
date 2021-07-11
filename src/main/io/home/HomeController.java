package main.io.home;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import main.domain.Account;
import main.usecase.Layout;
import main.usecase.eventing.AccountListener;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.net.URL;
import java.util.*;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static javafx.collections.FXCollections.observableList;
import static javafx.scene.control.ButtonType.OK;
import static main.io.blackjack.ImageMap.*;
import static main.usecase.Layout.*;
import static main.usecase.eventing.Predicate.*;

public class HomeController extends EventConnection implements Initializable, AccountListener, LayoutListener {

    @FXML
    public ImageView img1;

    @FXML
    public ImageView img2;

    @FXML
    public ImageView img3;

    @FXML
    public ImageView img4;

    @FXML
    private TableView<Account> tblAccounts;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnHistory;

    private final UUID key = randomUUID();
    private final Map<UUID, Account> accountMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final EventHandler<ActionEvent> handler = onDeleteEvent();

        btnDelete.setOnAction(handler);
        img1.imageProperty().setValue(symSpades());
        img2.imageProperty().setValue(symDiamonds());
        img3.imageProperty().setValue(symHearts());
        img4.imageProperty().setValue(symClubs());

    }

    @FXML
    public void onPlay() {
        final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();

        eventNetwork.onAccountEvent(new Event<>(key, now(), ACCOUNT_SELECTED, selectedAccount));
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, BET));
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
            final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
            btnPlay.setDisable(selectedAccount == null);
            btnDelete.setDisable(selectedAccount == null);
            btnHistory.setDisable(selectedAccount == null);
        }
    }

    @FXML
    public void onNew() {
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, REGISTRATION));
    }

    @FXML
    public void onRequestHistory() {
        final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();

        eventNetwork.onAccountEvent(new Event<>(key, now(), ACCOUNT_SELECTED, selectedAccount));
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, HISTORY));
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onAccountEvent(Event<Account> event) {
        if (event.is(CURRENT_BALANCE)) {
            final Account account = event.getData();
            accountMap.put(account.getKey(), account);
            tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values())));
        }
    }

    @Override
    public void onAccountsEvent(Event<Collection<Account>> event) {
        if (event.is(ACCOUNTS_LOADED)) {

            for (Account account : event.getData()) {
                accountMap.put(account.getKey(), account);
            }

            tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values())));
        }
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == HOME) {
            btnPlay.setDisable(true);
            btnDelete.setDisable(true);
            btnHistory.setDisable(true);
        }
    }

    private EventHandler<ActionEvent> onDeleteEvent() {
        return actionEvent -> {
            final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
            final Alert alert = initializeConfirmationAlert(selectedAccount);
            final Optional<ButtonType> buttonType = alert.showAndWait();

            if ((buttonType.isPresent() && buttonType.get() == OK)) {
                accountMap.remove(selectedAccount.getKey());
                tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values())));
                eventNetwork.onAccountEvent(new Event<>(key, now(), ACCOUNT_DELETED, selectedAccount));
            }
        };
    }

    private Alert initializeConfirmationAlert(Account account) {
        final Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        eventNetwork.onAlertEvent(new Event<>(key, now(), LAYOUT_ALERT, alert));

        alert.setTitle("Confirm Account Closure");
        alert.setHeaderText(account.getName());
        alert.setContentText("Are you sure you want to close this account?\n" +
                "This action is permanent and cannot be undone.");

        return alert;
    }
}
