package main.adapter.ui.registration;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.domain.model.Account;
import main.usecase.Layout;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.domain.model.Transaction.signingBonus;
import static main.usecase.Layout.HOME;
import static main.usecase.Layout.REGISTRATION;

public class RegistrationController extends EventConnection implements Initializable, LayoutListener {

    @FXML
    public TextField txtName;

    @FXML
    private Button btnOk;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onCancel() {
        eventNetwork.onLayoutEvent(HOME);
    }

    @FXML
    public void onKeyTyped() {
        btnOk.setDisable(txtName.getText().length() < 3);
    }

    @FXML
    public void onCreate() {
        final LocalDateTime now = now();
        final Account account = new Account(randomUUID(), txtName.getText(), 0, now);

        txtName.setText("");
        eventNetwork.onAccountCreated(account.apply(signingBonus(account)));
        eventNetwork.onLayoutEvent(HOME);
    }

    @Override
    public void onLayoutEvent(Layout event) {
        if (event == REGISTRATION) {
            btnOk.setDisable(true);
        }
    }
}
