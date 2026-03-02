package DAL;

import entities.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOStudent {

    public List<Student> getAll() throws SQLException {
        String sql = "SELECT StudentID, StudentName, Email, Phone FROM Student ORDER BY StudentID DESC";
        List<Student> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(rs.getInt("StudentID"), rs.getString("StudentName"),
                        rs.getString("Email"), rs.getString("Phone")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Student getById(int id) throws SQLException {
        String sql = "SELECT StudentID, StudentName, Email, Phone FROM Student WHERE StudentID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Student(rs.getInt("StudentID"), rs.getString("StudentName"),
                            rs.getString("Email"), rs.getString("Phone"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Student s) throws SQLException {
        String sql = "INSERT INTO Student(StudentName, Email, Phone) VALUES(?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        s.setStudentID(keys.getInt(1));
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Student s) throws SQLException {
        String sql = "UPDATE Student SET StudentName=?, Email=?, Phone=? WHERE StudentID=?";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getStudentID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Student WHERE StudentID = ?";
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
