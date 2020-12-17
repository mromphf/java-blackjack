package main.usecase;

public interface TransactionListener {
    void onBalanceChanged(int balance);
}
