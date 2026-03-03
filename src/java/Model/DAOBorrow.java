package Model;

import Entities.Borrow;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAOBorrow {

    public List<Borrow> getAll() throws SQLException {
        String sql = "SELECT BorrowID, StudentID, StaffID, BorrowDate, DueDate, Status, ReturnDate FROM Borrow ORDER BY BorrowID DESC";
        List<Borrow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return new Borrow(
                            rs.getInt("BorrowID"), rs.getInt("StudentID"), rs.getInt("StaffID"),
                            rs.getString("BorrowDate"), rs.getString("DueDate"),
                            rs.getString("Status"), rs.getString("ReturnDate"));
            }
        } finally {
            con.close();
        }
        return null;
    }

    public int insert(Borrow b) throws SQLException {
        String sql = "INSERT INTO Borrow(StudentID, StaffID, BorrowDate, DueDate, Status) VALUES(?,?,?,?,?)";
        Connection con = DBConnection.getConnection();
        if (con == null)
            throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getStudentID());
            ps.setInt(2, b.getStaffID());
            ps.setString(3, b.getBorrowDate());
            ps.setString(4, b.getDueDate());
            ps.setString(5, b.getStatus());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next())
                        b.setBorrowID(keys.getInt(1));
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
        if (con == null)
            throw new SQLException("Cannot connect to database!");
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
}
