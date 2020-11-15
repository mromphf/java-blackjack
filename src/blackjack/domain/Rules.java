package blackjack.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Rules {
    public static boolean isBlackjack(Collection<Card> cards) {
        boolean oneAce = cards.stream().filter(Card::isAce).count() == 1;
        boolean tenOrHigher = cards.stream().filter(c -> c.getValue() > 9).count() == 1;
        boolean twoCards = cards.size() == 2;
        return twoCards && oneAce && tenOrHigher;
    }

    public static Set<Option> options(Collection<Card> cards) {
        int benchmark = cards.iterator().next().getValue();
        if (cards.stream().allMatch(c -> c.getValue() == benchmark)) {
            return new HashSet<Option>() {{
                add(Option.DOUBLE);
                add(Option.HIT);
                add(Option.SPLIT);
                add(Option.STAND);
            }};
        } else {
            return new HashSet<Option>() {{
                add(Option.DOUBLE);
                add(Option.HIT);
                add(Option.STAND);
            }};
        }
    }
}
