package main.adapter.storage;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import main.domain.model.Card;
import main.domain.model.Deck;
import main.domain.model.Rank;
import main.domain.model.Suit;

import static main.domain.model.Card.card;

public class JsonUtil {

    public static Deck deckFromJson(String jsonString) {
        final Deck result = new Deck();
        final JsonObject[] jsonObjects = new Gson().fromJson(jsonString, JsonObject[].class);

        for (JsonObject obj : jsonObjects) {
            final Rank rank = Rank.of(obj.get("value").getAsInt());
            final Suit suit = Suit.valueOf(obj.get("suit").getAsString().toUpperCase());

            result.add(card(rank, suit));
        }

        return result;
    }
}
