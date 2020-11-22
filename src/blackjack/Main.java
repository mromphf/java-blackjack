package blackjack;

import blackjack.io.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class Main extends Application {

    private final static String BLACKJACK_FXML = "io/blackjack.fxml";

    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(BLACKJACK_FXML));

        try {
            new Controller(stage, fxmlLoader);
        } catch (Exception exception) {
            System.out.print("I couldn't load FXML from specified location! Quitting...");
            System.exit(1);
        }

        stage.setTitle("Blackjack");
        stage.setFullScreen(true);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
