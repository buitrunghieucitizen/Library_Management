package ViewModel;

import java.time.LocalDate;

public class BorrowRenewalDecision {

    private final boolean eligible;
    private final String message;
    private final LocalDate nextDueDate;

    private BorrowRenewalDecision(boolean eligible, String message, LocalDate nextDueDate) {
        this.eligible = eligible;
        this.message = message;
        this.nextDueDate = nextDueDate;
    }

    public static BorrowRenewalDecision eligible(LocalDate nextDueDate, String message) {
        return new BorrowRenewalDecision(true, message, nextDueDate);
    }

    public static BorrowRenewalDecision ineligible(String message) {
        return new BorrowRenewalDecision(false, message, null);
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getMessage() {
        return message;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public String getNextDueDateLabel() {
        return nextDueDate == null ? "" : nextDueDate.toString();
    }
}
