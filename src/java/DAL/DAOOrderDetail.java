package DAL;

import entities.OrderDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOOrderDetail {

    public List<OrderDetail> getByOrderId(int orderId) throws SQLException {
        String sql = "SELECT OrderID, BookID, Quantity, UnitPrice FROM OrderDetail WHERE OrderID = ?";
        List<OrderDetail> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderDetail(rs.getInt("OrderID"), rs.getInt("BookID"),
                            rs.getInt("Quantity"), rs.getDouble("UnitPrice")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<OrderDetail> getAll() throws SQLException {
        String sql = "SELECT OrderID, BookID, Quantity, UnitPrice FROM OrderDetail";
        List<OrderDetail> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new OrderDetail(rs.getInt("OrderID"), rs.getInt("BookID"),
                        rs.getInt("Quantity"), rs.getDouble("UnitPrice")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(OrderDetail od) throws SQLException {
        String sql = "INSERT INTO OrderDetail(OrderID, BookID, Quantity, UnitPrice) VALUES(?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, od.getOrderID());
            ps.setInt(2, od.getBookID());
            ps.setInt(3, od.getQuantity());
            ps.setDouble(4, od.getUnitPrice());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int update(OrderDetail od) throws SQLException {
        String sql = "UPDATE OrderDetail SET Quantity=?, UnitPrice=? WHERE OrderID=? AND BookID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, od.getQuantity());
            ps.setDouble(2, od.getUnitPrice());
            ps.setInt(3, od.getOrderID());
            ps.setInt(4, od.getBookID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int orderId, int bookId) throws SQLException {
        String sql = "DELETE FROM OrderDetail WHERE OrderID = ? AND BookID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, bookId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
