package Controller.borrow;

import Entities.Borrow;
import Model.DAOBorrow;
import Model.DAOFine;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates all business rules before a student can borrow books.
 *
 * Rules: 1. No overdue borrows allowed 2. Max 3 active borrows (Borrowing +
 * Pending combined) 3. No unpaid fines
 */
public class BorrowValidator {

    public static final int MAX_ACTIVE_BORROWS = 3;
    public static final int MAX_CART_SIZE = 3;

    private final DAOBorrow daoBorrow;
    private final DAOFine daoFine;

    public BorrowValidator(DAOBorrow daoBorrow, DAOFine daoFine) {
        this.daoBorrow = daoBorrow;
        this.daoFine = daoFine;
    }

    /**
     * Full validation before submitting a borrow request.
     *
     * @param studentId the student
     * @param cartSize number of books in current cart request
     * @return list of error messages (empty = all OK)
     */
    public List<String> validateBorrowEligibility(int studentId, int cartSize) throws SQLException {
        List<String> errors = new ArrayList<>();

        // Rule 1: Check overdue
        List<Borrow> activeBorrows = daoBorrow.getActiveByStudentId(studentId);
        boolean hasOverdue = false;
        for (Borrow b : activeBorrows) {
            if ("Overdue".equalsIgnoreCase(b.getStatus())) {
                hasOverdue = true;
                break;
            }
        }
        if (hasOverdue) {
            errors.add("Ban dang co sach muon qua han. Vui long tra sach truoc khi muon them.");
        }

        // Rule 2: Check active count (Borrowing + Overdue + Pending)
        int borrowingCount = activeBorrows.size(); // Borrowing + Overdue
        int pendingCount = daoBorrow.countByStudentAndStatus(studentId, "Pending");
        int totalActive = borrowingCount + pendingCount;

        if (totalActive >= MAX_ACTIVE_BORROWS) {
            errors.add("Ban dang co " + totalActive + "/" + MAX_ACTIVE_BORROWS
                    + " phieu muon (dang muon + cho duyet). Khong the muon them.");
        } else if (totalActive + cartSize > MAX_ACTIVE_BORROWS) {
            int canBorrow = MAX_ACTIVE_BORROWS - totalActive;
            errors.add("Ban chi co the muon them " + canBorrow
                    + " quyen nua (hien co " + totalActive + "/" + MAX_ACTIVE_BORROWS + " phieu).");
        }

        // Rule 3: Check unpaid fines
        if (daoFine.hasUnpaidFine(studentId)) {
            double totalFine = daoFine.getTotalUnpaid(studentId);
            errors.add("Ban dang no " + String.format("%,.0f", totalFine)
                    + " VND tien phat. Vui long thanh toan truoc khi muon.");
        }

        return errors;
    }

    /**
     * Quick check: can this student borrow at all?
     */
    public boolean canBorrow(int studentId) throws SQLException {
        return validateBorrowEligibility(studentId, 0).isEmpty();
    }

    /**
     * Validate adding to cart.
     *
     * @return error message or null if OK
     */
    public String validateCartAdd(int currentCartSize) {
        if (currentCartSize >= MAX_CART_SIZE) {
            return "Gio muon toi da " + MAX_CART_SIZE + " quyen. Vui long gui yeu cau hoac xoa bot.";
        }
        return null;
    }

    /**
     * Get eligibility summary for UI display.
     */
    public BorrowEligibility getEligibility(int studentId) throws SQLException {
        List<Borrow> activeBorrows = daoBorrow.getActiveByStudentId(studentId);
        int pendingCount = daoBorrow.countByStudentAndStatus(studentId, "Pending");
        boolean hasOverdue = false;
        for (Borrow b : activeBorrows) {
            if ("Overdue".equalsIgnoreCase(b.getStatus())) {
                hasOverdue = true;
                break;
            }
        }
        boolean hasUnpaidFine = daoFine.hasUnpaidFine(studentId);
        double unpaidAmount = hasUnpaidFine ? daoFine.getTotalUnpaid(studentId) : 0;
        int totalActive = activeBorrows.size() + pendingCount;
        int remaining = Math.max(0, MAX_ACTIVE_BORROWS - totalActive);
        boolean eligible = !hasOverdue && !hasUnpaidFine && remaining > 0;

        return new BorrowEligibility(eligible, totalActive, remaining,
                hasOverdue, hasUnpaidFine, unpaidAmount);
    }

    /**
     * Value object for UI display.
     */
    public static class BorrowEligibility {

        private final boolean eligible;
        private final int activeBorrows;
        private final int remainingSlots;
        private final boolean hasOverdue;
        private final boolean hasUnpaidFine;
        private final double unpaidFineAmount;

        public BorrowEligibility(boolean eligible, int activeBorrows, int remainingSlots,
                boolean hasOverdue, boolean hasUnpaidFine, double unpaidFineAmount) {
            this.eligible = eligible;
            this.activeBorrows = activeBorrows;
            this.remainingSlots = remainingSlots;
            this.hasOverdue = hasOverdue;
            this.hasUnpaidFine = hasUnpaidFine;
            this.unpaidFineAmount = unpaidFineAmount;
        }

        public boolean isEligible() {
            return eligible;
        }

        public int getActiveBorrows() {
            return activeBorrows;
        }

        public int getRemainingSlots() {
            return remainingSlots;
        }

        public boolean isHasOverdue() {
            return hasOverdue;
        }

        public boolean isHasUnpaidFine() {
            return hasUnpaidFine;
        }

        public double getUnpaidFineAmount() {
            return unpaidFineAmount;
        }
    }
}
