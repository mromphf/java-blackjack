package com.blackjack.main.adapter.graphics;

public enum Symbol {
    BLUE_CARD("card_blue"),
    RED_CARD("card_red"),
    SYMBOL_CLUBS("sym_clubs_small"),
    SYMBOL_DIAMONDS("sym_diamonds_small"),
    SYMBOL_HEARTS("sym_hearts_small"),
    SYMBOL_SPADES("sym_spades_small"),
    ;

    public final String VALUE;

    Symbol(String value) {
        this.VALUE = value;
    }
}
