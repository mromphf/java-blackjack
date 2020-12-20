package main.io.util;

import java.util.Collection;
import java.util.stream.Collectors;

public class StringUtil {
    public static String concat(Collection<?> c, char delimiter) {
        return chomp(c.stream()
                .map(a -> String.format("%s%s ", a, delimiter))
                .collect(Collectors.joining()), 2);
    }

    public static String chomp(String s, int n) {
        if (s.length() > 0) {
            return s.substring(0, s.length() - n);
        } else {
            return "";
        }
    }
}
