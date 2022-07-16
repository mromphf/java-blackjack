package main.adapter.graphics;

import com.google.common.primitives.Ints;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DelayedElement<T> implements Delayed {

    private final T data;
    private final long startTime;

    public DelayedElement(T data, long delayInMilliseconds) {
        this.data = data;
        this.startTime = currentTimeMillis() + delayInMilliseconds;
    }

    public T data() {
        return data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - currentTimeMillis();
        return unit.convert(diff, MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Ints.saturatedCast(
                this.startTime - (((DelayedElement<T>) o).startTime));
    }
}
