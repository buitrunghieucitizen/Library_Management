package DAL;

import entities.*;

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
            if (b.getDueDate() != null)
                ps.setString(4, b.getDueDate());
            else
                ps.setNull(4, java.sql.Types.DATE);
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

    public List<Borrow> getActiveByStaffId(int staffId) throws SQLException {
        String sql = "SELECT BorrowID, StudentID, StaffID, BorrowDate, DueDate, Status, ReturnDate " +
                "FROM Borrow WHERE StaffID = ? AND Status IN ('Borrowing', 'Overdue') " +
                "ORDER BY DueDate ASC";
        List<Borrow> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Borrow(
                            rs.getInt("BorrowID"),
                            rs.getInt("StudentID"),
                            rs.getInt("StaffID"),
                            rs.getString("BorrowDate"),
                            rs.getString("DueDate"),
                            rs.getString("Status"),
                            rs.getString("ReturnDate")
                    ));
                }
            }
        } finally {
            con.close();
        }
        return list;
    }

    // Check có đang mượn/overdue chưa trả không
    public boolean hasActiveBorrow(int staffId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Borrow WHERE StudentID = ? AND Status IN ('Borrowing', 'Overdue')";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } finally {
            con.close();
        }
        return false;
    }

    // Check có pending request không (nếu sau này thêm bảng BorrowRequest)
// Hiện tại dùng Status = 'Pending' trong Borrow
    public boolean hasPendingBorrow(int staffId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Borrow WHERE StudentID = ? AND Status = 'Pending'";
        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } finally {
            con.close();
        }
        return false;
    }

    // Tạo borrow + borrow items trong 1 transaction
    public int createBorrow(Borrow borrow, List<BorrowItem> items) throws SQLException {
        String sqlBorrow = "INSERT INTO Borrow(StudentID, StaffID, BorrowDate, DueDate, Status) VALUES(?,?,?,?,?)";
        String sqlItem = "INSERT INTO BorrowItem(BorrowID, BookID, Quantity) VALUES(?,?,?)";
        String sqlUpdate = "UPDATE Book SET Available = Available - 1 WHERE BookID = ? AND Available > 0";

        Connection con = DBConnection.getConnection();
        if (con == null) throw new SQLException("Cannot connect to database!");
        con.setAutoCommit(false);
        try {
            int borrowId;
            try (PreparedStatement ps = con.prepareStatement(sqlBorrow, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, borrow.getStudentID());
                ps.setInt(2, borrow.getStaffID());
                ps.setString(3, borrow.getBorrowDate());
                ps.setString(4, borrow.getDueDate());
                ps.setString(5, borrow.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Cannot get BorrowID");
                    borrowId = keys.getInt(1);
                }
            }
            for (BorrowItem item : items) {
                try (PreparedStatement ps = con.prepareStatement(sqlItem)) {
                    ps.setInt(1, borrowId);
                    ps.setInt(2, item.getBookID());
                    ps.setInt(3, item.getQuantity());
                    ps.executeUpdate();
                }
                // Giảm Available
                try (PreparedStatement ps = con.prepareStatement(sqlUpdate)) {
                    ps.setInt(1, item.getBookID());
                    int affected = ps.executeUpdate();
                    if (affected == 0) throw new SQLException("Book " + item.getBookID() + " hết sách!");
                }
            }
            con.commit();
            return borrowId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
}
