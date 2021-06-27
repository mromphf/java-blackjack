package main.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.domain.Card;
import main.domain.Suit;

import java.util.Stack;

public class JsonUtil {

    public static Stack<Card> deckFromJson(String jsonString) {
        final Stack<Card> result = new Stack<>();
        final JsonObject[] jsonObjects = new Gson().fromJson(jsonString, JsonObject[].class);

        for (JsonObject obj : jsonObjects) {
            final int val = obj.get("value").getAsInt();
            final Suit suit = Suit.valueOf(obj.get("suit").getAsString().toUpperCase());

            result.add(new Card(val, suit));
        }

        return result;
    }
}
