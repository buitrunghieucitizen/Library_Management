package Model;

import Entities.Orders;
import ViewModel.OrderRow;
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
        return getOrderRows(null, null, null);
    }

    public List<OrderRow> getOrderRows(Integer studentId, String keyword, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT o.OrderID, s.StudentName, "
                + "CASE WHEN o.Status = 'Pending' THEN N'Chua xu ly' ELSE st.StaffName END AS StaffName, "
                + "CONVERT(varchar(10), o.OrderDate, 23) AS OrderDate, "
                + "o.TotalAmount, o.Status, "
                + "ISNULL(STRING_AGG(CONCAT(b.BookName, ' (x', od.Quantity, ', ', CONVERT(varchar(20), od.UnitPrice), ')'), ', '), '') AS Items "
                + "FROM Orders o "
                + "JOIN Student s ON s.StudentID = o.StudentID "
                + "JOIN Staff st ON st.StaffID = o.StaffID "
                + "LEFT JOIN OrderDetail od ON od.OrderID = o.OrderID "
                + "LEFT JOIN Book b ON b.BookID = od.BookID "
                + "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (studentId != null) {
            sql.append("AND o.StudentID = ? ");
            params.add(studentId);
        }

        String normalizedStatus = status == null ? "" : status.trim();
        if (!normalizedStatus.isEmpty() && !"ALL".equalsIgnoreCase(normalizedStatus)) {
            sql.append("AND o.Status = ? ");
            params.add(normalizedStatus);
        }

        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        if (!normalizedKeyword.isEmpty()) {
            sql.append("AND (CAST(o.OrderID AS varchar(20)) LIKE ? "
                    + "OR s.StudentName LIKE ? "
                    + "OR EXISTS ("
                    + "    SELECT 1 "
                    + "    FROM OrderDetail od2 "
                    + "    JOIN Book b2 ON b2.BookID = od2.BookID "
                    + "    WHERE od2.OrderID = o.OrderID AND b2.BookName LIKE ?"
                    + ")) ");
            String likeValue = "%" + normalizedKeyword + "%";
            params.add(likeValue);
            params.add(likeValue);
            params.add(likeValue);
        }

        sql.append("GROUP BY o.OrderID, s.StudentName, st.StaffName, o.OrderDate, o.TotalAmount, o.Status ")
                .append("ORDER BY o.OrderID DESC");

        List<OrderRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } finally {
            con.close();
        }

        return rows;
    }

}
