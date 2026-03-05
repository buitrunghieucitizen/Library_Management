package Controller;

import Entities.Staff;
import Model.DBConnection;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "OrdersController", urlPatterns = {"/admin/orders"})
public class OrdersController extends HttpServlet {

    private static final String ORDERS_PATH = "/admin/orders";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageOrders(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        try {
            req.setAttribute("orders", fetchOrderRows());
            req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
            req.getRequestDispatcher("/WEB-INF/views/orders/list.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageOrders(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        String action = req.getParameter("action");
        if (!"approve".equals(action) && !"reject".equals(action)) {
            resp.sendRedirect(req.getContextPath() + ORDERS_PATH);
            return;
        }

        int orderId;
        try {
            orderId = Integer.parseInt(req.getParameter("orderID"));
        } catch (NumberFormatException e) {
            redirectWithMessage(req, resp, "error", "OrderID khong hop le.");
            return;
        }

        try {
            if ("approve".equals(action)) {
                approveOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da duyet don hang.");
            } else {
                rejectOrder(orderId, staff.getStaffID());
                redirectWithMessage(req, resp, "msg", "Da tu choi don hang.");
            }
        } catch (SQLException e) {
            redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    private boolean canManageOrders(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private void approveOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = getOrderStatusForUpdate(con, orderId);
            if (status == null) {
                throw new SQLException("Khong tim thay don hang.");
            }
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Chi co the duyet don Pending.");
            }

            List<OrderItemRow> items = getOrderItems(con, orderId);
            if (items.isEmpty()) {
                throw new SQLException("Don hang khong co chi tiet sach.");
            }

            for (OrderItemRow item : items) {
                decreaseBookStock(con, item.getBookID(), item.getQuantity());
            }

            updateOrderStatus(con, orderId, "Approved", staffId);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void rejectOrder(int orderId, int staffId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = getOrderStatusForUpdate(con, orderId);
            if (status == null) {
                throw new SQLException("Khong tim thay don hang.");
            }
            if (!"Pending".equalsIgnoreCase(status)) {
                throw new SQLException("Chi co the tu choi don Pending.");
            }

            updateOrderStatus(con, orderId, "Rejected", staffId);
            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private String getOrderStatusForUpdate(Connection con, int orderId) throws SQLException {
        String sql = "SELECT Status FROM Orders WITH (UPDLOCK, ROWLOCK) WHERE OrderID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        }
        return null;
    }

    private List<OrderItemRow> getOrderItems(Connection con, int orderId) throws SQLException {
        String sql = "SELECT od.BookID, od.Quantity, od.UnitPrice, b.BookName "
                + "FROM OrderDetail od "
                + "JOIN Book b ON b.BookID = od.BookID "
                + "WHERE od.OrderID = ?";
        List<OrderItemRow> items = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItemRow(
                            rs.getInt("BookID"),
                            rs.getString("BookName"),
                            rs.getInt("Quantity"),
                            rs.getDouble("UnitPrice")));
                }
            }
        }
        return items;
    }

    private void decreaseBookStock(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book "
                + "SET Quantity = Quantity - ?, Available = Available - ? "
                + "WHERE BookID = ? AND Quantity >= ? AND Available >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, bookId);
            ps.setInt(4, quantity);
            ps.setInt(5, quantity);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong du ton kho de duyet don cho sach id=" + bookId);
            }
        }
    }

    private void updateOrderStatus(Connection con, int orderId, String status, int staffId) throws SQLException {
        String sql = "UPDATE Orders SET Status = ?, StaffID = ? WHERE OrderID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, staffId);
            ps.setInt(3, orderId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Cap nhat trang thai don hang that bai.");
            }
        }
    }

    private List<OrderRow> fetchOrderRows() throws SQLException {
        String sql = "SELECT o.OrderID, s.StudentName, "
                + "CASE WHEN o.Status = 'Pending' THEN N'Chua xu ly' ELSE st.StaffName END AS StaffName, "
                + "CONVERT(varchar(10), o.OrderDate, 23) AS OrderDate, "
                + "o.TotalAmount, o.Status, "
                + "ISNULL(STRING_AGG(CONCAT(b.BookName, ' (x', od.Quantity, ', ', CONVERT(varchar(20), od.UnitPrice), ')'), ', '), '') AS Items "
                + "FROM Orders o "
                + "JOIN Student s ON s.StudentID = o.StudentID "
                + "JOIN Staff st ON st.StaffID = o.StaffID "
                + "LEFT JOIN OrderDetail od ON od.OrderID = o.OrderID "
                + "LEFT JOIN Book b ON b.BookID = od.BookID "
                + "GROUP BY o.OrderID, s.StudentName, st.StaffName, o.OrderDate, o.TotalAmount, o.Status "
                + "ORDER BY o.OrderID DESC";

        List<OrderRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new OrderRow(
                        rs.getInt("OrderID"),
                        rs.getString("StudentName"),
                        rs.getString("StaffName"),
                        rs.getString("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status"),
                        rs.getString("Items")));
            }
        } finally {
            con.close();
        }

        return rows;
    }

    private void redirectWithMessage(HttpServletRequest req, HttpServletResponse resp, String key, String value) throws IOException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + ORDERS_PATH + "?" + key + "=" + encoded);
    }

    public static class OrderRow {
        private final int orderID;
        private final String studentName;
        private final String staffName;
        private final String orderDate;
        private final double totalAmount;
        private final String status;
        private final String items;

        public OrderRow(int orderID, String studentName, String staffName, String orderDate,
                double totalAmount, String status, String items) {
            this.orderID = orderID;
            this.studentName = studentName;
            this.staffName = staffName;
            this.orderDate = orderDate;
            this.totalAmount = totalAmount;
            this.status = status;
            this.items = items;
        }

        public int getOrderID() {
            return orderID;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getStaffName() {
            return staffName;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public String getItems() {
            return items;
        }
    }

    private static class OrderItemRow {
        private final int bookID;
        private final String bookName;
        private final int quantity;
        private final double unitPrice;

        OrderItemRow(int bookID, String bookName, int quantity, double unitPrice) {
            this.bookID = bookID;
            this.bookName = bookName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public int getBookID() {
            return bookID;
        }

        public String getBookName() {
            return bookName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }
    }
}

