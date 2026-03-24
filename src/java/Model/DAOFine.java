package Model;

import Entities.Fine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOFine {

    /**
     * Check if student has any unpaid fines.
     */
    public boolean hasUnpaidFine(int studentId) throws SQLException {
        if (!checkTableExists()) {
            return false;
        }
        String sql = "SELECT TOP 1 1 FROM Fine f "
                + "JOIN Borrow b ON f.BorrowID = b.BorrowID "
                + "WHERE b.StudentID = ? AND f.Status = 'Unpaid'";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            con.close();
        }
    }

    /**
     * Get total unpaid fine amount for a student.
     */
    public double getTotalUnpaid(int studentId) throws SQLException {
        if (!checkTableExists()) {
            return 0;
        }
        String sql = "SELECT ISNULL(SUM(f.Amount), 0) AS Total FROM Fine f "
                + "JOIN Borrow b ON f.BorrowID = b.BorrowID "
                + "WHERE b.StudentID = ? AND f.Status = 'Unpaid'";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Total");
                }
            }
        } finally {
            con.close();
        }
        return 0;
    }

    /**
     * Get all unpaid fines for a student.
     */
    public List<Fine> getUnpaidByStudent(int studentId) throws SQLException {
        if (!checkTableExists()) {
            return new ArrayList<>();
        }
        String sql = "SELECT f.FineID, f.BorrowID, f.Amount, f.Reason, "
                + "CONVERT(varchar(10), f.CreatedDate, 23) AS CreatedDate, "
                + "CONVERT(varchar(10), f.PaidDate, 23) AS PaidDate, f.Status "
                + "FROM Fine f JOIN Borrow b ON f.BorrowID = b.BorrowID "
                + "WHERE b.StudentID = ? AND f.Status = 'Unpaid' "
                + "ORDER BY f.CreatedDate DESC";
        List<Fine> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Fine(
                            rs.getInt("FineID"), rs.getInt("BorrowID"),
                            rs.getDouble("Amount"), rs.getString("Reason"),
                            rs.getString("CreatedDate"), rs.getString("PaidDate"),
                            rs.getString("Status")));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    /**
     * Get all paid fines for dashboard revenue reporting.
     */
    public List<Fine> getPaidFines() throws SQLException {
        if (!checkTableExists()) {
            return new ArrayList<>();
        }

        String sql = "SELECT f.FineID, f.BorrowID, f.Amount, f.Reason, "
                + "CONVERT(varchar(10), f.CreatedDate, 23) AS CreatedDate, "
                + "CONVERT(varchar(10), f.PaidDate, 23) AS PaidDate, f.Status "
                + "FROM Fine f "
                + "WHERE f.Status = 'Paid' AND f.PaidDate IS NOT NULL "
                + "ORDER BY f.PaidDate DESC, f.FineID DESC";
        List<Fine> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Fine(
                        rs.getInt("FineID"),
                        rs.getInt("BorrowID"),
                        rs.getDouble("Amount"),
                        rs.getString("Reason"),
                        rs.getString("CreatedDate"),
                        rs.getString("PaidDate"),
                        rs.getString("Status")));
            }
        } finally {
            con.close();
        }

        return list;
    }

    /**
     * Insert a new fine (used within a transaction).
     */
    public int insert(Connection con, int borrowId, double amount, String reason) throws SQLException {
        if (!checkTableExists(con)) {
            throw new SQLException("Bang Fine chua ton tai. Hay chay migration trong sql.sql truoc.");
        }
        String sql = "INSERT INTO Fine(BorrowID, Amount, Reason) VALUES(?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, borrowId);
            ps.setDouble(2, amount);
            ps.setString(3, reason);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong tao duoc Fine.");
    }

    /**
     * Mark a fine as paid.
     */
    public int markPaid(int fineId) throws SQLException {
        if (!checkTableExists()) {
            return 0;
        }
        String sql = "UPDATE Fine SET Status = 'Paid', PaidDate = CAST(GETDATE() AS DATE) WHERE FineID = ?";
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            return ps.executeUpdate();
        } finally {
            con.close();
        }
    }

    private boolean checkTableExists() throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            return false;
        }
        try {
            return checkTableExists(con);
        } finally {
            con.close();
        }
    }

    private boolean checkTableExists(Connection con) throws SQLException {
        String sql = "SELECT 1 FROM INFORMATION_SCHEMA.TABLES "
                + "WHERE TABLE_SCHEMA = 'dbo' AND TABLE_NAME = 'Fine'";
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }
}
