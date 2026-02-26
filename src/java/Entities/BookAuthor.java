package Entities;

public class BookAuthor {
    private int BookID;
    private int AuthorID;

    public BookAuthor() {
    }

    public BookAuthor(int BookID, int AuthorID) {
        this.BookID = BookID;
        this.AuthorID = AuthorID;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public int getAuthorID() {
        return AuthorID;
    }

    public void setAuthorID(int AuthorID) {
        this.AuthorID = AuthorID;
    }

    @Override
    public String toString() {
        return "BookAuthor{" + "BookID=" + BookID + ", AuthorID=" + AuthorID + '}';
    }
}
