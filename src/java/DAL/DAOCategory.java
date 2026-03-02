package DAL;

import entities.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOCategory {

    public List<Category> getAll() throws SQLException {
        String sql = "SELECT CategoryID, CategoryName FROM Category ORDER BY CategoryID DESC";
        List<Category> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("CategoryID"), rs.getString("CategoryName")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Category getById(int id) throws SQLException {
        String sql = "SELECT CategoryID, CategoryName FROM Category WHERE CategoryID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Category(rs.getInt("CategoryID"), rs.getString("CategoryName"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Category c) throws SQLException {
        String sql = "INSERT INTO Category(CategoryName) VALUES(?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getCategoryName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        c.setCategoryID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Category c) throws SQLException {
        String sql = "UPDATE Category SET CategoryName=? WHERE CategoryID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCategoryName());
            ps.setInt(2, c.getCategoryID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
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
