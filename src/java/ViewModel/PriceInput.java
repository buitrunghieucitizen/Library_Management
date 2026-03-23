package ViewModel;

import Entities.Price;

public class PriceInput {
    private final double amount;
    private final String currency;
    private final String note;

    public PriceInput(double amount, String currency, String note) {
        this.amount = amount;
        this.currency = currency;
        this.note = note;
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

    public Price toEntity() {
        return new Price(amount, currency, note);
    }
}
