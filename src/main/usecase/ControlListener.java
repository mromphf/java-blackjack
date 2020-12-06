package main.usecase;

public interface ControlListener {
    void onHit();
    void onDouble();
    void onStand();
    void onSettleHand();
    void onSplit();
    void onPurchaseInsurance();
}
