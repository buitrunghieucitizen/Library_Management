package DAL;

import entities.StaffRole;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOStaffRole {

    public List<StaffRole> getByStaffId(int staffId) throws SQLException {
        String sql = "SELECT StaffID, RoleID FROM StaffRole WHERE StaffID = ?";
        List<StaffRole> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StaffRole(rs.getInt("StaffID"), rs.getInt("RoleID")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public List<StaffRole> getByRoleId(int roleId) throws SQLException {
        String sql = "SELECT StaffID, RoleID FROM StaffRole WHERE RoleID = ?";
        List<StaffRole> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new StaffRole(rs.getInt("StaffID"), rs.getInt("RoleID")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(StaffRole sr) throws SQLException {
        String sql = "INSERT INTO StaffRole(StaffID, RoleID) VALUES(?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sr.getStaffID());
            ps.setInt(2, sr.getRoleID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int staffId, int roleId) throws SQLException {
        String sql = "DELETE FROM StaffRole WHERE StaffID = ? AND RoleID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, roleId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }
}
