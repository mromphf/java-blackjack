package com.blackjack.main.domain.model;

import java.util.HashMap;
import java.util.Set;

public class Bets extends HashMap<Account, Integer> {

    public Set<Account> accounts() {
        return keySet();
    }

    public static Bets bet(Account account, int amount) {
        final Bets bets = new Bets();
        bets.put(account, amount);
        return bets;
    }
}
