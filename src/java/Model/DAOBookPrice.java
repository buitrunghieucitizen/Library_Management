package Model;

import Entities.BookPrice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBookPrice {

    public List<BookPrice> getByBookId(int bookId) throws SQLException {
        String sql = "SELECT BookID, PriceID, StartDate, EndDate FROM BookPrice WHERE BookID = ?";
        List<BookPrice> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookPrice(rs.getInt("BookID"), rs.getInt("PriceID"),
                            rs.getString("StartDate"), rs.getString("EndDate")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BookPrice> getAll() throws SQLException {
        String sql = "SELECT BookID, PriceID, StartDate, EndDate FROM BookPrice";
        List<BookPrice> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BookPrice(rs.getInt("BookID"), rs.getInt("PriceID"),
                        rs.getString("StartDate"), rs.getString("EndDate")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(BookPrice bp) throws SQLException {
        String sql = "INSERT INTO BookPrice(BookID, PriceID, StartDate, EndDate) VALUES(?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bp.getBookID());
            ps.setInt(2, bp.getPriceID());
            ps.setString(3, bp.getStartDate());
            ps.setString(4, bp.getEndDate());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int update(BookPrice bp) throws SQLException {
        String sql = "UPDATE BookPrice SET EndDate=? WHERE BookID=? AND PriceID=? AND StartDate=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, bp.getEndDate());
            ps.setInt(2, bp.getBookID());
            ps.setInt(3, bp.getPriceID());
            ps.setString(4, bp.getStartDate());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int bookId, int priceId, String startDate) throws SQLException {
        String sql = "DELETE FROM BookPrice WHERE BookID = ? AND PriceID = ? AND StartDate = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, priceId);
            ps.setString(3, startDate);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
