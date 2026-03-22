package Model;

import Entities.BookHold;
import ViewModel.HoldRow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBookHold {

    /**
     * Check if student already has an active hold (Waiting/Notified) on this
     * book.
     */
    public boolean hasActiveHold(int studentId, int bookId) throws SQLException {
        String sql = "SELECT TOP 1 1 FROM BookHold "
                + "WHERE StudentID = ? AND BookID = ? AND Status IN ('Waiting','Notified')";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            con.close();
        }
    }

    /**
     * Create a new hold request.
     */
    public int insert(int studentId, int bookId) throws SQLException {
        String sql = "INSERT INTO BookHold(StudentID, BookID) VALUES(?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, bookId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } finally {
            con.close();
        }
        throw new SQLException("Không tạo được hold request.");
    }

    /**
     * Get all active holds for a specific book, ordered by hold date (FIFO
     * queue).
     */
    public List<BookHold> getActiveByBookId(int bookId) throws SQLException {
        String sql = "SELECT HoldID, StudentID, BookID, "
                + "CONVERT(varchar(19), HoldDate, 120) AS HoldDate, Status, "
                + "CONVERT(varchar(19), NotifiedDate, 120) AS NotifiedDate, "
                + "CONVERT(varchar(19), ExpireDate, 120) AS ExpireDate, Note "
                + "FROM BookHold WHERE BookID = ? AND Status IN ('Waiting','Notified') "
                + "ORDER BY HoldDate ASC";
        List<BookHold> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    /**
     * Get all active holds for a student.
     */
    public List<HoldRow> getActiveByStudentId(int studentId) throws SQLException {
        String sql = "SELECT h.HoldID, h.StudentID, s.StaffName AS StudentName, s.Email AS StudentEmail, "
                + "h.BookID, b.BookName, "
                + "CONVERT(varchar(19), h.HoldDate, 120) AS HoldDate, h.Status, "
                + "CONVERT(varchar(19), h.NotifiedDate, 120) AS NotifiedDate, "
                + "CONVERT(varchar(19), h.ExpireDate, 120) AS ExpireDate, b.Available "
                + "FROM BookHold h "
                + "JOIN Staff s ON s.StaffID = h.StudentID "
                + "JOIN Book b ON b.BookID = h.BookID "
                + "WHERE h.StudentID = ? AND h.Status IN ('Waiting','Notified') "
                + "ORDER BY h.HoldDate ASC";
        List<HoldRow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapHoldRow(rs));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    /**
     * Get the NEXT waiting hold for a book (first in queue). Returns null if no
     * one is waiting.
     */
    public BookHold getNextWaiting(Connection con, int bookId) throws SQLException {
        String sql = "SELECT TOP 1 HoldID, StudentID, BookID, "
                + "CONVERT(varchar(19), HoldDate, 120) AS HoldDate, Status, "
                + "CONVERT(varchar(19), NotifiedDate, 120) AS NotifiedDate, "
                + "CONVERT(varchar(19), ExpireDate, 120) AS ExpireDate, Note "
                + "FROM BookHold WHERE BookID = ? AND Status = 'Waiting' "
                + "ORDER BY HoldDate ASC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Mark hold as Notified + set expire date (24h from now).
     */
    public int markNotified(Connection con, int holdId) throws SQLException {
        String sql = "UPDATE BookHold SET Status = 'Notified', "
                + "NotifiedDate = SYSUTCDATETIME(), "
                + "ExpireDate = DATEADD(HOUR, 24, SYSUTCDATETIME()) "
                + "WHERE HoldID = ? AND Status = 'Waiting'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, holdId);
            return ps.executeUpdate();
        }
    }

    /**
     * Mark hold as Fulfilled (student actually borrowed the book).
     */
    public int markFulfilled(int holdId) throws SQLException {
        String sql = "UPDATE BookHold SET Status = 'Fulfilled' WHERE HoldID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, holdId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    /**
     * Cancel a hold (by student or admin).
     */
    public int cancel(int holdId, int studentId) throws SQLException {
        String sql = "UPDATE BookHold SET Status = 'Cancelled' "
                + "WHERE HoldID = ? AND StudentID = ? AND Status IN ('Waiting','Notified')";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, holdId);
            ps.setInt(2, studentId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    /**
     * Count waiting holds for a specific book (queue size).
     */
    public int countWaiting(int bookId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM BookHold WHERE BookID = ? AND Status = 'Waiting'";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            con.close();
        }
        return 0;
    }

    /**
     * Expire holds that passed their ExpireDate (batch job).
     */
    public int expireOldHolds() throws SQLException {
        String sql = "UPDATE BookHold SET Status = 'Expired' "
                + "WHERE Status = 'Notified' AND ExpireDate < SYSUTCDATETIME()";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    /**
     * Get all holds for admin view.
     */
    public List<HoldRow> getAllActive() throws SQLException {
        String sql = "SELECT h.HoldID, h.StudentID, s.StaffName AS StudentName, s.Email AS StudentEmail, "
                + "h.BookID, b.BookName, "
                + "CONVERT(varchar(19), h.HoldDate, 120) AS HoldDate, h.Status, "
                + "CONVERT(varchar(19), h.NotifiedDate, 120) AS NotifiedDate, "
                + "CONVERT(varchar(19), h.ExpireDate, 120) AS ExpireDate, b.Available "
                + "FROM BookHold h "
                + "JOIN Staff s ON s.StaffID = h.StudentID "
                + "JOIN Book b ON b.BookID = h.BookID "
                + "WHERE h.Status IN ('Waiting','Notified') "
                + "ORDER BY h.BookID, h.HoldDate ASC";
        List<HoldRow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapHoldRow(rs));
            }
        } finally {
            con.close();
        }
        return list;
    }

    private BookHold mapRow(ResultSet rs) throws SQLException {
        return new BookHold(
                rs.getInt("HoldID"), rs.getInt("StudentID"), rs.getInt("BookID"),
                rs.getString("HoldDate"), rs.getString("Status"),
                rs.getString("NotifiedDate"), rs.getString("ExpireDate"),
                rs.getString("Note"));
    }

    private HoldRow mapHoldRow(ResultSet rs) throws SQLException {
        return new HoldRow(
                rs.getInt("HoldID"), rs.getInt("StudentID"),
                rs.getString("StudentName"), rs.getString("StudentEmail"),
                rs.getInt("BookID"), rs.getString("BookName"),
                rs.getString("HoldDate"), rs.getString("Status"),
                rs.getString("NotifiedDate"), rs.getString("ExpireDate"),
                rs.getInt("Available"));
    }

    /**
     * Check nếu sách có hold Notified bởi student KHÁC (không phải studentId
     * này). Dùng để block người khác mượn sách đang reserved cho hold.
     */
    public boolean hasNotifiedHoldByOther(int bookId, int excludeStudentId) throws SQLException {
        if (!checkTableExists()) {
            return false;
        }
        String sql = "SELECT TOP 1 1 FROM BookHold "
                + "WHERE BookID = ? AND Status = 'Notified' AND StudentID != ? "
                + "AND ExpireDate > SYSUTCDATETIME()";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, excludeStudentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            con.close();
        }
    }

    /**
     * Expire old holds + tăng Available cho sách bị reserve. Gọi bởi scheduled
     * task hoặc khi student vào trang.
     */
    public int expireAndReleaseHolds() throws SQLException {
        if (!checkTableExists()) {
            return 0;
        }

        // Tìm holds cần expire
        String findSql = "SELECT HoldID, BookID FROM BookHold "
                + "WHERE Status = 'Notified' AND ExpireDate < SYSUTCDATETIME()";

        List<int[]> toExpire = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return 0;
        }
        try (PreparedStatement ps = con.prepareStatement(findSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                toExpire.add(new int[]{rs.getInt("HoldID"), rs.getInt("BookID")});
            }
        } finally {
            con.close();
        }

        int count = 0;
        for (int[] hold : toExpire) {
            Connection con2 = DBConnection.getConnection();
            if (con2 == null) {
                continue;
            }
            try {
                con2.setAutoCommit(false);

                // Expire hold
                String expireSql = "UPDATE BookHold SET Status = 'Expired' WHERE HoldID = ? AND Status = 'Notified'";
                try (PreparedStatement ps = con2.prepareStatement(expireSql)) {
                    ps.setInt(1, hold[0]);
                    if (ps.executeUpdate() > 0) {
                        // Tăng Available lại (unreserve)
                        new DAOBook().increaseAvailable(con2, hold[1], 1);
                        count++;
                    }
                }
                con2.commit();
            } catch (SQLException e) {
                con2.rollback();
            } finally {
                con2.setAutoCommit(true);
                con2.close();
            }
        }
        return count;
    }

    private boolean checkTableExists() throws SQLException {
        String sql = "SELECT 1 FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'BookHold'";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } finally {
            con.close();
        }
    }
}
