package Model;

import Entities.Borrow;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DAOBorrow {

    public List<Borrow> getAll() throws SQLException {
        String sql = "SELECT BorrowID, StudentID, StaffID, BorrowDate, DueDate, Status, ReturnDate FROM Borrow ORDER BY BorrowID DESC";
        List<Borrow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Borrow(
                        rs.getInt("BorrowID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                        rs.getString("BorrowDate"), rs.getString("DueDate"),
                        rs.getString("Status"), rs.getString("ReturnDate")));
            }
        } finally {
            con.close();
        }
        return list;
    }

    public Borrow getById(int id) throws SQLException {
        String sql = "SELECT BorrowID, StudentID, StaffID, BorrowDate, DueDate, Status, ReturnDate FROM Borrow WHERE BorrowID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Borrow(
                            rs.getInt("BorrowID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                            rs.getString("BorrowDate"), rs.getString("DueDate"),
                            rs.getString("Status"), rs.getString("ReturnDate"));
                }
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Borrow b) throws SQLException {
        String sql = "INSERT INTO Borrow(StudentID, StaffID, BorrowDate, DueDate, Status) VALUES(?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getStudentID());
            ps.setInt(2, b.getStaffID());
            ps.setString(3, b.getBorrowDate());
            ps.setString(4, b.getDueDate());
            ps.setString(5, b.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        b.setBorrowID(keys.getInt(1));
                    }
                }
            }
            return affected;
        } finally {
            con.close();
        }
    }

    public int update(Borrow b) throws SQLException {
        String sql = "UPDATE Borrow SET StudentID=?, StaffID=?, BorrowDate=?, DueDate=?, Status=?, ReturnDate=? WHERE BorrowID=?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getStudentID());
            ps.setInt(2, b.getStaffID());
            ps.setString(3, b.getBorrowDate());
            ps.setString(4, b.getDueDate());
            ps.setString(5, b.getStatus());
            ps.setString(6, b.getReturnDate());
            ps.setInt(7, b.getBorrowID());
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public int delete(int id) throws SQLException {
        String sql = "DELETE FROM Borrow WHERE BorrowID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    public List<Borrow> getActiveByStudentId(int studentId) throws SQLException {
        String sql = "SELECT BorrowID, StudentID, StaffID, BorrowDate, DueDate, Status, ReturnDate "
                + "FROM Borrow "
                + "WHERE StudentID = ? AND Status IN ('Borrowing', 'Overdue') "
                + "ORDER BY DueDate ASC, BorrowID DESC";
        List<Borrow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Borrow(
                            rs.getInt("BorrowID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                            rs.getString("BorrowDate"), rs.getString("DueDate"),
                            rs.getString("Status"), rs.getString("ReturnDate")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    public int insert(Connection con, int studentId, int staffId, LocalDate borrowDate, LocalDate dueDate, String status)
            throws SQLException {
        String sql = "INSERT INTO Borrow(StudentID, StaffID, BorrowDate, DueDate, Status) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            ps.setDate(3, Date.valueOf(borrowDate));
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setString(5, status);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the tao phieu muon.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong lay duoc BorrowID moi.");
    }

    public String getStatusForUpdate(Connection con, int borrowId) throws SQLException {
        String sql = "SELECT Status FROM Borrow WITH (UPDLOCK, ROWLOCK) WHERE BorrowID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        }
        return null;
    }

    public int updateReturned(Connection con, int borrowId) throws SQLException {
        String sql = "UPDATE Borrow SET Status = ?, ReturnDate = CAST(GETDATE() AS DATE) WHERE BorrowID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "Returned");
            ps.setInt(2, borrowId);
            return ps.executeUpdate();
        }
    }

    public boolean existsOwnedByStudentAndNotReturned(int borrowId, int studentId) throws SQLException {
        String sql = "SELECT 1 FROM Borrow WHERE BorrowID = ? AND StudentID = ? AND Status <> 'Returned'";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            ps.setInt(2, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            con.close();
        }
    }

    public List<BorrowRow> getBorrowRows() throws SQLException {
        return getBorrowRowsByStudent(null);
    }

    public List<BorrowRow> getBorrowRowsByStudent(Integer studentId) throws SQLException {
        String sql = "SELECT b.BorrowID, s.StudentName, st.StaffName, "
                + "CONVERT(varchar(10), b.BorrowDate, 23) AS BorrowDate, "
                + "CONVERT(varchar(10), b.DueDate, 23) AS DueDate, "
                + "b.Status, "
                + "CONVERT(varchar(10), b.ReturnDate, 23) AS ReturnDate, "
                + "ISNULL(STRING_AGG(CASE WHEN bi.BookID IS NULL THEN NULL ELSE CONCAT(bo.BookName, ' (x', bi.Quantity, ')') END, ', '), '') AS Items "
                + "FROM Borrow b "
                + "JOIN Student s ON s.StudentID = b.StudentID "
                + "JOIN Staff st ON st.StaffID = b.StaffID "
                + "LEFT JOIN BorrowItem bi ON bi.BorrowID = b.BorrowID "
                + "LEFT JOIN Book bo ON bo.BookID = bi.BookID "
                + (studentId != null ? "WHERE b.StudentID = ? " : "")
                + "GROUP BY b.BorrowID, s.StudentName, st.StaffName, b.BorrowDate, b.DueDate, b.Status, b.ReturnDate "
                + "ORDER BY b.BorrowID DESC";

        List<BorrowRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (studentId != null) {
                ps.setInt(1, studentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new BorrowRow(
                            rs.getInt("BorrowID"),
                            rs.getString("StudentName"),
                            rs.getString("StaffName"),
                            rs.getString("BorrowDate"),
                            rs.getString("DueDate"),
                            rs.getString("Status"),
                            rs.getString("ReturnDate"),
                            rs.getString("Items")));
                }
            }
        } finally {
            con.close();
        }
        return rows;
    }

    public static class BorrowRow {
        private final int borrowID;
        private final String studentName;
        private final String staffName;
        private final String borrowDate;
        private final String dueDate;
        private final String status;
        private final String returnDate;
        private final String items;

        public BorrowRow(int borrowID, String studentName, String staffName, String borrowDate, String dueDate,
                String status, String returnDate, String items) {
            this.borrowID = borrowID;
            this.studentName = studentName;
            this.staffName = staffName;
            this.borrowDate = borrowDate;
            this.dueDate = dueDate;
            this.status = status;
            this.returnDate = returnDate;
            this.items = items;
        }

        public int getBorrowID() {
            return borrowID;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getStaffName() {
            return staffName;
        }

        public String getBorrowDate() {
            return borrowDate;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getStatus() {
            return status;
        }

        public String getReturnDate() {
            return returnDate;
        }

        public String getItems() {
            return items;
        }
    }
}
