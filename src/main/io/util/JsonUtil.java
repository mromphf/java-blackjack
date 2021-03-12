package main.io.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.Config;
import main.domain.Account;
import main.domain.Card;

import java.util.Stack;

public class JsonUtil {

    public static Account accountFromJson(String jsonDocument) {
        return new Gson().fromJson(jsonDocument, Account.class);
    }

    public static Config configFromJson(String jsonDocument) {
        return new Gson().fromJson(jsonDocument, Config.class);
    }

    public static Stack<Card> deckFromJson(String jsonDocument) {
        final Stack<Card> result = new Stack<>();
        final Gson gson = new Gson();
        final JsonObject json = gson.toJsonTree(jsonDocument).getAsJsonObject();
        return result;
    }
}
