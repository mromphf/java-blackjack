package com.blackjack.main.util;

import com.blackjack.main.domain.model.Action;
import com.blackjack.main.domain.model.Card;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.blackjack.main.domain.function.CardFunctions.score;

public class StringUtil {
    public static String playerString(Collection<Card> hand) {
        return String.format("{\n\t\tScore: %s,\n\t\tCards: [\n\t\t\t%s\t]}",
                score(hand), concat(hand.stream()
                        .map(Card::toString)
                        .collect(Collectors.toList()), "\n\t\t\t"));
    }

    public static String actionString(Map<LocalDateTime, Action> timestampedActions) {
        final StringJoiner joiner = new StringJoiner("");
        timestampedActions.forEach((key, value) -> joiner.add(
                String.format("\n\t\t%s   %s", key.toString(), value.toString())
        ));
        return joiner.toString();
    }

    public static String concat(Collection<?> c, String delimiter) {
        return chomp(c.stream()
                .map(a -> String.format("%s%s", a, delimiter))
                .collect(Collectors.joining()), 2);
    }

    public static String chomp(String s, int n) {
        if (s.length() > 0) {
            return s.substring(0, s.length() - n);
        } else {
            return "";
        }
    }
}
