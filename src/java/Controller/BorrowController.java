package Controller;

import Entities.Book;
import Entities.BorrowItem;
import Entities.OrderDetail;
import Entities.Staff;
import Entities.Student;
import Model.DAOBook;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Model.DAOStudent;
import Model.DBConnection;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "BorrowController", urlPatterns = {"/borrows", "/admin/borrows"})
public class BorrowController extends HttpServlet {

    private static final int DEFAULT_STUDENT_BORROW_DAYS = 7;
    private static final String PUBLIC_BORROWS_PATH = "/borrows";
    private static final String ADMIN_BORROWS_PATH = "/admin/borrows";

    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOBook daoBook = new DAOBook();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOBorrowItem daoBorrowItem = new DAOBorrowItem();
    private final DAOOrders daoOrders = new DAOOrders();
    private final DAOOrderDetail daoOrderDetail = new DAOOrderDetail();
    private final DAOBookPrice daoBookPrice = new DAOBookPrice();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BORROWS_PATH + "?action=list");
            return;
        }

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
        if (shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BORROWS_PATH + "?action=list");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    createBorrow(req, resp);
                    break;
                case "return":
                    returnBorrow(req, resp);
                    break;
                case "borrow":
                    borrowAsStudent(req, resp);
                    break;
                case "buy":
                    buyBookAsStudent(req, resp);
                    break;
                case "requestReturn":
                    requestReturnAsStudent(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list");
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        if (!isAdminSection(req) && RoleUtils.isStudentOnly(req)) {
            Integer studentId = resolveStudentIdForStaff(staff);
            if (studentId == null) {
                req.setAttribute("mappingError", "Khong xac dinh duoc tai khoan sinh vien cho user hien tai.");
                req.setAttribute("availableBooks", Collections.emptyList());
                req.setAttribute("bookPrices", Collections.emptyList());
                req.setAttribute("borrows", Collections.emptyList());
            } else {
                req.setAttribute("studentId", studentId);
                req.setAttribute("availableBooks", fetchBorrowableBooks());
                req.setAttribute("bookPrices", daoBookPrice.getBookPriceRows());
                req.setAttribute("borrows", daoBorrow.getBorrowRowsByStudent(studentId));
            }
            req.getRequestDispatcher("/WEB-INF/views/borrow/student.jsp").forward(req, resp);
            return;
        }

        if (!canAccessAdminSection(req)) {
            resp.sendRedirect(req.getContextPath() + PUBLIC_BORROWS_PATH + "?action=list&error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        req.setAttribute("borrows", daoBorrow.getBorrowRows());
        req.getRequestDispatcher("/WEB-INF/views/borrow/list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        if (!isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc tao phieu muon theo form quan tri.");
            return;
        }
        loadCreateData(req);
        req.getRequestDispatcher("/WEB-INF/views/borrow/create.jsp").forward(req, resp);
    }

    private void loadCreateData(HttpServletRequest req) throws SQLException {
        List<Student> students = daoStudent.getAll();
        List<Book> books = daoBook.getAll();
        List<Book> borrowableBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getAvailable() > 0) {
                borrowableBooks.add(book);
            }
        }
        req.setAttribute("students", students);
        req.setAttribute("books", borrowableBooks);
    }

    private void createBorrow(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        if (!isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc tao phieu muon theo form quan tri.");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
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

        try {
            createBorrowTransaction(studentId, staff.getStaffID(), bookId, quantity, borrowDate, dueDate);
            redirectWithMessage(req, resp, "msg", "Tao phieu muon thanh cong.");
        } catch (SQLException e) {
            forwardCreateError(req, resp, e.getMessage());
        }
    }

    private void borrowAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc muon sach nhanh.");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        Integer studentId = resolveStudentIdForStaff(staff);
        if (studentId == null) {
            redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien cho tai khoan hien tai.");
            return;
        }

        int bookId;
        try {
            bookId = parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(DEFAULT_STUDENT_BORROW_DAYS);

        try {
            createBorrowTransaction(studentId, staff.getStaffID(), bookId, 1, borrowDate, dueDate);
            redirectWithMessage(req, resp, "msg", "Muon sach thanh cong. Han tra: " + dueDate);
        } catch (SQLException e) {
            redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    private void buyBookAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc mua sach.");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        Integer studentId = resolveStudentIdForStaff(staff);
        if (studentId == null) {
            redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        int bookId;
        try {
            bookId = parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            int available = daoBook.getAvailable(con, bookId);
            if (available <= 0) {
                con.rollback();
                redirectWithMessage(req, resp, "error", "Sach da het hang.");
                return;
            }

            double unitPrice = daoBookPrice.getCurrentSellingPrice(con, bookId);
            if (unitPrice <= 0) {
                con.rollback();
                redirectWithMessage(req, resp, "error", "Sach chua co gia ban hop le.");
                return;
            }

            int orderId = daoOrders.insertPending(con, studentId, staff.getStaffID(), unitPrice);
            int affected = daoOrderDetail.insert(con, new OrderDetail(orderId, bookId, 1, unitPrice));
            if (affected == 0) {
                throw new SQLException("Khong the tao chi tiet don hang.");
            }

            con.commit();
            redirectWithMessage(req, resp, "msg", "Da gui yeu cau mua sach. Vui long cho Staff/Admin xac nhan.");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private void requestReturnAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc gui yeu cau tra.");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        Integer studentId = resolveStudentIdForStaff(staff);
        if (studentId == null) {
            redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien cho tai khoan hien tai.");
            return;
        }

        int borrowId;
        try {
            borrowId = parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "BorrowID khong hop le.");
            return;
        }

        if (!daoBorrow.existsOwnedByStudentAndNotReturned(borrowId, studentId)) {
            redirectWithMessage(req, resp, "error", "Khong tim thay phieu muon hop le de yeu cau tra.");
            return;
        }

        redirectWithMessage(req, resp, "msg", "Da gui yeu cau tra sach. Vui long cho staff/admin xac nhan.");
    }

    private void createBorrowTransaction(int studentId, int staffId, int bookId, int quantity, LocalDate borrowDate, LocalDate dueDate)
            throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            int available = daoBook.getAvailable(con, bookId);
            if (available < quantity) {
                con.rollback();
                throw new SQLException("So luong sach con lai khong du. Con lai: " + available);
            }

            int borrowId = daoBorrow.insert(con, studentId, staffId, borrowDate, dueDate, "Borrowing");

            int borrowItemAffected = daoBorrowItem.insert(con, new BorrowItem(borrowId, bookId, quantity));
            if (borrowItemAffected == 0) {
                throw new SQLException("Khong the tao chi tiet muon.");
            }

            int decreaseAffected = daoBook.decreaseAvailable(con, bookId, quantity);
            if (decreaseAffected == 0) {
                throw new SQLException("Khong du so luong sach de muon.");
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

    private void returnBorrow(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc xac nhan tra sach.");
            return;
        }

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

            String status = daoBorrow.getStatusForUpdate(con, borrowId);
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

            List<BorrowItem> items = daoBorrowItem.getByBorrowId(con, borrowId);
            if (items.isEmpty()) {
                con.rollback();
                redirectWithMessage(req, resp, "error", "Phieu muon khong co sach de tra.");
                return;
            }

            for (BorrowItem item : items) {
                int increased = daoBook.increaseAvailable(con, item.getBookID(), item.getQuantity());
                if (increased == 0) {
                    throw new SQLException("Khong tim thay sach id=" + item.getBookID());
                }
            }

            if (daoBorrow.updateReturned(con, borrowId) == 0) {
                throw new SQLException("Cap nhat tra sach that bai.");
            }

            con.commit();
            redirectWithMessage(req, resp, "msg", "Xac nhan tra sach thanh cong.");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private Integer resolveStudentIdForStaff(Staff staff) throws SQLException {
        if (staff == null) {
            return null;
        }

        Integer candidateFromUsername = extractTrailingNumber(staff.getUsername());
        if (candidateFromUsername != null && daoStudent.getById(candidateFromUsername) != null) {
            return candidateFromUsername;
        }

        int sameId = staff.getStaffID();
        if (sameId > 0 && daoStudent.getById(sameId) != null) {
            return sameId;
        }

        return null;
    }

    private Integer extractTrailingNumber(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        int index = value.length() - 1;
        while (index >= 0 && Character.isDigit(value.charAt(index))) {
            index--;
        }
        if (index == value.length() - 1) {
            return null;
        }
        String digits = value.substring(index + 1);
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void forwardCreateError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws SQLException, ServletException, IOException {
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
        resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&" + key + "=" + encoded);
    }

    private boolean shouldRedirectToAdminRoute(HttpServletRequest req) {
        return PUBLIC_BORROWS_PATH.equals(req.getServletPath()) && !RoleUtils.isStudentOnly(req);
    }

    private boolean isAdminSection(HttpServletRequest req) {
        return ADMIN_BORROWS_PATH.equals(req.getServletPath());
    }

    private boolean canAccessAdminSection(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private String getListPath(HttpServletRequest req) {
        return isAdminSection(req) ? ADMIN_BORROWS_PATH : PUBLIC_BORROWS_PATH;
    }

    private int parsePositiveInt(String raw, String fieldName) {
        int value = Integer.parseInt(raw);
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phai > 0");
        }
        return value;
    }

    private List<Book> fetchBorrowableBooks() throws SQLException {
        List<Book> allBooks = daoBook.getAll();
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getAvailable() > 0) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }
}
