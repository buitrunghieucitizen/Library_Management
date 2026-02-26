package Entities;

public class BookCode {
    private int BookCodeID;
    private int BookID;
    private String BookCode;

    public BookCode() {
    }

    public BookCode(int BookCodeID, int BookID, String BookCode) {
        this.BookCodeID = BookCodeID;
        this.BookID = BookID;
        this.BookCode = BookCode;
    }

    public BookCode(int BookID, String BookCode) {
        this.BookID = BookID;
        this.BookCode = BookCode;
    }

    public int getBookCodeID() {
        return BookCodeID;
    }

    public void setBookCodeID(int BookCodeID) {
        this.BookCodeID = BookCodeID;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public String getBookCode() {
        return BookCode;
    }

    public void setBookCode(String BookCode) {
        this.BookCode = BookCode;
    }

    @Override
    public String toString() {
        return "BookCode{" + "BookCodeID=" + BookCodeID + ", BookID=" + BookID + ", BookCode=" + BookCode + '}';
    }
}
