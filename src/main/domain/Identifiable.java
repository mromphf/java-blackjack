package main.domain;

import java.util.UUID;

import static java.util.UUID.*;

public interface Identifiable {
    UUID getKey();
}
