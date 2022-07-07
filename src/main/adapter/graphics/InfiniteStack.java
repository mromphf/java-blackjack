package main.adapter.graphics;

import java.util.Collection;
import java.util.LinkedList;

public class InfiniteStack<T> extends LinkedList<T> {

    public InfiniteStack(Collection<T> data) {
        addAll(data);
    }


    @Override
    public T pop() {
        T first = getFirst();

        addLast(removeFirst());

        return first;
    }
}
