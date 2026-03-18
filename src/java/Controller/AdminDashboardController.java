package Controller;

import Entities.Book;
import Entities.Borrow;
import Entities.Orders;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOOrders;
import Model.DAOStaff;
import Model.DAOStudent;
import Utils.RoleUtils;
import ViewModel.BorrowRow;
import ViewModel.OrderRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int LOW_STOCK_THRESHOLD = 3;
    private static final int RECENT_ITEMS_LIMIT = 5;
    private static final String STATUS_ORDER_WAITING = "H\u00e0ng ch\u1edd";
    private static final String STATUS_ORDER_READY = "S\u1eb5n s\u00e0ng";
    private static final String STATUS_ORDER_DELIVERED = "\u0110\u00e3 giao";
    private static final String STATUS_ORDER_CANCELLED = "\u0110\u00e3 h\u1ee7y";

    private final DAOBook daoBook = new DAOBook();
    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOOrders daoOrders = new DAOOrders();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean isAdminDashboard = RoleUtils.isAdmin(request);

        request.setAttribute("isAdminDashboard", isAdminDashboard);
        request.setAttribute("dashboardLabel", isAdminDashboard ? "ADMIN CONTROL CENTER" : "STAFF OPERATIONS");
        request.setAttribute("dashboardTitle", isAdminDashboard ? "Admin Dashboard" : "Staff Dashboard");
        request.setAttribute("dashboardIntro", isAdminDashboard
                ? "Tong quan he thong thu vien de theo doi kho sach, nguoi dung va giao dich quan trong."
                : "Tong quan van hanh de theo doi muon tra, xu ly don va tinh trang kho sach.");
        request.setAttribute("dashboardRoleLabel", isAdminDashboard
                ? "Toan quyen quan tri"
                : "Dieu phoi van hanh");
        request.setAttribute("managedAreas", isAdminDashboard ? 9 : 5);
        request.setAttribute("todayLabel", LocalDate.now().format(DATE_FORMATTER));

        try {
            populateStats(request, isAdminDashboard);
        } catch (SQLException e) {
            request.setAttribute("dashboardLoadError", "Khong tai duoc du lieu dashboard: " + e.getMessage());
        }

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
    }

    private void populateStats(HttpServletRequest request, boolean isAdminDashboard) throws SQLException {
        List<Book> books = daoBook.getAll();
        List<Borrow> borrows = daoBorrow.getAll();
        List<Orders> orders = daoOrders.getAll();
        List<BorrowRow> borrowRows = daoBorrow.getBorrowRows();
        List<OrderRow> orderRows = daoOrders.getOrderRows();

        int totalBooks = books.size();
        int totalCopies = sumTotalCopies(books);
        int availableCopies = sumAvailableCopies(books);
        int borrowedCopies = Math.max(0, totalCopies - availableCopies);
        int totalStudents = daoStudent.getAll().size();
        int activeBorrows = countBorrowsByStatus(borrows, "Borrowing", "Overdue");
        int overdueBorrows = countBorrowsByStatus(borrows, "Overdue");
        int pendingOrders = countOrdersByStatus(orders, "Pending", STATUS_ORDER_WAITING, STATUS_ORDER_READY);
        int readyOrders = countOrdersByStatus(orders, "Approved", STATUS_ORDER_READY);
        int completedOrders = countOrdersByStatus(orders, STATUS_ORDER_DELIVERED);
        int cancelledOrders = countOrdersByStatus(orders, "Rejected", STATUS_ORDER_CANCELLED);
        int lowStockCount = countLowStockBooks(books, LOW_STOCK_THRESHOLD);
        int outOfStockCount = countOutOfStockBooks(books);
        int stockCoverageValue = totalCopies == 0 ? 0 : calculatePercent(availableCopies, totalCopies);
        int borrowHealthValue = activeBorrows == 0 ? 100 : calculatePercent(Math.max(0, activeBorrows - overdueBorrows), activeBorrows);
        int orderFulfillmentValue = orders.isEmpty() ? 100 : calculatePercent(completedOrders, orders.size());

        request.setAttribute("totalBooks", totalBooks);
        request.setAttribute("totalCopies", totalCopies);
        request.setAttribute("availableCopies", availableCopies);
        request.setAttribute("borrowedCopies", borrowedCopies);
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("activeBorrows", activeBorrows);
        request.setAttribute("overdueBorrows", overdueBorrows);
        request.setAttribute("pendingOrders", pendingOrders);
        request.setAttribute("readyOrders", readyOrders);
        request.setAttribute("completedOrders", completedOrders);
        request.setAttribute("cancelledOrders", cancelledOrders);
        request.setAttribute("lowStockCount", lowStockCount);
        request.setAttribute("outOfStockCount", outOfStockCount);
        request.setAttribute("priorityCount", overdueBorrows + pendingOrders + lowStockCount);
        request.setAttribute("totalTransactions", borrows.size() + orders.size());
        request.setAttribute("stockCoverageValue", stockCoverageValue);
        request.setAttribute("stockCoverageLabel", stockCoverageValue + "%");
        request.setAttribute("borrowHealthValue", borrowHealthValue);
        request.setAttribute("borrowHealthLabel", borrowHealthValue + "%");
        request.setAttribute("orderFulfillmentValue", orderFulfillmentValue);
        request.setAttribute("orderFulfillmentLabel", orderFulfillmentValue + "%");
        request.setAttribute("recentBorrowRows", slice(borrowRows, RECENT_ITEMS_LIMIT));
        request.setAttribute("recentOrderRows", slice(orderRows, RECENT_ITEMS_LIMIT));
        request.setAttribute("lowStockBooks", selectLowStockBooks(books, LOW_STOCK_THRESHOLD, RECENT_ITEMS_LIMIT));

        if (isAdminDashboard) {
            int totalStaff = daoStaff.getAll().size();
            request.setAttribute("totalStaff", totalStaff);
            request.setAttribute("studentsPerStaffLabel",
                    totalStaff == 0 ? "--" : String.format(Locale.ROOT, "%.1f", (double) totalStudents / totalStaff));
        }
    }

    private int sumTotalCopies(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            total += book.getQuantity();
        }
        return total;
    }

    private int sumAvailableCopies(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            total += book.getAvailable();
        }
        return total;
    }

    private int countLowStockBooks(List<Book> books, int threshold) {
        int total = 0;
        for (Book book : books) {
            if (book.getAvailable() <= threshold) {
                total++;
            }
        }
        return total;
    }

    private int countOutOfStockBooks(List<Book> books) {
        int total = 0;
        for (Book book : books) {
            if (book.getAvailable() <= 0) {
                total++;
            }
        }
        return total;
    }

    private int calculatePercent(int part, int whole) {
        if (whole <= 0) {
            return 0;
        }
        return (int) Math.round(part * 100.0 / whole);
    }

    private int countBorrowsByStatus(List<Borrow> borrows, String... targetStatuses) {
        int total = 0;
        for (Borrow borrow : borrows) {
            if (matchesStatus(borrow.getStatus(), targetStatuses)) {
                total++;
            }
        }
        return total;
    }

    private int countOrdersByStatus(List<Orders> orders, String... targetStatuses) {
        int total = 0;
        for (Orders order : orders) {
            if (matchesStatus(order.getStatus(), targetStatuses)) {
                total++;
            }
        }
        return total;
    }

    private List<Book> selectLowStockBooks(List<Book> books, int threshold, int limit) {
        List<Book> selectedBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAvailable() <= threshold) {
                selectedBooks.add(book);
            }
        }

        selectedBooks.sort(
                Comparator.comparingInt(Book::getAvailable)
                        .thenComparingInt(Book::getQuantity)
                        .thenComparingInt(Book::getBookID));

        return slice(selectedBooks, limit);
    }

    private <T> List<T> slice(List<T> source, int limit) {
        if (source == null || source.isEmpty() || limit <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(source.subList(0, Math.min(limit, source.size())));
    }

    private boolean matchesStatus(String value, String... targetStatuses) {
        if (value == null || targetStatuses == null) {
            return false;
        }

        String normalizedValue = value.trim().toLowerCase(Locale.ROOT);
        for (String targetStatus : targetStatuses) {
            if (targetStatus != null && normalizedValue.equals(targetStatus.trim().toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
