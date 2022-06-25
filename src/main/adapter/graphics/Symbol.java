package main.adapter.graphics;

public enum Symbol {
    BLUE_CARD("card_blue"),
    CARD("card"),
    RED_CARD("card_red"),
    SYMBOL_CLUBS("sym_clubs"),
    SYMBOL_DIAMONDS("sym_diamonds"),
    SYMBOL_HEARTS("sym_hearts"),
    SYMBOL_SPADES("sym_spades"),
    ;

    public final String VALUE;

    Symbol(String value) {
        this.VALUE = value;
    }
}
