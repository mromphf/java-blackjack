package main.adapter.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.domain.Card;
import main.domain.Deck;
import main.domain.Suit;

import java.util.Stack;

public class JsonUtil {

    public static Deck deckFromJson(String jsonString) {
        final Deck result = new Deck();
        final JsonObject[] jsonObjects = new Gson().fromJson(jsonString, JsonObject[].class);

        for (JsonObject obj : jsonObjects) {
            final int val = obj.get("value").getAsInt();
            final Suit suit = Suit.valueOf(obj.get("suit").getAsString().toUpperCase());

            result.add(new Card(val, suit));
        }

        return result;
    }
}
