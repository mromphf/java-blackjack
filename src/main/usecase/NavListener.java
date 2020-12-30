package main.usecase;

import main.Layout;
import main.domain.Account;

public interface NavListener {
    void onChangeLayout(Layout layout);
    void onChangeLayout(Layout layout, Account account);
}
