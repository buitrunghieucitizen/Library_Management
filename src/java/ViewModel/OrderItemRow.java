package ViewModel;

public class OrderItemRow {
    private final int bookID;
    private final String bookName;
    private final int quantity;
    private final double unitPrice;

    public OrderItemRow(int bookID, String bookName, int quantity, double unitPrice) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getBookID() {
        return bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
