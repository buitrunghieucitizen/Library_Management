package Model;

import Entities.Orders;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOOrders {

    public List<Orders> getAll() throws SQLException {
        String sql = "SELECT OrderID, StudentID, StaffID, OrderDate, TotalAmount, Status FROM Orders ORDER BY OrderID DESC";
        List<Orders> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Orders(rs.getInt("OrderID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                        rs.getString("OrderDate"), rs.getDouble("TotalAmount"), rs.getString("Status")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Orders getById(int id) throws SQLException {
        String sql = "SELECT OrderID, StudentID, StaffID, OrderDate, TotalAmount, Status FROM Orders WHERE OrderID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Orders(rs.getInt("OrderID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                            rs.getString("OrderDate"), rs.getDouble("TotalAmount"), rs.getString("Status"));
                }
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Orders o) throws SQLException {
        String sql = "INSERT INTO Orders(StudentID, StaffID, OrderDate, TotalAmount, Status) VALUES(?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getStudentID());
            ps.setInt(2, o.getStaffID());
            ps.setString(3, o.getOrderDate());
            ps.setDouble(4, o.getTotalAmount());
            ps.setString(5, o.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        o.setOrderID(keys.getInt(1));
                    }
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Orders o) throws SQLException {
        String sql = "UPDATE Orders SET StudentID=?, StaffID=?, OrderDate=?, TotalAmount=?, Status=? WHERE OrderID=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, o.getStudentID());
            ps.setInt(2, o.getStaffID());
            ps.setString(3, o.getOrderDate());
            ps.setDouble(4, o.getTotalAmount());
            ps.setString(5, o.getStatus());
            ps.setInt(6, o.getOrderID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Orders WHERE OrderID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int insertPending(Connection con, int studentId, int staffId, double totalAmount) throws SQLException {
        String sql = "INSERT INTO Orders(StudentID, StaffID, OrderDate, TotalAmount, Status) VALUES(?,?,GETDATE(),?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            ps.setDouble(3, totalAmount);
            ps.setString(4, "Pending");
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the tao don hang.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong lay duoc OrderID moi.");
    }

    public String getStatusForUpdate(Connection con, int orderId) throws SQLException {
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

    public int updateStatus(Connection con, int orderId, String status, int staffId) throws SQLException {
        String sql = "UPDATE Orders SET Status = ?, StaffID = ? WHERE OrderID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, staffId);
            ps.setInt(3, orderId);
            return ps.executeUpdate();
        }
    }

    public List<OrderRow> getOrderRows() throws SQLException {
        String sql = "SELECT o.OrderID, s.StudentName, "
                // 1. Đã cập nhật thêm trạng thái Sẵn sàng và Hàng chờ
                + "CASE WHEN o.Status IN ('Pending', N'Sẵn sàng', N'Hàng chờ') THEN N'Chưa xử lý' ELSE st.StaffName END AS StaffName, "
                + "CONVERT(varchar(10), o.OrderDate, 23) AS OrderDate, "
                + "o.TotalAmount, o.Status, "
                + "ISNULL(STRING_AGG(CONCAT(b.BookName, ' (x', od.Quantity, ', ', CONVERT(varchar(20), od.UnitPrice), ')'), ', '), '') AS Items "
                + "FROM Orders o "
                + "JOIN Student s ON s.StudentID = o.StudentID "
                // 2. ĐÃ SỬA THÀNH LEFT JOIN ĐỂ KHÔNG BỊ MẤT ĐƠN HÀNG
                + "LEFT JOIN Staff st ON st.StaffID = o.StaffID "
                + "LEFT JOIN OrderDetail od ON od.OrderID = o.OrderID "
                + "LEFT JOIN Book b ON b.BookID = od.BookID "
                + "GROUP BY o.OrderID, s.StudentName, st.StaffName, o.OrderDate, o.TotalAmount, o.Status "
                + "ORDER BY o.OrderID DESC";

        List<OrderRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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

    // 1. Hàm lấy danh sách đơn mua của riêng 1 sinh viên
    public List<OrderRow> getOrderRowsByStudent(int studentId) throws SQLException {
        String sql = "SELECT o.OrderID, s.StudentName, "
                + "CASE WHEN o.Status IN ('Pending', N'Hàng chờ', N'Sẵn sàng') THEN N'Chưa xử lý' ELSE st.StaffName END AS StaffName, "
                + "CONVERT(varchar(10), o.OrderDate, 23) AS OrderDate, "
                + "o.TotalAmount, o.Status, "
                + "ISNULL(STRING_AGG(CONCAT(b.BookName, ' (x', od.Quantity, ', ', CONVERT(varchar(20), od.UnitPrice), ')'), ', '), '') AS Items "
                + "FROM Orders o "
                + "JOIN Student s ON s.StudentID = o.StudentID "
                + "LEFT JOIN Staff st ON st.StaffID = o.StaffID "
                + "LEFT JOIN OrderDetail od ON od.OrderID = o.OrderID "
                + "LEFT JOIN Book b ON b.BookID = od.BookID "
                + "WHERE o.StudentID = ? "
                + "GROUP BY o.OrderID, s.StudentName, st.StaffName, o.OrderDate, o.TotalAmount, o.Status "
                + "ORDER BY o.OrderID DESC";

        List<OrderRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new OrderRow(
                            rs.getInt("OrderID"),
                            rs.getString("StudentName"),
                            rs.getString("StaffName"),
                            rs.getString("OrderDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status"),
                            rs.getString("Items")
                    ));
                }
            }
        } finally {
            con.close();
        }
        return rows;
    }

    // 2. Hàm tạo đơn hàng với Trạng thái tùy chỉnh
    public int insertOrderCustomStatus(Connection con, int studentId, int staffId, double totalAmount, String status) throws SQLException {
        String sql = "INSERT INTO Orders(StudentID, StaffID, OrderDate, TotalAmount, Status) VALUES(?,?,GETDATE(),?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            ps.setDouble(3, totalAmount);
            ps.setString(4, status); // Chèn N'Sẵn sàng' hoặc N'Hàng chờ'

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Không thể tạo đơn hàng.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1); // Trả về OrderID vừa được IDENTITY(1,1) sinh ra
                }
            }
        }
        throw new SQLException("Không lấy được OrderID mới.");
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
}
