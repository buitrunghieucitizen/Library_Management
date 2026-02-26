package Model;

import Entities.Author;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOAuthor {

    public List<Author> getAll() throws SQLException {
        String sql = "SELECT AuthorID, AuthorName FROM Author ORDER BY AuthorID DESC";
        List<Author> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Author(rs.getInt("AuthorID"), rs.getString("AuthorName")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Author getById(int id) throws SQLException {
        String sql = "SELECT AuthorID, AuthorName FROM Author WHERE AuthorID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Author(rs.getInt("AuthorID"), rs.getString("AuthorName"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Author a) throws SQLException {
        String sql = "INSERT INTO Author(AuthorName) VALUES(?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getAuthorName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        a.setAuthorID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Author a) throws SQLException {
        String sql = "UPDATE Author SET AuthorName=? WHERE AuthorID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getAuthorName());
            ps.setInt(2, a.getAuthorID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Author WHERE AuthorID = ?";
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
