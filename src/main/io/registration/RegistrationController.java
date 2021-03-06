package main.io.registration;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.domain.Account;
import main.usecase.Layout;
import main.usecase.eventing.Event;
import main.usecase.eventing.EventConnection;
import main.usecase.eventing.LayoutListener;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.UUID;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static main.usecase.Layout.HOME;
import static main.usecase.Layout.REGISTRATION;
import static main.usecase.eventing.Predicate.ACCOUNT_CREATED;
import static main.usecase.eventing.Predicate.LAYOUT_CHANGED;

public class RegistrationController extends EventConnection implements Initializable, LayoutListener {

    @FXML
    public TextField txtName;

    @FXML
    private Button btnOk;

    private final UUID key = randomUUID();

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    @FXML
    public void onCancel() {
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, HOME));
    }

    @FXML
    public void onKeyTyped() {
        btnOk.setDisable(txtName.getText().length() < 3);
    }

    @FXML
    public void onCreate() {
        final UUID uuid = randomUUID();
        final String name = txtName.getText();
        final LocalDateTime now = now();
        final Account account = new Account(uuid, name, now);

        txtName.setText("");

        eventNetwork.onAccountEvent(new Event<>(key, now(), ACCOUNT_CREATED, account));
        eventNetwork.onLayoutEvent(new Event<>(key, now(), LAYOUT_CHANGED, HOME));
    }

    @Override
    public UUID getKey() {
        return key;
    }

    @Override
    public void onLayoutEvent(Event<Layout> event) {
        if (event.is(LAYOUT_CHANGED) && event.getData() == REGISTRATION) {
            btnOk.setDisable(true);
        }
    }
}
