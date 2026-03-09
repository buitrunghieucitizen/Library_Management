package ViewModel;

public class StudentBuyListRow {
    private final int bookID;
    private final String bookName;
    private final int quantity;
    private final int available;
    private final double unitPrice;
    private final String currency;
    private final double lineTotal;
    private final boolean canOrder;

    public StudentBuyListRow(int bookID, String bookName, int quantity, int available,
            double unitPrice, String currency, double lineTotal, boolean canOrder) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.quantity = quantity;
        this.available = available;
        this.unitPrice = unitPrice;
        this.currency = currency;
        this.lineTotal = lineTotal;
        this.canOrder = canOrder;
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

    public int getAvailable() {
        return available;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public boolean isCanOrder() {
        return canOrder;
    }
}
