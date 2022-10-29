package com.blackjack.main.util;

import com.blackjack.main.domain.model.Action;
import com.blackjack.main.domain.model.Card;
import com.blackjack.main.domain.model.TableView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static com.blackjack.main.domain.function.CardFunctions.score;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class StringUtil {
    public static String playerString(TableView tableView) {
        return format("{\n\t\tScore: %s," +
                        "\n\t\tAccount: %s" +
                        "\n\t\tBalance: $%s" +
                        "\n\t\tBet: $%s" +
                        "\n\t\tHands to Play: %s" +
                        "\n\t\tHands to Settle: %s" +
                        "\n\t\tActionsTaken: {%s\n\t\t}" +
                        "\n\t\tCards: [\n\t\t\t%s\t]}",
                score(tableView.playerHand()),
                tableView.playerAccountKey(),
                tableView.playerBalance(),
                tableView.bet(),
                tableView.handsToPlay().size(),
                tableView.handsToSettle().size(),
                actionString(tableView.actionsTimestamped()),
                concat(tableView.playerHand().stream()
                        .map(Card::toString)
                        .collect(toList()), "\n\t\t\t"));
    }

    public static String dealerString(Collection<Card> hand) {
            return String.format("{\n\t\tScore: %s,\n\t\tCards: [\n\t\t\t%s\t]}",
                    score(hand), concat(new ArrayList<>(hand), "\n\t\t\t"));
    }

    public static String actionString(Map<LocalDateTime, Action> timestampedActions) {
        final StringJoiner joiner = new StringJoiner("");
        timestampedActions.forEach((key, value) -> joiner.add(
                format("\n\t\t\t%s   %s", key.toString(), value.toString())
        ));
        return joiner.toString();
    }

    public static String concat(Collection<?> c, String delimiter) {
        return chomp(c.stream()
                .map(a -> format("%s%s", a, delimiter))
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
