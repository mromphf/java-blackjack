package main.adapter.graphics;

import java.util.LinkedList;

import static java.util.Arrays.asList;

public class InfiniteStack<T> extends LinkedList<T> {


    public InfiniteStack(T... t) {
        addAll(asList(t));
    }

    @Override
    public T pop() {
        T first = getFirst();

        addLast(removeFirst());

        return first;
    }
}
