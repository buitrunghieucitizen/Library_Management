package Entities;

public class Author {
    private int AuthorID;
    private String AuthorName;

    public Author() {
    }

    public Author(int AuthorID, String AuthorName) {
        this.AuthorID = AuthorID;
        this.AuthorName = AuthorName;
    }

    public Author(String AuthorName) {
        this.AuthorName = AuthorName;
    }

    public int getAuthorID() {
        return AuthorID;
    }

    public void setAuthorID(int AuthorID) {
        this.AuthorID = AuthorID;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String AuthorName) {
        this.AuthorName = AuthorName;
    }

    @Override
    public String toString() {
        return "Author{" + "AuthorID=" + AuthorID + ", AuthorName=" + AuthorName + '}';
    }
}
