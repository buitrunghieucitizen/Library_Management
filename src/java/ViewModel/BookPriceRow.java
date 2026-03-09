package ViewModel;

public class BookPriceRow {
    private final int bookID;
    private final String bookName;
    private final int available;
    private final double amount;
    private final String currency;
    private final String note;

    public BookPriceRow(int bookID, String bookName, int available, double amount, String currency, String note) {
        this.bookID = bookID;
        this.bookName = bookName;
        this.available = available;
        this.amount = amount;
        this.currency = currency;
        this.note = note;
    }

    public int getBookID() {
        return bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public int getAvailable() {
        return available;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getNote() {
        return note;
    }
}
