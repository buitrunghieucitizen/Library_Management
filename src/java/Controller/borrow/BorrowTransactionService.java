package Controller.borrow;

import Entities.Borrow;
import Entities.BorrowItem;
import Entities.OrderDetail;
import Model.DAOBook;
import Model.DAOBookHold;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DBConnection;
import ViewModel.BorrowRenewalDecision;
import ViewModel.PurchaseRequestItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.sql.PreparedStatement;

public class BorrowTransactionService {

    private final DAOBook daoBook;
    private final DAOBorrow daoBorrow;
    private final DAOBorrowItem daoBorrowItem;
    private final DAOOrders daoOrders;
    private final DAOOrderDetail daoOrderDetail;
    private final DAOBookPrice daoBookPrice;
    private final BorrowHelper helper;

    public BorrowTransactionService(DAOBook daoBook, DAOBorrow daoBorrow, DAOBorrowItem daoBorrowItem,
            DAOOrders daoOrders, DAOOrderDetail daoOrderDetail, DAOBookPrice daoBookPrice, BorrowHelper helper) {
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoBorrowItem = daoBorrowItem;
        this.daoOrders = daoOrders;
        this.daoOrderDetail = daoOrderDetail;
        this.daoBookPrice = daoBookPrice;
        this.helper = helper;
    }

