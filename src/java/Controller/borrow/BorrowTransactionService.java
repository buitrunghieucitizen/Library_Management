package Controller.borrow;

import Entities.BorrowItem;
import Entities.OrderDetail;
import Model.DAOBook;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DBConnection;
import ViewModel.PurchaseRequestItem;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BorrowTransactionService {

    private final DAOBook daoBook;
    private final DAOBorrow daoBorrow;
    private final DAOBorrowItem daoBorrowItem;
    private final DAOOrders daoOrders;
    private final DAOOrderDetail daoOrderDetail;
    private final DAOBookPrice daoBookPrice;

    public BorrowTransactionService(DAOBook daoBook, DAOBorrow daoBorrow, DAOBorrowItem daoBorrowItem,
            DAOOrders daoOrders, DAOOrderDetail daoOrderDetail, DAOBookPrice daoBookPrice) {
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoBorrowItem = daoBorrowItem;
        this.daoOrders = daoOrders;
        this.daoOrderDetail = daoOrderDetail;
        this.daoBookPrice = daoBookPrice;
    }

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
                con.rollback();
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

    public int createPendingOrder(int studentId, int staffId, List<PurchaseRequestItem> items) throws SQLException {
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
}
