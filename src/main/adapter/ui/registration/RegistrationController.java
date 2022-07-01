package main.adapter.ui.registration;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.domain.model.Transaction.signingBonus;
import static main.usecase.Layout.BACK;
import static main.usecase.Layout.HOME;

public class RegistrationController implements Initializable, LayoutListener {

    @FXML
    public TextField txtName;

    @FXML
    private Button btnOk;

    final private LayoutManager layoutManager;
    final private Collection<AccountRegistrar> accountRegistrars;

    @Inject
    public RegistrationController(
            final Collection<AccountRegistrar> accountRegistrars,
            final LayoutManager layoutManager) {
        this.layoutManager  = layoutManager;
        this.accountRegistrars = accountRegistrars;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onCancel() {
        layoutManager.onLayoutEvent(BACK);
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

        layoutManager.onLayoutEvent(HOME);
        txtName.setText("");
    }

    @Override
    public void onLayoutEvent(Layout event) {
        btnOk.setDisable(true);
    }
}
