package Entities;

public class BorrowItem {
    private int BorrowID;
    private int BookID;
    private int Quantity;

    public BorrowItem() {
    }

    public BorrowItem(int BorrowID, int BookID, int Quantity) {
        this.BorrowID = BorrowID;
        this.BookID = BookID;
        this.Quantity = Quantity;
    }

    public int getBorrowID() {
        return BorrowID;
    }

    public void setBorrowID(int BorrowID) {
        this.BorrowID = BorrowID;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    @Override
    public String toString() {
        return "BorrowItem{" + "BorrowID=" + BorrowID + ", BookID=" + BookID + ", Quantity=" + Quantity + '}';
    }
}
