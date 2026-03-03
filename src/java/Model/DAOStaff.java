package Model;

import Entities.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOStaff {

    // ===== LOGIN =====
    public Staff login(String username, String password) throws SQLException {
        String sql = "SELECT StaffID, StaffName, Username, Password FROM Staff WHERE Username = ? AND Password = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Staff(rs.getInt("StaffID"), rs.getString("StaffName"),
                            rs.getString("Username"), rs.getString("Password"));
                }
            }
        } finally { con.close(); }
        return null;
    }

    // ===== CRUD =====
    public List<Staff> getAll() throws SQLException {
        String sql = "SELECT StaffID, StaffName, Username, Password FROM Staff ORDER BY StaffID DESC";
        List<Staff> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Staff(rs.getInt("StaffID"), rs.getString("StaffName"),
                        rs.getString("Username"), rs.getString("Password")));
            }
        } finally { con.close(); }
        return list;
    }

    public Staff getById(int id) throws SQLException {
        String sql = "SELECT StaffID, StaffName, Username, Password FROM Staff WHERE StaffID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new Staff(rs.getInt("StaffID"), rs.getString("StaffName"),
                        rs.getString("Username"), rs.getString("Password"));
            }
        } finally { con.close(); }
        return null;
    }

    public int insert(Staff s) throws SQLException {
        String sql = "INSERT INTO Staff(StaffName, Username, Password) VALUES(?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStaffName());
            ps.setString(2, s.getUsername());
            ps.setString(3, s.getPassword());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) s.setStaffID(keys.getInt(1));
                }
            }
            return affected;
        } finally { con.close(); }
    }

    public int update(Staff s) throws SQLException {
        String sql = "UPDATE Staff SET StaffName=?, Username=?, Password=? WHERE StaffID=?";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getStaffName());
            ps.setString(2, s.getUsername());
            ps.setString(3, s.getPassword());
            ps.setInt(4, s.getStaffID());
            return ps.executeUpdate();
        } finally { con.close(); }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Staff WHERE StaffID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } finally { con.close(); }
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Staff WHERE Username = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } finally {
            con.close();
        }
        return false;
    }
}
