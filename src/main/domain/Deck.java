package main.domain;

import java.util.Stack;

public class Deck extends Stack<Card> {

    public Card drawCard() {
        return pop();
    }
}
