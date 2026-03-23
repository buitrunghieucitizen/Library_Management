package ViewModel;

public class PurchaseRequestItem {
    private final int bookID;
    private final int quantity;
    private double unitPrice;

    public PurchaseRequestItem(int bookID, int quantity) {
        this.bookID = bookID;
        this.quantity = quantity;
    }

    public int getBookID() {
        return bookID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
