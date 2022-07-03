package main.adapter.ui;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountRegistrar;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.adapter.ui.Screen.BACK;
import static main.adapter.ui.Screen.HOME;
import static main.domain.model.Transaction.signingBonus;

public class RegistrationController implements Initializable, ScreenObserver {

    @FXML
    public TextField txtName;

    @FXML
    private Button btnOk;

    final private ScreenManagement screen;
    final private Collection<AccountRegistrar> accountRegistrars;

    @Inject
    public RegistrationController(
            final Collection<AccountRegistrar> accountRegistrars,
            final ScreenManagement screen) {
        this.screen = screen;
        this.accountRegistrars = accountRegistrars;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onCancel() {
        txtName.clear();
        screen.switchTo(BACK);
    }

    @FXML
    public void onKeyTyped() {
        btnOk.setDisable(txtName.getText().length() < 3);
    }

    @FXML
    public void onCreate() {
        final LocalDateTime now = now();
        final Account account = new Account(randomUUID(), txtName.getText(), 0, now);
        final Transaction signingBonus = signingBonus(account);

        for (AccountRegistrar registrar : accountRegistrars) {
            registrar.createNew(account.apply(signingBonus));
        }

        txtName.clear();
        screen.switchTo(HOME);
    }

    @Override
    public void onScreenChanged() {
        btnOk.setDisable(true);
    }
}
