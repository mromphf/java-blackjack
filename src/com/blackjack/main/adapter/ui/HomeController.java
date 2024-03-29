package com.blackjack.main.adapter.ui;

import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import com.blackjack.main.adapter.graphics.animation.ImageReel;
import com.blackjack.main.domain.model.Account;
import com.blackjack.main.usecase.AccountStore;

import java.net.URL;
import java.util.*;

import static javafx.application.Platform.runLater;
import static javafx.collections.FXCollections.observableList;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.OK;
import static javafx.scene.input.MouseButton.SECONDARY;
import static com.blackjack.main.adapter.ui.Screen.*;

public class HomeController implements Initializable, ScreenObserver {

    @FXML
    public Canvas topScroller;

    @FXML
    public Canvas bottomScroller;

    @FXML
    private TableView<Account> tblAccounts;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnHistory;

    private static final String ANIMATION_THREAD_NAME = "Home Screen Animation Thread";

    private final AccountStore accountStore;
    private final Map<UUID, Account> accountMap = new HashMap<>();
    private final Map<Canvas, ImageReel> animations = new HashMap<>();

    private final ScreenManagement screen;
    private final AlertService alertService;
    private final ImageService imageStore;

    @Inject
    public HomeController(
            ImageService imageService,
            AlertService alertService,
            AccountStore accountStore,
            ScreenManagement screen) {
        this.alertService = alertService;
        this.accountStore = accountStore;
        this.screen = screen;
        this.imageStore = imageService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final EventHandler<ActionEvent> handler = onDeleteEvent();
        final GraphicsContext topScrollerGraphics = topScroller.getGraphicsContext2D();
        final GraphicsContext bottomScrollerGraphics = bottomScroller.getGraphicsContext2D();

        animations.put(topScroller, new ImageReel(imageStore.reelLeft(), topScrollerGraphics));
        animations.put(bottomScroller, new ImageReel(imageStore.reelRight(), bottomScrollerGraphics));

        tblAccounts.setPlaceholder(new Label("No account records."));

        btnDelete.setOnAction(handler);

        toggleAnimationsRunning(true);
    }

    @FXML
    public void onPlay() {
        accountStore.selectAccount(tblAccounts.getSelectionModel().getSelectedItem());
        screen.switchTo(BET);
    }

    @FXML
    public void onExit() {
        System.exit(0);
    }

    @FXML
    public void onClickList(MouseEvent mouseEvent) {
        final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
        btnPlay.setDisable(selectedAccount == null);
        btnDelete.setDisable(selectedAccount == null);
        btnHistory.setDisable(selectedAccount == null);

        if (mouseEvent.getClickCount() == 2) {
            onPlay();
        } else if (selectedAccount != null) {
            accountStore.selectAccount(selectedAccount);
        }
    }

    @FXML
    public void onNew() {
        screen.switchTo(REGISTRATION);
    }

    @FXML
    public void onRequestHistory() {
        accountStore.selectAccount(tblAccounts.getSelectionModel().getSelectedItem());
        screen.switchTo(HISTORY);
        toggleAnimationsRunning((false));
    }

    @FXML
    public void onClickTopScroller(MouseEvent event) {
        animations.get(topScroller).switchDirection();

        if (event.getButton() == SECONDARY) {
            animations.get(bottomScroller).switchDirection();
        }
    }

    @FXML
    public void onClickBottomScroller(MouseEvent event) {
        animations.get(bottomScroller).switchDirection();

        if (event.getButton() == SECONDARY) {
            animations.get(topScroller).switchDirection();
        }
    }

    public void onAccountsLoaded(Collection<Account> accounts) {
        for (Account account : accounts) {
            accountMap.put(account.key(), account);
        }

        tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values())));
    }

    @Override
    public void onScreenChanged() {
        final Optional<Account> selectedAccount = accountStore.selectedAccount();

        toggleAnimationsRunning((true));
        btnPlay.setDisable(true);
        btnDelete.setDisable(true);
        btnHistory.setDisable(true);

        if (selectedAccount.isPresent()) {
            final Account account = selectedAccount.get();

            accountMap.put(account.key(), account);
            runLater(() -> tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values()))));
        }
    }

    private void toggleAnimationsRunning(boolean isRunning) {
        if (isRunning) {
            animations.values()
                    .forEach(animation -> new Thread(animation::start, ANIMATION_THREAD_NAME).start());
        } else {
            animations.values().forEach(ImageReel::stop);
        }
    }

    private EventHandler<ActionEvent> onDeleteEvent() {
        return actionEvent -> {
            final Account selectedAccount = tblAccounts.getSelectionModel().getSelectedItem();
            final Alert alert = initializeConfirmationAlert(selectedAccount);
            final Optional<ButtonType> buttonType = alert.showAndWait();

            if ((buttonType.isPresent() && buttonType.get() == OK)) {
                accountMap.remove(selectedAccount.key());
                tblAccounts.setItems(observableList(new ArrayList<>(accountMap.values())));
                accountStore.onAccountDeleted(selectedAccount);
            }
        };
    }

    private Alert initializeConfirmationAlert(Account account) {
        final Alert alert = new Alert(CONFIRMATION);

        alertService.issueAlert(alert);

        alert.setTitle("Confirm Account Closure");
        alert.setHeaderText(account.getName());
        alert.setContentText("Are you sure you want to close this account?\n" +
                "This action is permanent and cannot be undone.");

        return alert;
    }
}
