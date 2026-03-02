package controller;

import entities.Book;
import entities.BorrowItem;
import entities.Staff;
import entities.Student;
import DAL.DAOBook;
import DAL.DAOStudent;
import DAL.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "BorrowController", urlPatterns = {"/borrows"})
public class BorrowController extends HttpServlet {

    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    showCreate(req, resp);
                    break;
                case "list":
                default:
                    showList(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null) {
            action = "create";
        }

        try {
            switch (action) {
                case "create":
                    createBorrow(req, resp);
                    break;
                case "return":
                    returnBorrow(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/borrows?action=list");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        req.setAttribute("borrows", fetchBorrowRows());
        req.getRequestDispatcher("/WEB-INF/views/borrow/list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        loadCreateData(req);
        req.getRequestDispatcher("/WEB-INF/views/borrow/create.jsp").forward(req, resp);
    }

    private void loadCreateData(HttpServletRequest req) throws SQLException {
        List<Student> students = daoStudent.getAll();
        List<Book> books = daoBook.getAll();
        List<Book> borrowableBooks = new ArrayList<>();
        for (Book b : books) {
            if (b.getAvailable() > 0) {
                borrowableBooks.add(b);
            }
        }
        req.setAttribute("students", students);
        req.setAttribute("books", borrowableBooks);
    }

    private void createBorrow(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        int studentId;
        int bookId;
        int quantity;
        LocalDate dueDate;
        LocalDate borrowDate = LocalDate.now();

        try {
            studentId = parsePositiveInt(req.getParameter("studentID"), "Student");
            bookId = parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = parsePositiveInt(req.getParameter("quantity"), "Quantity");
            dueDate = LocalDate.parse(req.getParameter("dueDate"));
            if (dueDate.isBefore(borrowDate)) {
                throw new IllegalArgumentException("Han tra phai >= ngay muon.");
            }
        } catch (Exception e) {
            forwardCreateError(req, resp, "Du lieu khong hop le: " + e.getMessage());
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            int available = getBookAvailable(con, bookId);
            if (available < quantity) {
                con.rollback();
                forwardCreateError(req, resp, "So luong sach con lai khong du. Con lai: " + available);
                return;
            }

            int borrowId = insertBorrow(con, studentId, staff.getStaffID(), borrowDate, dueDate);
            insertBorrowItem(con, borrowId, bookId, quantity);
            decreaseBookAvailable(con, bookId, quantity);

            con.commit();
            redirectWithMessage(req, resp, "msg", "Tao phieu muon thanh cong.");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void returnBorrow(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int borrowId;
        try {
            borrowId = parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "BorrowID khong hop le.");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            String status = getBorrowStatusForUpdate(con, borrowId);
            if (status == null) {
                con.rollback();
                redirectWithMessage(req, resp, "error", "Khong tim thay phieu muon.");
                return;
            }

            if ("Returned".equalsIgnoreCase(status)) {
                con.rollback();
                redirectWithMessage(req, resp, "msg", "Phieu nay da duoc tra truoc do.");
                return;
            }

            List<BorrowItem> items = getBorrowItems(con, borrowId);
            if (items.isEmpty()) {
                con.rollback();
                redirectWithMessage(req, resp, "error", "Phieu muon khong co sach de tra.");
                return;
            }

            for (BorrowItem item : items) {
                increaseBookAvailable(con, item.getBookID(), item.getQuantity());
            }

            updateBorrowReturned(con, borrowId);
            con.commit();
            redirectWithMessage(req, resp, "msg", "Tra sach thanh cong.");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private Staff getLoggedStaff(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        Object obj = session.getAttribute("staff");
        if (obj instanceof Staff) {
            return (Staff) obj;
        }
        return null;
    }

    private void forwardCreateError(HttpServletRequest req, HttpServletResponse resp, String error) throws SQLException, ServletException, IOException {
        req.setAttribute("error", error);
        req.setAttribute("selectedStudentId", req.getParameter("studentID"));
        req.setAttribute("selectedBookId", req.getParameter("bookID"));
        req.setAttribute("quantity", req.getParameter("quantity"));
        req.setAttribute("dueDate", req.getParameter("dueDate"));
        loadCreateData(req);
        req.getRequestDispatcher("/WEB-INF/views/borrow/create.jsp").forward(req, resp);
    }

    private void redirectWithMessage(HttpServletRequest req, HttpServletResponse resp, String key, String value) throws IOException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/borrows?action=list&" + key + "=" + encoded);
    }

    private int parsePositiveInt(String raw, String fieldName) {
        int value = Integer.parseInt(raw);
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phai > 0");
        }
        return value;
    }

    private int getBookAvailable(Connection con, int bookId) throws SQLException {
        String sql = "SELECT Available FROM Book WHERE BookID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Available");
                }
            }
        }
        throw new SQLException("Khong tim thay sach id=" + bookId);
    }

    private int insertBorrow(Connection con, int studentId, int staffId, LocalDate borrowDate, LocalDate dueDate) throws SQLException {
        String sql = "INSERT INTO Borrow(StudentID, StaffID, BorrowDate, DueDate, Status) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, studentId);
            ps.setInt(2, staffId);
            ps.setDate(3, Date.valueOf(borrowDate));
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setString(5, "Borrowing");
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

    private void insertBorrowItem(Connection con, int borrowId, int bookId, int quantity) throws SQLException {
        String sql = "INSERT INTO BorrowItem(BorrowID, BookID, Quantity) VALUES(?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the tao chi tiet muon.");
            }
        }
    }

    private void decreaseBookAvailable(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book SET Available = Available - ? WHERE BookID = ? AND Available >= ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            ps.setInt(3, quantity);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong du so luong sach de muon.");
            }
        }
    }

    private String getBorrowStatusForUpdate(Connection con, int borrowId) throws SQLException {
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

    private List<BorrowItem> getBorrowItems(Connection con, int borrowId) throws SQLException {
        String sql = "SELECT BorrowID, BookID, Quantity FROM BorrowItem WHERE BorrowID = ?";
        List<BorrowItem> items = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, borrowId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new BorrowItem(
                            rs.getInt("BorrowID"),
                            rs.getInt("BookID"),
                            rs.getInt("Quantity")));
                }
            }
        }
        return items;
    }

    private void increaseBookAvailable(Connection con, int bookId, int quantity) throws SQLException {
        String sql = "UPDATE Book SET Available = Available + ? WHERE BookID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, bookId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong tim thay sach id=" + bookId);
            }
        }
    }

    private void updateBorrowReturned(Connection con, int borrowId) throws SQLException {
        String sql = "UPDATE Borrow SET Status = ?, ReturnDate = CAST(GETDATE() AS DATE) WHERE BorrowID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "Returned");
            ps.setInt(2, borrowId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Cap nhat tra sach that bai.");
            }
        }
    }

    private List<BorrowRow> fetchBorrowRows() throws SQLException {
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
                + "GROUP BY b.BorrowID, s.StudentName, st.StaffName, b.BorrowDate, b.DueDate, b.Status, b.ReturnDate "
                + "ORDER BY b.BorrowID DESC";

        List<BorrowRow> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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

        public BorrowRow(int borrowID, String studentName, String staffName, String borrowDate, String dueDate, String status, String returnDate, String items) {
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
