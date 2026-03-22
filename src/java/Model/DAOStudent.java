package Model;

import Entities.Staff;
import Entities.Student;
import ViewModel.StudentProfileSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
            return getById(con, detectProfileSupport(con), id);
        }
    }

    public Student getByEmail(String email) throws SQLException {
        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail.isEmpty()) {
            return null;
        }

        try (Connection con = openConnection()) {
            return getByEmail(con, detectProfileSupport(con), normalizedEmail);
        }
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

    public Student ensureMirrorFromStaff(Staff staff) throws SQLException {
        if (staff == null || staff.getStaffID() <= 0) {
            return null;
        }

        try (Connection con = openConnection()) {
            StudentProfileSupport support = detectProfileSupport(con);
            Student existing = getById(con, support, staff.getStaffID());
            if (existing != null) {
                syncMirrorFromStaff(con, support, existing, staff);
                return getById(con, support, staff.getStaffID());
            }

            insertMirrorFromStaff(con, support, staff);
            Student created = getById(con, support, staff.getStaffID());
            if (created != null) {
                return created;
            }
        }

        throw new SQLException("Khong the tao ho so Student cho tai khoan #" + staff.getStaffID() + ".");
    }

    private Connection openConnection() throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        return con;
    }

    private Student getById(Connection con, StudentProfileSupport support, int id) throws SQLException {
        String sql = buildStudentSelect(support) + " WHERE StudentID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    private Student getByEmail(Connection con, StudentProfileSupport support, String email) throws SQLException {
        String sql = buildStudentSelect(support) + " WHERE LOWER(LTRIM(RTRIM(Email))) = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, normalizeEmail(email));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }
        return null;
    }

    private StudentProfileSupport detectProfileSupport(Connection con) throws SQLException {
        return new StudentProfileSupport(
                hasColumn(con, "AvatarUrl"),
                hasColumn(con, "ClassName"),
                hasColumn(con, "FacultyName"),
                hasColumn(con, "AccountStatus"),
                hasColumn(con, "CreatedAt"));
    }

    private void insertMirrorFromStaff(Connection con, StudentProfileSupport support, Staff staff)
            throws SQLException {
        String sql = buildMirrorInsertSql(support);
        boolean identityInsertEnabled = false;

        try (Statement statement = con.createStatement()) {
            statement.execute("SET IDENTITY_INSERT dbo.Student ON");
            identityInsertEnabled = true;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            int index = 1;
            ps.setInt(index++, staff.getStaffID());
            ps.setString(index++, buildMirrorName(staff));

            String mirrorEmail = resolveMirrorEmail(con, support, staff);
            if (mirrorEmail.isEmpty()) {
                ps.setNull(index++, Types.NVARCHAR);
            } else {
                ps.setString(index++, mirrorEmail);
            }

            ps.setNull(index++, Types.NVARCHAR);

            if (support.isAvatarUrlSupported()) {
                ps.setNull(index++, Types.NVARCHAR);
            }
            if (support.isClassNameSupported()) {
                ps.setNull(index++, Types.NVARCHAR);
            }
            if (support.isFacultyNameSupported()) {
                ps.setNull(index++, Types.NVARCHAR);
            }
            if (support.isAccountStatusSupported()) {
                ps.setString(index++, "Active");
            }
            if (support.isCreatedAtSupported()) {
                ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
            }

            ps.executeUpdate();
        } finally {
            if (identityInsertEnabled) {
                try (Statement statement = con.createStatement()) {
                    statement.execute("SET IDENTITY_INSERT dbo.Student OFF");
                }
            }
        }
    }

    private void syncMirrorFromStaff(Connection con, StudentProfileSupport support, Student existingStudent, Staff staff)
            throws SQLException {
        if (existingStudent == null || staff == null) {
            return;
        }

        String currentName = trim(existingStudent.getStudentName());
        String nextName = currentName.isEmpty() ? buildMirrorName(staff) : currentName;

        String currentEmail = normalizeEmail(existingStudent.getEmail());
        String nextEmail = currentEmail;
        if (currentEmail.isEmpty() || isPlaceholderEmail(currentEmail)) {
            nextEmail = resolveMirrorEmail(con, support, staff);
        }

        boolean shouldUpdateName = !nextName.equals(existingStudent.getStudentName());
        boolean shouldUpdateEmail = !normalizeEmail(nextEmail).equals(currentEmail);
        if (!shouldUpdateName && !shouldUpdateEmail) {
            return;
        }

        String sql = "UPDATE Student SET StudentName=?, Email=? WHERE StudentID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nextName);
            if (nextEmail.isEmpty()) {
                ps.setNull(2, Types.NVARCHAR);
            } else {
                ps.setString(2, nextEmail);
            }
            ps.setInt(3, existingStudent.getStudentID());
            ps.executeUpdate();
        }
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

    private String buildMirrorInsertSql(StudentProfileSupport support) {
        StringBuilder columns = new StringBuilder("StudentID, StudentName, Email, Phone");
        StringBuilder values = new StringBuilder("?, ?, ?, ?");

        if (support.isAvatarUrlSupported()) {
            columns.append(", AvatarUrl");
            values.append(", ?");
        }
        if (support.isClassNameSupported()) {
            columns.append(", ClassName");
            values.append(", ?");
        }
        if (support.isFacultyNameSupported()) {
            columns.append(", FacultyName");
            values.append(", ?");
        }
        if (support.isAccountStatusSupported()) {
            columns.append(", AccountStatus");
            values.append(", ?");
        }
        if (support.isCreatedAtSupported()) {
            columns.append(", CreatedAt");
            values.append(", ?");
        }

        return "INSERT INTO Student(" + columns + ") VALUES(" + values + ")";
    }

    private String resolveMirrorEmail(Connection con, StudentProfileSupport support, Staff staff) throws SQLException {
        String normalizedEmail = normalizeEmail(staff.getEmail());
        if (!normalizedEmail.isEmpty()) {
            Student existingByEmail = getByEmail(con, support, normalizedEmail);
            if (existingByEmail == null || existingByEmail.getStudentID() == staff.getStaffID()) {
                return normalizedEmail;
            }
        }
        return buildPlaceholderEmail(staff);
    }

    private String buildMirrorName(Staff staff) {
        String staffName = trim(staff.getStaffName());
        if (!staffName.isEmpty()) {
            return staffName;
        }

        String username = trim(staff.getUsername());
        if (!username.isEmpty()) {
            return username;
        }

        return "Student #" + staff.getStaffID();
    }

    private String buildPlaceholderEmail(Staff staff) {
        String username = trim(staff.getUsername())
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9._-]", "");
        if (username.isEmpty()) {
            username = "student" + staff.getStaffID();
        }
        return username + "." + staff.getStaffID() + "@student.local";
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private boolean isPlaceholderEmail(String email) {
        return normalizeEmail(email).endsWith("@student.local");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
