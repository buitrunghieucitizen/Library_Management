package Model;

import Entities.BookAuthor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBookAuthor {

    public List<BookAuthor> getByBookId(int bookId) throws SQLException {
        String sql = "SELECT BookID, AuthorID FROM BookAuthor WHERE BookID = ?";
        List<BookAuthor> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookAuthor(rs.getInt("BookID"), rs.getInt("AuthorID")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<BookAuthor> getByAuthorId(int authorId) throws SQLException {
        String sql = "SELECT BookID, AuthorID FROM BookAuthor WHERE AuthorID = ?";
        List<BookAuthor> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookAuthor(rs.getInt("BookID"), rs.getInt("AuthorID")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(BookAuthor ba) throws SQLException {
        String sql = "INSERT INTO BookAuthor(BookID, AuthorID) VALUES(?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, ba.getBookID());
            ps.setInt(2, ba.getAuthorID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int bookId, int authorId) throws SQLException {
        String sql = "DELETE FROM BookAuthor WHERE BookID = ? AND AuthorID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, authorId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
