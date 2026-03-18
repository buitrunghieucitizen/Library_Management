package Model;

import Entities.GoogleAccount;
import Entities.Staff;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DAOStaff {

    // ===== LOGIN =====
    public Staff login(String username, String password) throws SQLException {
        String normalizedUsername = normalizeUsername(username);
        String normalizedEmail = normalizeEmail(username);
        if (normalizedUsername.isEmpty() && normalizedEmail.isEmpty()) {
            return null;
        }

        try (Connection con = openConnection()) {
            boolean hasEmailColumn = hasEmailColumn(con);
            String sql = buildStaffSelect(hasEmailColumn)
                    + (hasEmailColumn
                    ? " WHERE (Username = ? OR Email = ?) AND Password = ?"
                    : " WHERE Username = ? AND Password = ?");
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, normalizedUsername);
                if (hasEmailColumn) {
                    ps.setString(2, normalizedEmail);
                    ps.setString(3, password);
                } else {
                    ps.setString(2, password);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapStaff(rs);
                    }
                }
            }
        }
        return null;
    }

    public Staff getByUsername(String username) throws SQLException {
        String normalizedUsername = normalizeUsername(username);
        if (normalizedUsername.isEmpty()) {
            return null;
        }

        try (Connection con = openConnection()) {
            String sql = buildStaffSelect(hasEmailColumn(con)) + " WHERE Username = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, normalizedUsername);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapStaff(rs);
                    }
                }
            }
        }
        return null;
    }

    public Staff getByEmail(String email) throws SQLException {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return null;
        }

        try (Connection con = openConnection()) {
            if (!hasEmailColumn(con)) {
                return null;
            }

            String sql = buildStaffSelect(true) + " WHERE Email = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, normalizedEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapStaff(rs);
                    }
                }
            }
        }
        return null;
    }

    public Staff loginByGoogle(GoogleAccount googleAccount) throws SQLException {
        if (googleAccount == null) {
            return null;
        }

        String normalizedEmail = normalizeEmail(googleAccount.getEmail());
        if (normalizedEmail.isEmpty()) {
            return null;
        }

        String username = normalizeUsername(googleAccount.getEmail());
        if (username.isEmpty()) {
            return null;
        }

        boolean hasEmailColumn;
        try (Connection con = openConnection()) {
            hasEmailColumn = hasEmailColumn(con);
        }

        Staff existing = hasEmailColumn ? getByEmail(normalizedEmail) : null;
        if (existing == null) {
            existing = getByUsername(username);
        }
        if (existing != null) {
            if (hasEmailColumn && isBlank(existing.getEmail())) {
                existing.setEmail(normalizedEmail);
                update(existing);
            }
            return existing;
        }

        String displayName = normalizeDisplayName(googleAccount.getName(), username);
        Staff created = new Staff(displayName, username,
                hasEmailColumn ? normalizedEmail : null, UUID.randomUUID().toString());
        try {
            insert(created);
        } catch (SQLException ex) {
            if (!isDuplicateUsername(ex)) {
                throw ex;
            }
        }

        Staff createdOrExisting = hasEmailColumn ? getByEmail(normalizedEmail) : null;
        if (createdOrExisting != null) {
            return createdOrExisting;
        }
        return getByUsername(username);
    }

    // ===== CRUD =====
    public List<Staff> getAll() throws SQLException {
        List<Staff> list = new ArrayList<>();
        try (Connection con = openConnection()) {
            String sql = buildStaffSelect(hasEmailColumn(con)) + " ORDER BY StaffID DESC";
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStaff(rs));
                }
            }
        }
        return list;
    }

    public Staff getById(int id) throws SQLException {
        try (Connection con = openConnection()) {
            String sql = buildStaffSelect(hasEmailColumn(con)) + " WHERE StaffID = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapStaff(rs);
                    }
                }
            }
        }
        return null;
    }

    public int insert(Staff s) throws SQLException {
        String normalizedUsername = normalizeUsername(s.getUsername());
        String normalizedEmail = normalizeEmail(s.getEmail());
        s.setUsername(normalizedUsername);
        s.setEmail(normalizedEmail);

        try (Connection con = openConnection()) {
            boolean hasEmailColumn = hasEmailColumn(con);
            String sql = hasEmailColumn
                    ? "INSERT INTO Staff(StaffName, Username, Email, Password) VALUES(?,?,?,?)"
                    : "INSERT INTO Staff(StaffName, Username, Password) VALUES(?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, s.getStaffName());
                ps.setString(2, normalizedUsername);
                if (hasEmailColumn) {
                    ps.setString(3, normalizedEmail.isEmpty() ? null : normalizedEmail);
                    ps.setString(4, s.getPassword());
                } else {
                    ps.setString(3, s.getPassword());
                }

                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            s.setStaffID(keys.getInt(1));
                        }
                    }
                }
                return affected;
            }
        }
    }

    public int update(Staff s) throws SQLException {
        String normalizedUsername = normalizeUsername(s.getUsername());
        String normalizedEmail = normalizeEmail(s.getEmail());
        s.setUsername(normalizedUsername);
        s.setEmail(normalizedEmail);

        try (Connection con = openConnection()) {
            boolean hasEmailColumn = hasEmailColumn(con);
            String sql = hasEmailColumn
                    ? "UPDATE Staff SET StaffName=?, Username=?, Email=?, Password=? WHERE StaffID=?"
                    : "UPDATE Staff SET StaffName=?, Username=?, Password=? WHERE StaffID=?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, s.getStaffName());
                ps.setString(2, normalizedUsername);
                if (hasEmailColumn) {
                    ps.setString(3, normalizedEmail.isEmpty() ? null : normalizedEmail);
                    ps.setString(4, s.getPassword());
                    ps.setInt(5, s.getStaffID());
                } else {
                    ps.setString(3, s.getPassword());
                    ps.setInt(4, s.getStaffID());
                }
                return ps.executeUpdate();
            }
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Staff WHERE StaffID = ?";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        String normalizedUsername = normalizeUsername(username);
        if (normalizedUsername.isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM Staff WHERE Username = ?";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizedUsername);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public boolean existsByEmail(String email) throws SQLException {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return false;
        }

        try (Connection con = openConnection()) {
            if (!hasEmailColumn(con)) {
                return false;
            }

            String sql = "SELECT COUNT(*) FROM Staff WHERE Email = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, normalizedEmail);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }
            }
        }
    }

    public int updatePasswordByUsername(String username, String newPassword) throws SQLException {
        String normalizedUsername = normalizeUsername(username);
        if (normalizedUsername.isEmpty()) {
            return 0;
        }

        String sql = "UPDATE Staff SET Password = ? WHERE Username = ?";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, normalizedUsername);
            return ps.executeUpdate();
        }
    }

    private Connection openConnection() throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        return con;
    }

    private String buildStaffSelect(boolean hasEmailColumn) {
        if (hasEmailColumn) {
            return "SELECT StaffID, StaffName, Username, Email, Password FROM Staff";
        }
        return "SELECT StaffID, StaffName, Username, CAST(NULL AS NVARCHAR(100)) AS Email, Password FROM Staff";
    }

    private boolean hasEmailColumn(Connection con) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Staff' AND COLUMN_NAME = 'Email'";
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
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

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeDisplayName(String displayName, String fallback) {
        String value = displayName == null ? "" : displayName.trim();
        if (value.isEmpty()) {
            value = fallback;
        }
        return value.length() > 100 ? value.substring(0, 100) : value;
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        return new Staff(rs.getInt("StaffID"), rs.getString("StaffName"),
                rs.getString("Username"), rs.getString("Email"), rs.getString("Password"));
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

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
