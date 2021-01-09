package main.io.util;

import com.oracle.javafx.jmx.json.JSONDocument;
import main.Config;
import main.domain.Account;

import java.time.LocalDateTime;
import java.util.UUID;

public class JsonUtil {

    public static String toJson(Account account) {
        final JSONDocument document = JSONDocument.createObject();

        document.setString("key", account.getKey().toString());
        document.setString("name", account.getName());
        document.setString("created", account.getCreated().toString());

        return document.toString();
    }

    public static Account accountFromJson(JSONDocument jsonDocument) {
        return new Account(
                UUID.fromString(jsonDocument.getString("key")),
                jsonDocument.getString("name"),
                0,
                LocalDateTime.parse(jsonDocument.getString("created"))
        );
    }

    public static Config configFromJSON(JSONDocument jsonDocument) {
        return new Config(
                jsonDocument.getNumber("decks").intValue()
        );
    }
}