    /**
     * NEW: Create a PENDING borrow request (student submits, admin approves
     * later). Does NOT decrease Available — that happens on approval.
     *
     * @param bookIds list of BookIDs from the borrow cart
     */
    public int createPendingBorrow(int studentId, int staffId, List<Integer> bookIds,
            LocalDate borrowDate, LocalDate dueDate) throws SQLException {
        if (bookIds == null || bookIds.isEmpty()) {
            throw new SQLException("Gio muon trong, khong co sach de muon.");
        }
        if (staffId <= 0) {
            throw new SQLException("Khong xac dinh duoc tai khoan gui yeu cau muon.");
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            // Validate all books are still available
            for (int bookId : bookIds) {
                int available = daoBook.getAvailable(con, bookId);
                if (available <= 0) {
                    throw new SQLException("Sach ID=" + bookId + " da het. Vui long xoa khoi gio muon.");
                }
            }

            // Create borrow record with Pending status
            int borrowId = daoBorrow.insertPending(con, studentId, staffId, borrowDate, dueDate);

            // Insert each book as a BorrowItem (qty=1 per book)
            for (int bookId : bookIds) {
                int affected = daoBorrowItem.insert(con, new BorrowItem(borrowId, bookId, 1));
                if (affected == 0) {
                    throw new SQLException("Khong the them sach ID=" + bookId + " vao phieu muon.");
                }
            }

            // NOTE: We do NOT decrease Available here.
            // Available will be decreased when admin APPROVES the request.
            con.commit();
            return borrowId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    /**
     * ADMIN: Approve a pending borrow request. This is where we actually
     * decrease Available.
     */
    public void approveBorrow(int borrowId, int approverStaffId) throws SQLException {
        if (approverStaffId <= 0) {
            throw new SQLException("Khong xac dinh duoc nhan vien duyet phieu muon.");
        }
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            // Verify it's still Pending
            String status = daoBorrow.getStatusForUpdate(con, borrowId);
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Phieu muon nay khong con o trang thai Pending (hien tai: " + status + ").");
            }

            // Get all items
            List<BorrowItem> items = daoBorrowItem.getByBorrowId(con, borrowId);
            if (items.isEmpty()) {
                throw new SQLException("Phieu muon khong co sach.");
            }

            // Decrease available for each book
            for (BorrowItem item : items) {
                int decreased = daoBook.decreaseAvailable(con, item.getBookID(), item.getQuantity());
                if (decreased == 0) {
                    throw new SQLException("Sach ID=" + item.getBookID()
                            + " khong du so luong de cho muon.");
                }
            }

            // Update status to Borrowing
            if (daoBorrow.updateStatus(con, borrowId, "Borrowing", approverStaffId) == 0) {
                throw new SQLException("Khong the cap nhat trang thai phieu muon.");
            }

            // Auto fulfill hold nếu student có hold cho sách này
            DAOBookHold daoBookHold = new DAOBookHold();
            for (BorrowItem item : items) {
                // Tìm hold Notified của student cho bookId này → set Fulfilled
                try {
                    String fulfillSql = "UPDATE BookHold SET Status = 'Fulfilled' "
                            + "WHERE StudentID = (SELECT StudentID FROM Borrow WHERE BorrowID = ?) "
                            + "AND BookID = ? AND Status = 'Notified'";
                    try (PreparedStatement psHold = con.prepareStatement(fulfillSql)) {
                        psHold.setInt(1, borrowId);
                        psHold.setInt(2, item.getBookID());
                        psHold.executeUpdate();
                    }
                } catch (SQLException ignored) {
                }
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    /**
     * ADMIN: Reject a pending borrow request. No stock changes needed since we
     * never reserved.
     */
    public void rejectBorrow(int borrowId, int reviewerStaffId) throws SQLException {
        if (reviewerStaffId <= 0) {
            throw new SQLException("Khong xac dinh duoc nhan vien xu ly phieu muon.");
        }
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = daoBorrow.getStatusForUpdate(con, borrowId);
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Phieu muon nay khong con o trang thai Pending (hien tai: " + status + ").");
            }

            if (daoBorrow.updateStatus(con, borrowId, "Rejected", reviewerStaffId) == 0) {
                throw new SQLException("Khong the cap nhat trang thai phieu muon.");
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    /**
     * EXISTING: Direct borrow transaction (used by admin create form). Status =
     * Borrowing immediately, decreases Available.
     */
    public void createBorrowTransaction(int studentId, int staffId, int bookId, int quantity,
            LocalDate borrowDate, LocalDate dueDate) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            int available = daoBook.getAvailable(con, bookId);
            if (available < quantity) {
                throw new SQLException("So luong sach con lai khong du. Con lai: " + available);
            }

            int borrowId = daoBorrow.insert(con, studentId, staffId, borrowDate, dueDate, "Borrowing");
            int borrowItemAffected = daoBorrowItem.insert(con, new BorrowItem(borrowId, bookId, quantity));
            if (borrowItemAffected == 0) {
                throw new SQLException("Khong the tao chi tiet muon.");
            }

            int decreaseAffected = daoBook.decreaseAvailable(con, bookId, quantity);
            if (decreaseAffected == 0) {
                throw new SQLException("Khong du so luong sach de muon.");
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    /**
     * EXISTING: Create a pending purchase order.
     */
    public int createPendingOrder(int studentId, int staffId,
            List<PurchaseRequestItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Don mua khong co sach.");
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            double totalAmount = 0;
            for (PurchaseRequestItem item : items) {
                int available = daoBook.getAvailable(con, item.getBookID());
                if (available < item.getQuantity()) {
                    throw new SQLException("Khong du ton kho cho sach id=" + item.getBookID());
                }
                double currentPrice = daoBookPrice.getCurrentSellingPrice(con, item.getBookID());
                if (currentPrice <= 0) {
                    throw new SQLException("Sach id=" + item.getBookID() + " chua co gia ban hop le.");
                }
                item.setUnitPrice(currentPrice);
                totalAmount += currentPrice * item.getQuantity();
            }

            int orderId = daoOrders.insertPending(con, studentId, staffId, totalAmount);
            for (PurchaseRequestItem item : items) {
                int affected = daoOrderDetail.insert(con,
                        new OrderDetail(orderId, item.getBookID(), item.getQuantity(), item.getUnitPrice()));
                if (affected == 0) {
                    throw new SQLException("Khong the tao chi tiet don hang cho sach id=" + item.getBookID());
                }
            }

            con.commit();
            return orderId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    public BorrowRenewalDecision renewBorrowTransaction(int borrowId, int studentId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            Borrow borrow = daoBorrow.getOwnedByStudentForUpdate(con, borrowId, studentId);
            if (borrow == null) {
                throw new SQLException("Khong tim thay phieu muon hop le de gia han.");
            }

            BorrowRenewalDecision decision = helper.evaluateRenewal(borrow);
            if (!decision.isEligible()) {
                throw new SQLException(decision.getMessage());
            }

            if (daoBorrow.updateDueDate(con, borrowId, decision.getNextDueDate()) == 0) {
                throw new SQLException("Khong the cap nhat han tra moi.");
            }

            con.commit();
            return decision;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
}
