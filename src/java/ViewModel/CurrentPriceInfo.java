package ViewModel;

import java.time.LocalDate;

public class CurrentPriceInfo {
    private final int priceID;
    private final LocalDate startDate;
    private final double amount;
    private final String currency;
    private final String note;

    public CurrentPriceInfo(int priceID, LocalDate startDate, double amount, String currency, String note) {
        this.priceID = priceID;
        this.startDate = startDate;
        this.amount = amount;
        this.currency = currency;
        this.note = note;
    }

    public int getPriceID() {
        return priceID;
    }

    public LocalDate getStartDate() {
        return startDate;
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
