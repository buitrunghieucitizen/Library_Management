package Controller.borrow;

import Entities.Borrow;
import Model.DAOBorrow;
import Model.DAOFine;
import Model.DBConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Validates all business rules before a student can borrow books.
 *
 * Rules: 1. No overdue borrows allowed 2. Max 3 active borrows (Borrowing +
 * Pending combined) 3. No unpaid fines
 */
public class BorrowValidator {

    public static final int MAX_ACTIVE_BORROWS = 3;
    public static final int MAX_CART_SIZE = 3;
    public static final int MAX_BORROWS_PER_WEEK = 3;

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

        // Rule 1: Check overdue (giữ nguyên)
        List<Borrow> activeBorrows = daoBorrow.getActiveByStudentId(studentId);
        boolean hasOverdue = false;
        for (Borrow b : activeBorrows) {
            if ("Overdue".equalsIgnoreCase(b.getStatus())) {
                hasOverdue = true;
                break;
            }
        }
        if (hasOverdue) {
            errors.add("Bạn đang có sách mượn quá hạn. Vui lòng trả sách trước khi mượn thêm.");
        }

        // Rule 2: Max active (giữ nguyên)
        int borrowingCount = activeBorrows.size();
        int pendingCount = daoBorrow.countByStudentAndStatus(studentId, "Pending");
        int totalActive = borrowingCount + pendingCount;
        if (totalActive >= MAX_ACTIVE_BORROWS) {
            errors.add("Bạn đang có " + totalActive + "/" + MAX_ACTIVE_BORROWS
                    + " phiếu mượn (đang mượn + chờ duyệt). Không thể mượn thêm.");
        } else if (totalActive + cartSize > MAX_ACTIVE_BORROWS) {
            int canBorrow = MAX_ACTIVE_BORROWS - totalActive;
            errors.add("Bạn chỉ có thể mượn thêm " + canBorrow + " quyển nữa.");
        }

        // Rule 3: Unpaid fines (giữ nguyên)
        if (daoFine.hasUnpaidFine(studentId)) {
            double totalFine = daoFine.getTotalUnpaid(studentId);
            errors.add("Bạn đang nợ " + String.format("%,.0f", totalFine)
                    + " VNĐ tiền phạt. Vui lòng thanh toán trước khi mượn.");
        }

        // Rule 4: MỚI — max 3 quyển/tuần
        int borrowedThisWeek = countBorrowsThisWeek(studentId);
        if (borrowedThisWeek >= MAX_BORROWS_PER_WEEK) {
            errors.add("Bạn đã mượn " + borrowedThisWeek + " quyển trong tuần này. "
                    + "Giới hạn " + MAX_BORROWS_PER_WEEK + " quyển/tuần.");
        } else if (borrowedThisWeek + cartSize > MAX_BORROWS_PER_WEEK) {
            int canBorrow = MAX_BORROWS_PER_WEEK - borrowedThisWeek;
            errors.add("Tuần này bạn chỉ có thể mượn thêm " + canBorrow + " quyển.");
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
            return "Giỏ mượn tối đa " + MAX_CART_SIZE + " quyển. Vui lòng gửi yêu cầu hoặc xóa bớt.";
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

        int borrowedThisWeek = countBorrowsThisWeek(studentId);
        int weekRemaining = Math.max(0, MAX_BORROWS_PER_WEEK - borrowedThisWeek);

        // Effective remaining = min of slot remaining and week remaining
        int effectiveRemaining = Math.min(remaining, weekRemaining);

        boolean eligible = !hasOverdue && !hasUnpaidFine && effectiveRemaining > 0;

        return new BorrowEligibility(eligible, totalActive, effectiveRemaining,
                hasOverdue, hasUnpaidFine, unpaidAmount, borrowedThisWeek, MAX_BORROWS_PER_WEEK);
    }

    // Thêm method mới:
    private int countBorrowsThisWeek(int studentId) throws SQLException {
        // Đếm tổng sách đã mượn trong tuần này (Monday → Sunday)
        String sql = "SELECT ISNULL(SUM(bi.Quantity), 0) "
                + "FROM Borrow b "
                + "JOIN BorrowItem bi ON bi.BorrowID = b.BorrowID "
                + "WHERE b.StudentID = ? "
                + "AND b.Status IN ('Borrowing', 'Returned', 'Pending', 'Overdue', 'ReturnRequested') "
                + "AND b.BorrowDate >= DATEADD(DAY, 1-DATEPART(WEEKDAY, GETDATE()), CAST(GETDATE() AS DATE))";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Không thể kết nối đến cơ sở dữ liệu.");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            con.close();
        }
        return 0;
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
        private final int borrowedThisWeek;
        private final int maxPerWeek;

        public BorrowEligibility(boolean eligible, int activeBorrows, int remainingSlots,
                boolean hasOverdue, boolean hasUnpaidFine, double unpaidFineAmount,
                int borrowedThisWeek, int maxPerWeek) {
            this.eligible = eligible;
            this.activeBorrows = activeBorrows;
            this.remainingSlots = remainingSlots;
            this.hasOverdue = hasOverdue;
            this.hasUnpaidFine = hasUnpaidFine;
            this.unpaidFineAmount = unpaidFineAmount;
            this.borrowedThisWeek = borrowedThisWeek;
            this.maxPerWeek = maxPerWeek;
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

        public int getBorrowedThisWeek() {
            return borrowedThisWeek;
        }

        public int getMaxPerWeek() {
            return maxPerWeek;
        }
    }
}
