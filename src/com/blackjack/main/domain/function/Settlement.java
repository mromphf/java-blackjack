package com.blackjack.main.domain.function;

import com.blackjack.main.domain.model.TableView;

import static com.blackjack.main.domain.predicate.LowOrderPredicate.insurancePaysOut;
import static com.blackjack.main.domain.predicate.LowOrderPredicate.playerDoubledDown;

public class Settlement {
    public static int settleBet(TableView tableView) {
        final int bet = tableView.bet();
        final int insurancePayout = insurancePaysOut.test(tableView) ? (bet * 2) : 0;
        final int ddMultiplier = playerDoubledDown.test(tableView) ? 2 : 1;

        switch (tableView.outcome()) {
            case BLACKJACK:
                return (int) (((bet * 1.5f) * 2) * ddMultiplier) + insurancePayout;
            case WIN:
                return ((bet * 2) * ddMultiplier) + insurancePayout;
            case PUSH:
                return (bet * ddMultiplier) + insurancePayout;
            default:
                return 0;
        }
    }
}
