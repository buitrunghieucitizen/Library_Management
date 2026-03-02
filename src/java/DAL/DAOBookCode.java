package DAL;

import entities.BookCode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBookCode {

    public List<BookCode> getAll() throws SQLException {
        String sql = "SELECT BookCodeID, BookID, BookCode FROM BookCode ORDER BY BookCodeID DESC";
        List<BookCode> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new BookCode(rs.getInt("BookCodeID"), rs.getInt("BookID"), rs.getString("BookCode")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BookCode> getByBookId(int bookId) throws SQLException {
        String sql = "SELECT BookCodeID, BookID, BookCode FROM BookCode WHERE BookID = ?";
        List<BookCode> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookCode(rs.getInt("BookCodeID"), rs.getInt("BookID"), rs.getString("BookCode")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public BookCode getById(int id) throws SQLException {
        String sql = "SELECT BookCodeID, BookID, BookCode FROM BookCode WHERE BookCodeID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new BookCode(rs.getInt("BookCodeID"), rs.getInt("BookID"), rs.getString("BookCode"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(BookCode bc) throws SQLException {
        String sql = "INSERT INTO BookCode(BookID, BookCode) VALUES(?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bc.getBookID());
            ps.setString(2, bc.getBookCode());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        bc.setBookCodeID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(BookCode bc) throws SQLException {
        String sql = "UPDATE BookCode SET BookID=?, BookCode=? WHERE BookCodeID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bc.getBookID());
            ps.setString(2, bc.getBookCode());
            ps.setInt(3, bc.getBookCodeID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM BookCode WHERE BookCodeID = ?";
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
