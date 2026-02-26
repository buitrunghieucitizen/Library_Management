package Model;

import Entities.Price;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOPrice {

    public List<Price> getAll() throws SQLException {
        String sql = "SELECT PriceID, Amount, Currency, Note FROM Price ORDER BY PriceID DESC";
        List<Price> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Price(rs.getInt("PriceID"), rs.getDouble("Amount"),
                        rs.getString("Currency"), rs.getString("Note")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Price getById(int id) throws SQLException {
        String sql = "SELECT PriceID, Amount, Currency, Note FROM Price WHERE PriceID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Price(rs.getInt("PriceID"), rs.getDouble("Amount"),
                            rs.getString("Currency"), rs.getString("Note"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Price p) throws SQLException {
        String sql = "INSERT INTO Price(Amount, Currency, Note) VALUES(?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, p.getAmount());
            ps.setString(2, p.getCurrency());
            ps.setString(3, p.getNote());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        p.setPriceID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Price p) throws SQLException {
        String sql = "UPDATE Price SET Amount=?, Currency=?, Note=? WHERE PriceID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, p.getAmount());
            ps.setString(2, p.getCurrency());
            ps.setString(3, p.getNote());
            ps.setInt(4, p.getPriceID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Price WHERE PriceID = ?";
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
