package Controller.borrow;

import Entities.Book;
import Entities.BorrowItem;
import Entities.Staff;
import Entities.Student;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOStudent;
import Model.DBConnection;
import Utils.RoleUtils;
import ViewModel.BorrowRow;
import ViewModel.PageSlice;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowAdminHandler {

    private final DAOStudent daoStudent;
    private final DAOBook daoBook;
    private final DAOBorrow daoBorrow;
    private final DAOBorrowItem daoBorrowItem;
    private final BorrowHelper helper;
    private final BorrowTransactionService transactionService;

    public BorrowAdminHandler(DAOStudent daoStudent, DAOBook daoBook, DAOBorrow daoBorrow,
            DAOBorrowItem daoBorrowItem, BorrowHelper helper, BorrowTransactionService transactionService) {
        this.daoStudent = daoStudent;
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoBorrowItem = daoBorrowItem;
        this.helper = helper;
        this.transactionService = transactionService;
    }

    public void showList(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!helper.canAccessAdminSection(req)) {
            String errorMessage = URLEncoder.encode("Truy cap bi tu choi", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + BorrowHelper.PUBLIC_BORROWS_PATH + "?action=list&error=" + errorMessage);
            return;
        }

        int page = helper.parsePage(req.getParameter("page"), 1);
        List<BorrowRow> rows = daoBorrow.getBorrowRows();
        PageSlice<BorrowRow> pageSlice = helper.paginate(rows, page, BorrowHelper.ADMIN_BORROW_PAGE_SIZE);
        req.setAttribute("borrows", pageSlice.getItems());
        req.setAttribute("currentPage", pageSlice.getPage());
        req.setAttribute("totalPages", pageSlice.getTotalPages());
        req.setAttribute("totalItems", pageSlice.getTotalItems());
        req.getRequestDispatcher("/WEB-INF/views/borrow/list.jsp").forward(req, resp);
    }

    public void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc tao phieu muon theo form quan tri.");
            return;
        }
        loadCreateData(req);
        req.getRequestDispatcher("/WEB-INF/views/borrow/create.jsp").forward(req, resp);
    }

    public void createBorrow(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc tao phieu muon theo form quan tri.");
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
            studentId = helper.parsePositiveInt(req.getParameter("studentID"), "Student");
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = helper.parsePositiveInt(req.getParameter("quantity"), "Quantity");
            dueDate = LocalDate.parse(req.getParameter("dueDate"));
            if (dueDate.isBefore(borrowDate)) {
                throw new IllegalArgumentException("Han tra phai >= ngay muon.");
            }
        } catch (Exception e) {
            forwardCreateError(req, resp, "Du lieu khong hop le: " + e.getMessage());
            return;
        }

        try {
            transactionService.createBorrowTransaction(studentId, staff.getStaffID(), bookId, quantity, borrowDate, dueDate);
            helper.redirectWithMessage(req, resp, "msg", "Tao phieu muon thanh cong.");
        } catch (SQLException e) {
            forwardCreateError(req, resp, e.getMessage());
        }
    }

    public void returnBorrow(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Hoc sinh khong duoc xac nhan tra sach.");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BorrowID khong hop le.");
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
                helper.redirectWithMessage(req, resp, "error", "Khong tim thay phieu muon.");
                return;
            }

            if ("Returned".equalsIgnoreCase(status)) {
                con.rollback();
                helper.redirectWithMessage(req, resp, "msg", "Phieu nay da duoc tra truoc do.");
                return;
            }

            List<BorrowItem> items = daoBorrowItem.getByBorrowId(con, borrowId);
            if (items.isEmpty()) {
                con.rollback();
                helper.redirectWithMessage(req, resp, "error", "Phieu muon khong co sach de tra.");
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
            helper.redirectWithMessage(req, resp, "msg", "Xac nhan tra sach thanh cong.");
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
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
}
