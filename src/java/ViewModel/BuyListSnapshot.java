package ViewModel;

import java.util.List;

public class BuyListSnapshot {
    private final List<StudentBuyListRow> items;
    private final double totalAmount;

    public BuyListSnapshot(List<StudentBuyListRow> items, double totalAmount) {
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public List<StudentBuyListRow> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
