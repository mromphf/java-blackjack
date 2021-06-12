package main.usecase;

public class Response {
    private final int currentBalance;

    private Response(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    public static Response of(int currentBalance) {
        return new Response(currentBalance);
    }

    public int getCurrentBalance() {
        return currentBalance;
    }
}
