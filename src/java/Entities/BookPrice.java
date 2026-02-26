package Entities;

public class BookPrice {
    private int BookID;
    private int PriceID;
    private String StartDate;
    private String EndDate;

    public BookPrice() {
    }

    public BookPrice(int BookID, int PriceID, String StartDate, String EndDate) {
        this.BookID = BookID;
        this.PriceID = PriceID;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
    }

    public int getBookID() {
        return BookID;
    }

    public void setBookID(int BookID) {
        this.BookID = BookID;
    }

    public int getPriceID() {
        return PriceID;
    }

    public void setPriceID(int PriceID) {
        this.PriceID = PriceID;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String EndDate) {
        this.EndDate = EndDate;
    }

    @Override
    public String toString() {
        return "BookPrice{" + "BookID=" + BookID + ", PriceID=" + PriceID + ", StartDate=" + StartDate + ", EndDate="
                + EndDate + '}';
    }
}
