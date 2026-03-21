package Model;

import Entities.Student;
import ViewModel.StudentProfileSupport;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOStudent {

    public List<Student> getAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection con = openConnection()) {
            String sql = buildStudentSelect(detectProfileSupport(con)) + " ORDER BY StudentID DESC";
            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        }
        return list;
    }

    public Student getById(int id) throws SQLException {
        try (Connection con = openConnection()) {
            String sql = buildStudentSelect(detectProfileSupport(con)) + " WHERE StudentID = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapStudent(rs);
                    }
                }
            }
        }
        return null;
    }

    public StudentProfileSupport getProfileSupport() throws SQLException {
        try (Connection con = openConnection()) {
            return detectProfileSupport(con);
        }
    }

    public int insert(Student s) throws SQLException {
        String sql = "INSERT INTO Student(StudentName, Email, Phone) VALUES(?,?,?)";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        s.setStudentID(keys.getInt(1));
                    }
                }
            }
            return affected;
        }
    }

    public int update(Student s) throws SQLException {
        String sql = "UPDATE Student SET StudentName=?, Email=?, Phone=? WHERE StudentID=?";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getStudentName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPhone());
            ps.setInt(4, s.getStudentID());
            return ps.executeUpdate();
        }
    }

    public int updateProfileDetails(Student s) throws SQLException {
        try (Connection con = openConnection()) {
            StudentProfileSupport support = detectProfileSupport(con);
            StringBuilder sql = new StringBuilder(
                    "UPDATE Student SET StudentName=?, Email=?, Phone=?");
            List<Object> params = new ArrayList<>();
            params.add(s.getStudentName());
            params.add(s.getEmail());
            params.add(s.getPhone());

            if (support.isAvatarUrlSupported()) {
                sql.append(", AvatarUrl=?");
                params.add(emptyToNull(s.getAvatarUrl()));
            }
            if (support.isClassNameSupported()) {
                sql.append(", ClassName=?");
                params.add(emptyToNull(s.getClassName()));
            }
            if (support.isFacultyNameSupported()) {
                sql.append(", FacultyName=?");
                params.add(emptyToNull(s.getFacultyName()));
            }

            sql.append(" WHERE StudentID=?");
            params.add(s.getStudentID());

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                return ps.executeUpdate();
            }
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Student WHERE StudentID = ?";
        try (Connection con = openConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
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

    private StudentProfileSupport detectProfileSupport(Connection con) throws SQLException {
        return new StudentProfileSupport(
                hasColumn(con, "AvatarUrl"),
                hasColumn(con, "ClassName"),
                hasColumn(con, "FacultyName"),
                hasColumn(con, "AccountStatus"),
                hasColumn(con, "CreatedAt"));
    }

    private boolean hasColumn(Connection con, String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Student' AND COLUMN_NAME = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private String buildStudentSelect(StudentProfileSupport support) {
        return "SELECT StudentID, StudentName, Email, Phone"
                + (support.isAvatarUrlSupported()
                        ? ", AvatarUrl"
                        : ", CAST(NULL AS NVARCHAR(500)) AS AvatarUrl")
                + (support.isClassNameSupported()
                        ? ", ClassName"
                        : ", CAST(NULL AS NVARCHAR(100)) AS ClassName")
                + (support.isFacultyNameSupported()
                        ? ", FacultyName"
                        : ", CAST(NULL AS NVARCHAR(100)) AS FacultyName")
                + (support.isAccountStatusSupported()
                        ? ", AccountStatus"
                        : ", CAST(NULL AS NVARCHAR(30)) AS AccountStatus")
                + (support.isCreatedAtSupported()
                        ? ", CONVERT(varchar(19), CreatedAt, 120) AS CreatedAt"
                        : ", CAST(NULL AS NVARCHAR(19)) AS CreatedAt")
                + " FROM Student";
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        return new Student(
                rs.getInt("StudentID"),
                rs.getString("StudentName"),
                rs.getString("Email"),
                rs.getString("Phone"),
                rs.getString("AvatarUrl"),
                rs.getString("ClassName"),
                rs.getString("FacultyName"),
                rs.getString("AccountStatus"),
                rs.getString("CreatedAt"));
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
