package Entities;

public class WaitlistItem {
    private int bookId;
    private String bookName;
    private int quantity;
    private double unitPrice;

    public WaitlistItem(int bookId, String bookName, int quantity, double unitPrice) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Các Getters / Setters như cũ
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getBookName() { return bookName; }
    public void setBookName(String bookName) { this.bookName = bookName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getTotalPrice() { return quantity * unitPrice; }
}