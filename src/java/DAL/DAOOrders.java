package DAL;

import entities.Orders;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOOrders {

    public List<Orders> getAll() throws SQLException {
        String sql = "SELECT OrderID, StudentID, StaffID, OrderDate, TotalAmount, Status FROM Orders ORDER BY OrderID DESC";
        List<Orders> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Orders(rs.getInt("OrderID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                            rs.getString("OrderDate"), rs.getDouble("TotalAmount"), rs.getString("Status"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Orders o) throws SQLException {
        String sql = "INSERT INTO Orders(StudentID, StaffID, OrderDate, TotalAmount, Status) VALUES(?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getStudentID());
            ps.setInt(2, o.getStaffID());
            ps.setString(3, o.getOrderDate());
            ps.setDouble(4, o.getTotalAmount());
            ps.setString(5, o.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        o.setOrderID(keys.getInt(1));
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
