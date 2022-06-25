package main.domain.util;

import java.util.function.Predicate;

public class LessCode {
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return predicate.negate();
    }
}
