package DAL;

import entities.Publisher;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOPublisher {

    public List<Publisher> getAll() throws SQLException {
        String sql = "SELECT PublisherID, PublisherName FROM Publisher ORDER BY PublisherID DESC";
        List<Publisher> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Publisher(rs.getInt("PublisherID"), rs.getString("PublisherName")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Publisher getById(int id) throws SQLException {
        String sql = "SELECT PublisherID, PublisherName FROM Publisher WHERE PublisherID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Publisher(rs.getInt("PublisherID"), rs.getString("PublisherName"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Publisher p) throws SQLException {
        String sql = "INSERT INTO Publisher(PublisherName) VALUES(?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPublisherName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        p.setPublisherID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Publisher p) throws SQLException {
        String sql = "UPDATE Publisher SET PublisherName=? WHERE PublisherID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getPublisherName());
            ps.setInt(2, p.getPublisherID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Publisher WHERE PublisherID = ?";
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
