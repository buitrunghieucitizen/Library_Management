package Controller;

import Entities.Book;
import Model.DAOBook;
import Model.DBConnection;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "BookController", urlPatterns = {"/books", "/admin/books"})
public class BookController extends HttpServlet {

    private static final String PUBLIC_BOOKS_PATH = "/books";
    private static final String ADMIN_BOOKS_PATH = "/admin/books";

    private final DAOBook dao = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
            return;
        }

        if (isAdminSection(req) && !canAccessAdminSection(req)) {
            resp.sendRedirect(req.getContextPath() + PUBLIC_BOOKS_PATH + "?action=list&error=Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                case "edit":
                case "delete":
                    if (!isAdminSection(req) || !isAdmin(req)) {
                        resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&error=Permission Denied");
                        return;
                    }
                    if ("create".equals(action)) {
                        req.getRequestDispatcher("/WEB-INF/views/book/create.jsp").forward(req, resp);
                    } else if ("edit".equals(action)) {
                        showEdit(req, resp);
                    } else {
                        int id = Integer.parseInt(req.getParameter("id"));
                        dao.delete(id);
                        resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
                    }
                    break;

                case "list":
                default:
                    List<Book> list = dao.getAll();
                    req.setAttribute("books", list);
                    req.setAttribute("adminSection", isAdminSection(req));
                    req.getRequestDispatcher("/WEB-INF/views/book/list.jsp").forward(req, resp);
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

        if (!isAdminSection(req) || !isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&error=Permission Denied");
            return;
        }

        try {
            if ("create".equals(action)) {
                createBookWithPrice(req);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&msg=Them%20sach%20va%20gia%20thanh%20cong");
                return;
            }

            if ("edit".equals(action)) {
                updateBookWithPrice(req);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&msg=Cap%20nhat%20sach%20va%20gia%20thanh%20cong");
                return;
            }

            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Book book = dao.getById(id);
        if (book == null) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&error=Khong%20tim%20thay%20sach");
            return;
        }

        req.setAttribute("book", book);
        req.setAttribute("currentPrice", getCurrentPriceInfo(id));
        req.getRequestDispatcher("/WEB-INF/views/book/edit.jsp").forward(req, resp);
    }

    private boolean isAdmin(HttpServletRequest req) {
        return RoleUtils.isAdmin(req);
    }

    private boolean shouldRedirectToAdminRoute(HttpServletRequest req) {
        return PUBLIC_BOOKS_PATH.equals(req.getServletPath()) && !RoleUtils.isStudentOnly(req);
    }

    private boolean canAccessAdminSection(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private boolean isAdminSection(HttpServletRequest req) {
        return ADMIN_BOOKS_PATH.equals(req.getServletPath());
    }

    private String getListPath(HttpServletRequest req) {
        return isAdminSection(req) ? ADMIN_BOOKS_PATH : PUBLIC_BOOKS_PATH;
    }

    private Book readBookFromRequest(HttpServletRequest req, boolean hasId) {
        String name = req.getParameter("bookName");
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        int available = Integer.parseInt(req.getParameter("available"));
        int categoryID = Integer.parseInt(req.getParameter("categoryID"));
        int publisherID = Integer.parseInt(req.getParameter("publisherID"));

        Book book = new Book(name, quantity, available, categoryID, publisherID);
        if (hasId) {
            book.setBookID(Integer.parseInt(req.getParameter("bookID")));
        }
        return book;
    }

    private PriceInput readPriceInput(HttpServletRequest req) {
        double amount = Double.parseDouble(req.getParameter("priceAmount"));
        String currency = trimToDefault(req.getParameter("priceCurrency"), "VND");
        String note = trimToNull(req.getParameter("priceNote"));
        return new PriceInput(amount, currency, note);
    }

    private void createBookWithPrice(HttpServletRequest req) throws SQLException {
        Book book = readBookFromRequest(req, false);
        PriceInput priceInput = readPriceInput(req);

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            int bookId = insertBook(con, book);
            int priceId = insertPrice(con, priceInput);
            insertBookPrice(con, bookId, priceId, LocalDate.now());

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void updateBookWithPrice(HttpServletRequest req) throws SQLException {
        Book book = readBookFromRequest(req, true);
        PriceInput priceInput = readPriceInput(req);

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            updateBook(con, book);

            CurrentPriceInfo currentPrice = getCurrentPriceInfo(con, book.getBookID());
            if (currentPrice == null) {
                int priceId = insertPrice(con, priceInput);
                insertBookPrice(con, book.getBookID(), priceId, LocalDate.now());
            } else if (isPriceChanged(currentPrice, priceInput)) {
                if (LocalDate.now().equals(currentPrice.getStartDate())) {
                    updatePrice(con, currentPrice.getPriceID(), priceInput);
                } else {
                    closeCurrentBookPrice(con, book.getBookID(), currentPrice.getPriceID(), currentPrice.getStartDate());
                    int priceId = insertPrice(con, priceInput);
                    insertBookPrice(con, book.getBookID(), priceId, LocalDate.now());
                }
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private int insertBook(Connection con, Book book) throws SQLException {
        String sql = "INSERT INTO Book(BookName, Quantity, Available, CategoryID, PublisherID) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getBookName());
            ps.setInt(2, book.getQuantity());
            ps.setInt(3, book.getAvailable());
            ps.setInt(4, book.getCategoryID());
            ps.setInt(5, book.getPublisherID());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the tao sach.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong lay duoc BookID moi.");
    }

    private void updateBook(Connection con, Book book) throws SQLException {
        String sql = "UPDATE Book SET BookName=?, Quantity=?, Available=?, CategoryID=?, PublisherID=? WHERE BookID=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, book.getBookName());
            ps.setInt(2, book.getQuantity());
            ps.setInt(3, book.getAvailable());
            ps.setInt(4, book.getCategoryID());
            ps.setInt(5, book.getPublisherID());
            ps.setInt(6, book.getBookID());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Cap nhat sach that bai.");
            }
        }
    }

    private int insertPrice(Connection con, PriceInput priceInput) throws SQLException {
        String sql = "INSERT INTO Price(Amount, Currency, Note) VALUES(?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, priceInput.getAmount());
            ps.setString(2, priceInput.getCurrency());
            ps.setString(3, priceInput.getNote());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the tao gia sach.");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Khong lay duoc PriceID moi.");
    }

    private void updatePrice(Connection con, int priceId, PriceInput priceInput) throws SQLException {
        String sql = "UPDATE Price SET Amount = ?, Currency = ?, Note = ? WHERE PriceID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, priceInput.getAmount());
            ps.setString(2, priceInput.getCurrency());
            ps.setString(3, priceInput.getNote());
            ps.setInt(4, priceId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Cap nhat gia sach that bai.");
            }
        }
    }

    private void insertBookPrice(Connection con, int bookId, int priceId, LocalDate startDate) throws SQLException {
        String sql = "INSERT INTO BookPrice(BookID, PriceID, StartDate, EndDate) VALUES(?,?,?,NULL)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, priceId);
            ps.setDate(3, Date.valueOf(startDate));
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Khong the gan gia cho sach.");
            }
        }
    }

    private void closeCurrentBookPrice(Connection con, int bookId, int priceId, LocalDate startDate) throws SQLException {
        String sql = "UPDATE BookPrice SET EndDate = ? WHERE BookID = ? AND PriceID = ? AND StartDate = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(LocalDate.now().minusDays(1)));
            ps.setInt(2, bookId);
            ps.setInt(3, priceId);
            ps.setDate(4, Date.valueOf(startDate));
            ps.executeUpdate();
        }
    }

    private CurrentPriceInfo getCurrentPriceInfo(int bookId) throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            return getCurrentPriceInfo(con, bookId);
        } finally {
            con.close();
        }
    }

    private CurrentPriceInfo getCurrentPriceInfo(Connection con, int bookId) throws SQLException {
        String sql = "SELECT TOP 1 bp.PriceID, bp.StartDate, p.Amount, p.Currency, p.Note "
                + "FROM BookPrice bp "
                + "JOIN Price p ON p.PriceID = bp.PriceID "
                + "WHERE bp.BookID = ? AND bp.StartDate <= CAST(GETDATE() AS DATE) "
                + "AND (bp.EndDate IS NULL OR bp.EndDate >= CAST(GETDATE() AS DATE)) "
                + "ORDER BY bp.StartDate DESC, bp.PriceID DESC";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CurrentPriceInfo(
                            rs.getInt("PriceID"),
                            rs.getDate("StartDate").toLocalDate(),
                            rs.getDouble("Amount"),
                            rs.getString("Currency"),
                            rs.getString("Note"));
                }
            }
        }
        return null;
    }

    private boolean isPriceChanged(CurrentPriceInfo currentPrice, PriceInput priceInput) {
        if (currentPrice == null) {
            return true;
        }
        if (Double.compare(currentPrice.getAmount(), priceInput.getAmount()) != 0) {
            return true;
        }
        if (!safeEquals(currentPrice.getCurrency(), priceInput.getCurrency())) {
            return true;
        }
        return !safeEquals(currentPrice.getNote(), priceInput.getNote());
    }

    private boolean safeEquals(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private String trimToDefault(String value, String defaultValue) {
        String trimmed = trimToNull(value);
        return trimmed == null ? defaultValue : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class CurrentPriceInfo {
        private final int priceID;
        private final LocalDate startDate;
        private final double amount;
        private final String currency;
        private final String note;

        public CurrentPriceInfo(int priceID, LocalDate startDate, double amount, String currency, String note) {
            this.priceID = priceID;
            this.startDate = startDate;
            this.amount = amount;
            this.currency = currency;
            this.note = note;
        }

        public int getPriceID() {
            return priceID;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public double getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getNote() {
            return note;
        }
    }

    private static class PriceInput {
        private final double amount;
        private final String currency;
        private final String note;

        PriceInput(double amount, String currency, String note) {
            this.amount = amount;
            this.currency = currency;
            this.note = note;
        }

        public double getAmount() {
            return amount;
        }

        public String getCurrency() {
            return currency;
        }

        public String getNote() {
            return note;
        }
    }
}
