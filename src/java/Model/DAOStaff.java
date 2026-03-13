package Model;

import Entities.GoogleAccount;
import Entities.Staff;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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

    public Staff getByUsername(String username) throws SQLException {
        String normalizedUsername = normalizeUsername(username);
        if (normalizedUsername.isEmpty()) {
            return null;
        }

        String sql = "SELECT StaffID, StaffName, Username, Password FROM Staff WHERE Username = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizedUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Staff(rs.getInt("StaffID"), rs.getString("StaffName"),
                            rs.getString("Username"), rs.getString("Password"));
                }
            }
        } finally {
            con.close();
        }
        return null;
    }

    public Staff loginByGoogle(GoogleAccount googleAccount) throws SQLException {
        if (googleAccount == null) {
            return null;
        }

        String username = normalizeUsername(googleAccount.getEmail());
        if (username.isEmpty()) {
            return null;
        }

        Staff existing = getByUsername(username);
        if (existing != null) {
            return existing;
        }

        String displayName = normalizeDisplayName(googleAccount.getName(), username);
        Staff created = new Staff(displayName, username, UUID.randomUUID().toString());
        try {
            insert(created);
        } catch (SQLException ex) {
            if (!isDuplicateUsername(ex)) {
                throw ex;
            }
        }
        return getByUsername(username);
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

    private String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }

        String normalized = username.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() <= 50) {
            return normalized;
        }

        String suffix = Integer.toHexString(normalized.hashCode());
        int prefixLength = Math.max(0, 50 - suffix.length() - 1);
        if (prefixLength == 0) {
            return normalized.substring(0, 50);
        }
        return normalized.substring(0, prefixLength) + "_" + suffix;
    }

    private String normalizeDisplayName(String displayName, String fallback) {
        String value = displayName == null ? "" : displayName.trim();
        if (value.isEmpty()) {
            value = fallback;
        }
        return value.length() > 100 ? value.substring(0, 100) : value;
    }

    private boolean isDuplicateUsername(SQLException ex) {
        if (ex == null) {
            return false;
        }

        int code = ex.getErrorCode();
        if (code == 2601 || code == 2627) {
            return true;
        }

        String message = ex.getMessage();
        return message != null && message.toLowerCase(Locale.ROOT).contains("uq_staff_username");
    }
}
