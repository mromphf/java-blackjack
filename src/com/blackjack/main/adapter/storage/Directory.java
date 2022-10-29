package com.blackjack.main.adapter.storage;

public enum Directory {
    LOG("./log");

    private final String path;

    Directory(String path) {
        this.path = path;
    }

    public String path() {
        return this.path;
    }
}
