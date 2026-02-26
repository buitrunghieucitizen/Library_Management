package Model;

import Entities.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAORole {

    public List<Role> getAll() throws SQLException {
        String sql = "SELECT RoleID, RoleName FROM Role ORDER BY RoleID DESC";
        List<Role> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Role(rs.getInt("RoleID"), rs.getString("RoleName")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Role getById(int id) throws SQLException {
        String sql = "SELECT RoleID, RoleName FROM Role WHERE RoleID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Role(rs.getInt("RoleID"), rs.getString("RoleName"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Role r) throws SQLException {
        String sql = "INSERT INTO Role(RoleName) VALUES(?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getRoleName());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        r.setRoleID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Role r) throws SQLException {
        String sql = "UPDATE Role SET RoleName=? WHERE RoleID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getRoleName());
            ps.setInt(2, r.getRoleID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Role WHERE RoleID = ?";
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
