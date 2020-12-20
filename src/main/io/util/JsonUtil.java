package main.io.util;

import com.oracle.javafx.jmx.json.JSONDocument;
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
        final UUID accountKey = UUID.fromString(jsonDocument.getString("key"));

        return new Account(
                accountKey,
                jsonDocument.getString("name"),
                0,
                LocalDateTime.parse(jsonDocument.getString("created"))
        );
    }
}
