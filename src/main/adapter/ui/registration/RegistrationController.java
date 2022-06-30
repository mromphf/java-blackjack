package main.adapter.ui.registration;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.domain.model.Account;
import main.domain.model.Transaction;
import main.usecase.AccountService;
import main.usecase.Layout;
import main.usecase.LayoutManager;
import main.usecase.TransactionService;
import main.usecase.LayoutListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.usecase.Layout.*;

public class RegistrationController implements Initializable, LayoutListener {

    @FXML
    public TextField txtName;

    @FXML
    private Button btnOk;

    final private LayoutManager layoutManager;
    final private AccountService accountService;
    final private TransactionService transactionService;

    @Inject
    public RegistrationController(
            final LayoutManager layoutManager,
            final AccountService accountService,
            final TransactionService transactionService) {
        this.layoutManager  = layoutManager;
        this.accountService = accountService;
        this.transactionService = transactionService;
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
        final Transaction signingBonus = Transaction.signingBonus(account);

        txtName.setText("");
        accountService.onAccountCreated(account.apply(signingBonus));
        transactionService.onTransactionIssued(signingBonus);
        layoutManager.onLayoutEvent(HOME);
    }

    @Override
    public void onLayoutEvent(Layout event) {
        if (event == REGISTRATION) {
            btnOk.setDisable(true);
        }
    }
}
