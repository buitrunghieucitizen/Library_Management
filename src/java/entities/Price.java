package entities;

public class Price {
    private int PriceID;
    private double Amount;
    private String Currency;
    private String Note;

    public Price() {
    }

    public Price(int PriceID, double Amount, String Currency, String Note) {
        this.PriceID = PriceID;
        this.Amount = Amount;
        this.Currency = Currency;
        this.Note = Note;
    }

    public Price(double Amount, String Currency, String Note) {
        this.Amount = Amount;
        this.Currency = Currency;
        this.Note = Note;
    }

    public int getPriceID() {
        return PriceID;
    }

    public void setPriceID(int PriceID) {
        this.PriceID = PriceID;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double Amount) {
        this.Amount = Amount;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String Currency) {
        this.Currency = Currency;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String Note) {
        this.Note = Note;
    }

    @Override
    public String toString() {
        return "Price{" + "PriceID=" + PriceID + ", Amount=" + Amount + ", Currency=" + Currency + ", Note=" + Note
                + '}';
    }
}
