package DAL;

import entities.BorrowItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBorrowItem {

    public List<BorrowItem> getByBorrowId(int borrowId) throws SQLException {
        String sql = "SELECT BorrowID, BookID, Quantity FROM BorrowItem WHERE BorrowID = ?";
        List<BorrowItem> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BorrowItem(rs.getInt("BorrowID"), rs.getInt("BookID"), rs.getInt("Quantity")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BorrowItem> getAll() throws SQLException {
        String sql = "SELECT BorrowID, BookID, Quantity FROM BorrowItem";
        List<BorrowItem> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BorrowItem(rs.getInt("BorrowID"), rs.getInt("BookID"), rs.getInt("Quantity")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(BorrowItem bi) throws SQLException {
        String sql = "INSERT INTO BorrowItem(BorrowID, BookID, Quantity) VALUES(?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bi.getBorrowID());
            ps.setInt(2, bi.getBookID());
            ps.setInt(3, bi.getQuantity());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int update(BorrowItem bi) throws SQLException {
        String sql = "UPDATE BorrowItem SET Quantity=? WHERE BorrowID=? AND BookID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bi.getQuantity());
            ps.setInt(2, bi.getBorrowID());
            ps.setInt(3, bi.getBookID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int borrowId, int bookId) throws SQLException {
        String sql = "DELETE FROM BorrowItem WHERE BorrowID = ? AND BookID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            ps.setInt(2, bookId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
