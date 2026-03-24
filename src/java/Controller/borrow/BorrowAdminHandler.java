package Controller.borrow;

import Entities.Book;
import Entities.Borrow;
import Entities.BorrowItem;
import Entities.Staff;
import Entities.Student;
import Model.DAOBook;
import Model.DAOBookHold;
import Model.DAOBorrow;
import Model.DAOBorrowItem;
import Model.DAOStudent;
import Model.DBConnection;
import Utils.NotificationBroadcaster;
import Utils.HoldNotificationService;
import Utils.RoleUtils;
import ViewModel.BorrowRow;
import ViewModel.HoldRow;
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
    private final DAOBookHold daoBookHold;
    private final BorrowHelper helper;
    private final BorrowTransactionService transactionService;
    private final HoldNotificationService holdNotificationService;

    public BorrowAdminHandler(DAOStudent daoStudent, DAOBook daoBook, DAOBorrow daoBorrow,
            DAOBorrowItem daoBorrowItem, BorrowHelper helper,
            BorrowTransactionService transactionService) {
        this.daoStudent = daoStudent;
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoBorrowItem = daoBorrowItem;
        this.daoBookHold = new DAOBookHold();
        this.helper = helper;
        this.transactionService = transactionService;
        this.holdNotificationService = new HoldNotificationService();
    }

    public void showList(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        try {
            daoBookHold.expireAndReleaseHolds();
        } catch (Exception ignored) {
        }

        if (!helper.canAccessAdminSection(req)) {
            String err = URLEncoder.encode("Truy cập bị từ chối", StandardCharsets.UTF_8);
            resp.sendRedirect(req.getContextPath() + BorrowHelper.PUBLIC_BORROWS_PATH + "?action=list&error=" + err);
            return;
        }

        int page = helper.parsePage(req.getParameter("page"), 1);
        List<BorrowRow> rows = daoBorrow.getBorrowRows();
        PageSlice<BorrowRow> pageSlice = helper.paginate(rows, page, BorrowHelper.ADMIN_BORROW_PAGE_SIZE);

        int pendingCount = daoBorrow.countPending();
        List<BorrowRow> pendingRows = daoBorrow.getPendingBorrowRows();
        List<HoldRow> activeHolds = daoBookHold.getAllActive();

        req.setAttribute("borrows", pageSlice.getItems());
        req.setAttribute("currentPage", pageSlice.getPage());
        req.setAttribute("totalPages", pageSlice.getTotalPages());
        req.setAttribute("totalItems", pageSlice.getTotalItems());
        req.setAttribute("pendingCount", pendingCount);
        req.setAttribute("pendingBorrows", pendingRows);
        req.setAttribute("activeHolds", activeHolds);

        req.getRequestDispatcher("/WEB-INF/views/borrow/list.jsp").forward(req, resp);
    }

    public void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Sinh viên không được tạo phiếu mượn theo biểu mẫu quản trị.");
            return;
        }
        loadCreateData(req);
        req.getRequestDispatcher("/WEB-INF/views/admin/borrow/create.jsp").forward(req, resp);
    }

    public void createBorrow(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Sinh viên không được tạo phiếu mượn.");
            return;
        }

        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        int studentId, bookId, quantity;
        LocalDate dueDate, borrowDate = LocalDate.now();
        try {
            studentId = helper.parsePositiveInt(req.getParameter("studentID"), "Student");
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = helper.parsePositiveInt(req.getParameter("quantity"), "Quantity");
            dueDate = LocalDate.parse(req.getParameter("dueDate"));
            if (dueDate.isBefore(borrowDate)) {
                throw new IllegalArgumentException("Hạn trả phải >= ngày mượn.");
            }
        } catch (Exception e) {
            forwardCreateError(req, resp, "Dữ liệu không hợp lệ: " + e.getMessage());
            return;
        }

        try {
            transactionService.createBorrowTransaction(studentId, staff.getStaffID(), bookId, quantity, borrowDate, dueDate);
            helper.redirectWithMessage(req, resp, "msg", "Tạo phiếu mượn thành công.");
        } catch (SQLException e) {
            forwardCreateError(req, resp, e.getMessage());
        }
    }

    public void approveBorrow(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Không có quyền duyệt.");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BorrowID không hợp lệ.");
            return;
        }

        // Lấy info TRƯỚC khi approve (dùng connection riêng, không conflict)
        Borrow borrow = daoBorrow.getById(borrowId);

        try {
            Staff adminStaff = RoleUtils.getLoggedStaff(req);
            if (adminStaff == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginURL");
                return;
            }
            transactionService.approveBorrow(borrowId, adminStaff.getStaffID());

            // WebSocket thông báo student
            if (borrow != null) {
                NotificationBroadcaster.notifyStudentApproved(borrow.getStudentID(), borrowId);

                // Broadcast available changed tới tất cả student
                List<BorrowItem> items = daoBorrowItem.getByBorrowId(borrowId);
                for (BorrowItem item : items) {
                    Book book = daoBook.getById(item.getBookID());
                    if (book != null) {
                        NotificationBroadcaster.notifyAllStudentsBookChanged(
                                book.getBookID(), book.getBookName(), book.getAvailable());
                    }
                }
            }

            helper.redirectWithMessage(req, resp, "msg", "Đã duyệt phiếu mượn #" + borrowId + " thành công.");
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void rejectBorrow(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Không có quyền từ chối.");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BorrowID không hợp lệ.");
            return;
        }

        Borrow borrow = daoBorrow.getById(borrowId);

        try {
            Staff adminStaff = RoleUtils.getLoggedStaff(req);
            if (adminStaff == null) {
                resp.sendRedirect(req.getContextPath() + "/LoginURL");
                return;
            }
            transactionService.rejectBorrow(borrowId, adminStaff.getStaffID());

            // WebSocket thông báo student
            if (borrow != null) {
                NotificationBroadcaster.notifyStudentRejected(borrow.getStudentID(), borrowId);
            }

            helper.redirectWithMessage(req, resp, "msg", "Đã từ chối phiếu mượn #" + borrowId + ".");
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    /**
     * Return borrow — FIX: không gọi getById bên trong transaction. Lấy book
     * names SAU khi commit.
     */
    public void returnBorrow(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!helper.isAdminSection(req) || RoleUtils.isStudentOnly(req)) {
            helper.redirectWithMessage(req, resp, "error", "Sinh viên không được xác nhận trả sách.");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BorrowID không hợp lệ.");
            return;
        }

        // Lấy borrow info TRƯỚC transaction (connection riêng)
        Borrow borrowInfo = daoBorrow.getById(borrowId);
        if (borrowInfo == null) {
            helper.redirectWithMessage(req, resp, "error", "Không tìm thấy phiếu mượn.");
            return;
        }
        if ("Returned".equalsIgnoreCase(borrowInfo.getStatus())) {
            helper.redirectWithMessage(req, resp, "msg", "Phiếu này đã được trả trước đó.");
            return;
        }

        // Lấy items TRƯỚC transaction (connection riêng)
        List<BorrowItem> items = daoBorrowItem.getByBorrowId(borrowId);
        if (items.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Phiếu mượn không có sách để trả.");
            return;
        }

        // === TRANSACTION: chỉ update DB, không gọi method nào mở connection khác ===
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Không thể kết nối đến cơ sở dữ liệu.");
        }

        try {
            con.setAutoCommit(false);

            // Double-check status with lock
            String status = daoBorrow.getStatusForUpdate(con, borrowId);
            if ("Returned".equalsIgnoreCase(status)) {
                con.rollback();
                helper.redirectWithMessage(req, resp, "msg", "Phiếu này đã được trả trước đó.");
                return;
            }

            // Tăng Available cho từng sách
            for (BorrowItem item : items) {
                int increased = daoBook.increaseAvailable(con, item.getBookID(), item.getQuantity());
                if (increased == 0) {
                    throw new SQLException("Không tìm thấy sách id=" + item.getBookID());
                }
            }

            // Cập nhật status = Returned
            if (daoBorrow.updateReturned(con, borrowId) == 0) {
                throw new SQLException("Cập nhật trả sách thất bại.");
            }

            con.commit();
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }

        // === SAU COMMIT: Xử lý hold + WebSocket (connection riêng, an toàn) ===
        NotificationBroadcaster.notifyStudentReturnConfirmed(borrowInfo.getStudentID(), borrowId);

        StringBuilder holdMsg = new StringBuilder();
        for (BorrowItem item : items) {
            Book book = daoBook.getById(item.getBookID());
            String bookName = (book != null) ? book.getBookName() : ("Sách #" + item.getBookID());

            boolean hasHold = holdNotificationService.processHoldQueue(item.getBookID(), bookName);

            if (hasHold) {
                // Có người hold → GIẢM Available lại 1 (reserve cho họ)
                Connection reserveCon = DBConnection.getConnection();
                if (reserveCon != null) {
                    try {
                        daoBook.decreaseAvailable(reserveCon, item.getBookID(), 1);
                    } catch (SQLException e) {
                        System.err.println("[Hold] Không thể reserve sách: " + e.getMessage());
                    } finally {
                        reserveCon.close();
                    }
                }
                holdMsg.append(" Đã thông báo người chờ sách \"").append(bookName).append("\".");
            }

            // Broadcast available changed tới tất cả student
            Book updatedBook = daoBook.getById(item.getBookID());
            if (updatedBook != null) {
                NotificationBroadcaster.notifyAllStudentsBookChanged(
                        updatedBook.getBookID(), updatedBook.getBookName(), updatedBook.getAvailable());
            }
        }

        helper.redirectWithMessage(req, resp, "msg",
                "Xác nhận trả sách thành công." + holdMsg.toString());
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
        req.getRequestDispatcher("/WEB-INF/views/admin/borrow/create.jsp").forward(req, resp);
    }
}
