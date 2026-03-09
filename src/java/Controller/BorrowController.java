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
import ViewModel.BookPriceRow;
import ViewModel.BorrowRow;
import ViewModel.BuyListSnapshot;
import ViewModel.OrderRow;
import ViewModel.PageSlice;
import ViewModel.PurchaseRequestItem;
import ViewModel.StudentBuyListRow;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "BorrowController", urlPatterns = {"/borrows", "/admin/borrows"})
public class BorrowController extends HttpServlet {

    private static final int DEFAULT_STUDENT_BORROW_DAYS = 7;
    private static final int ADMIN_BORROW_PAGE_SIZE = 10;
    private static final int STUDENT_BOOK_PAGE_SIZE = 8;
    private static final int STUDENT_PURCHASE_PAGE_SIZE = 8;
    private static final String PUBLIC_BORROWS_PATH = "/borrows";
    private static final String ADMIN_BORROWS_PATH = "/admin/borrows";
    private static final String BUY_LIST_SESSION_KEY = "studentBuyList";

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
                case "addBuyList":
                    addToBuyList(req, resp);
                    break;
                case "removeBuyItem":
                    removeFromBuyList(req, resp);
                    break;
                case "updateBuyQty":
                    updateBuyListQuantity(req, resp);
                    break;
                case "orderBuyItem":
                    orderOneFromBuyList(req, resp);
                    break;
                case "orderBuyAll":
                    orderAllFromBuyList(req, resp);
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
                req.setAttribute("buyListItems", Collections.emptyList());
                req.setAttribute("purchasedOrders", Collections.emptyList());
                req.setAttribute("bookCurrentPage", 1);
                req.setAttribute("bookTotalPages", 1);
                req.setAttribute("purchaseCurrentPage", 1);
                req.setAttribute("purchaseTotalPages", 1);
                req.setAttribute("bookSearch", "");
                req.setAttribute("purchaseSearch", "");
                req.setAttribute("buyListTotal", 0.0d);
            } else {
                String bookSearch = trim(req.getParameter("bookSearch"));
                String purchaseSearch = trim(req.getParameter("purchaseSearch"));
                int bookPage = parsePage(req.getParameter("bookPage"), 1);
                int purchasePage = parsePage(req.getParameter("purchasePage"), 1);

                List<Book> allBooks = daoBook.getAll();
                List<Book> availableBooks = filterBooksByKeyword(filterBorrowableBooks(allBooks), bookSearch);
                PageSlice<Book> availablePage = paginate(availableBooks, bookPage, STUDENT_BOOK_PAGE_SIZE);

                List<BookPriceRow> bookPrices = daoBookPrice.getBookPriceRows();
                BuyListSnapshot buyListSnapshot = buildBuyListSnapshot(req, allBooks, bookPrices);

                List<OrderRow> purchasedOrders = daoOrders.getOrderRows(studentId, purchaseSearch, "Approved");
                PageSlice<OrderRow> purchasePageSlice = paginate(purchasedOrders, purchasePage, STUDENT_PURCHASE_PAGE_SIZE);

                req.setAttribute("studentId", studentId);
                req.setAttribute("availableBooks", availablePage.getItems());
                req.setAttribute("bookCurrentPage", availablePage.getPage());
                req.setAttribute("bookTotalPages", availablePage.getTotalPages());
                req.setAttribute("bookTotalItems", availablePage.getTotalItems());
                req.setAttribute("bookSearch", bookSearch);

                req.setAttribute("bookPrices", bookPrices);
                req.setAttribute("buyListItems", buyListSnapshot.getItems());
                req.setAttribute("buyListTotal", buyListSnapshot.getTotalAmount());

                req.setAttribute("purchasedOrders", purchasePageSlice.getItems());
                req.setAttribute("purchaseCurrentPage", purchasePageSlice.getPage());
                req.setAttribute("purchaseTotalPages", purchasePageSlice.getTotalPages());
                req.setAttribute("purchaseTotalItems", purchasePageSlice.getTotalItems());
                req.setAttribute("purchaseSearch", purchaseSearch);

                req.setAttribute("borrows", daoBorrow.getBorrowRowsByStudent(studentId));
            }
            req.getRequestDispatcher("/WEB-INF/views/borrow/student.jsp").forward(req, resp);
            return;
        }

        if (!canAccessAdminSection(req)) {
            resp.sendRedirect(req.getContextPath() + PUBLIC_BORROWS_PATH + "?action=list&error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        int page = parsePage(req.getParameter("page"), 1);
        List<BorrowRow> rows = daoBorrow.getBorrowRows();
        PageSlice<BorrowRow> pageSlice = paginate(rows, page, ADMIN_BORROW_PAGE_SIZE);
        req.setAttribute("borrows", pageSlice.getItems());
        req.setAttribute("currentPage", pageSlice.getPage());
        req.setAttribute("totalPages", pageSlice.getTotalPages());
        req.setAttribute("totalItems", pageSlice.getTotalItems());
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

    private void addToBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc them vao danh sach mua.");
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
            int available = daoBook.getAvailable(con, bookId);
            if (available <= 0) {
                redirectWithMessage(req, resp, "error", "Sach da het hang.");
                return;
            }

            double unitPrice = daoBookPrice.getCurrentSellingPrice(con, bookId);
            if (unitPrice <= 0) {
                redirectWithMessage(req, resp, "error", "Sach chua co gia ban hop le.");
                return;
            }

            LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
            int currentQty = buyList.getOrDefault(bookId, 0);
            if (currentQty + 1 > available) {
                redirectWithMessage(req, resp, "error", "So luong vuot qua ton kho hien tai.");
                return;
            }

            buyList.put(bookId, currentQty + 1);
            redirectWithMessage(req, resp, "msg", "Da them sach vao danh sach can mua.");
        } finally {
            con.close();
        }
    }

    private void removeFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc sua danh sach mua.");
            return;
        }

        int bookId;
        try {
            bookId = parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
        if (buyList.remove(bookId) != null) {
            redirectWithMessage(req, resp, "msg", "Da xoa sach khoi danh sach can mua.");
        } else {
            redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
        }
    }

    private void updateBuyListQuantity(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc sua danh sach mua.");
            return;
        }

        int bookId;
        int quantity;
        try {
            bookId = parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = parsePositiveInt(req.getParameter("quantity"), "Quantity");
        } catch (Exception e) {
            redirectWithMessage(req, resp, "error", "Du lieu cap nhat so luong khong hop le.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
        if (!buyList.containsKey(bookId)) {
            redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            int available = daoBook.getAvailable(con, bookId);
            if (quantity > available) {
                redirectWithMessage(req, resp, "error", "So luong vuot qua ton kho hien tai.");
                return;
            }

            buyList.put(bookId, quantity);
            redirectWithMessage(req, resp, "msg", "Da cap nhat so luong.");
        } finally {
            con.close();
        }
    }

    private void orderOneFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc gui don mua.");
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

        LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
        Integer quantity = buyList.get(bookId);
        if (quantity == null || quantity <= 0) {
            redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
            return;
        }

        PurchaseRequestItem item = new PurchaseRequestItem(bookId, quantity);
        try {
            int orderId = createPendingOrder(studentId, staff.getStaffID(), List.of(item));
            buyList.remove(bookId);
            redirectWithMessage(req, resp, "msg", "Da gui duyet 1 sach. Ma don: " + orderId);
        } catch (SQLException e) {
            redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    private void orderAllFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!RoleUtils.isStudentOnly(req)) {
            redirectWithMessage(req, resp, "error", "Chi tai khoan hoc sinh moi duoc gui don mua.");
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

        LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
        if (buyList.isEmpty()) {
            redirectWithMessage(req, resp, "error", "Danh sach can mua dang trong.");
            return;
        }

        List<PurchaseRequestItem> items = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : buyList.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                items.add(new PurchaseRequestItem(entry.getKey(), entry.getValue()));
            }
        }
        if (items.isEmpty()) {
            redirectWithMessage(req, resp, "error", "Danh sach can mua dang trong.");
            return;
        }

        try {
            int orderId = createPendingOrder(studentId, staff.getStaffID(), items);
            buyList.clear();
            redirectWithMessage(req, resp, "msg", "Da gui duyet tat ca sach can mua. Ma don: " + orderId);
        } catch (SQLException e) {
            redirectWithMessage(req, resp, "error", e.getMessage());
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

    private int parsePage(String raw, int defaultPage) {
        if (raw == null || raw.trim().isEmpty()) {
            return defaultPage;
        }
        try {
            int page = Integer.parseInt(raw.trim());
            return page > 0 ? page : defaultPage;
        } catch (NumberFormatException e) {
            return defaultPage;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private List<Book> filterBorrowableBooks(List<Book> allBooks) {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : allBooks == null ? Collections.<Book>emptyList() : allBooks) {
            if (book.getAvailable() > 0) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    private List<Book> filterBooksByKeyword(List<Book> books, String keyword) {
        String normalizedKeyword = trim(keyword).toLowerCase();
        if (normalizedKeyword.isEmpty()) {
            return books;
        }

        List<Book> filtered = new ArrayList<>();
        for (Book book : books) {
            if (String.valueOf(book.getBookID()).contains(normalizedKeyword)
                    || (book.getBookName() != null && book.getBookName().toLowerCase().contains(normalizedKeyword))) {
                filtered.add(book);
            }
        }
        return filtered;
    }

    private LinkedHashMap<Integer, Integer> getOrCreateBuyList(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object raw = session.getAttribute(BUY_LIST_SESSION_KEY);
        if (raw instanceof LinkedHashMap<?, ?>) {
            @SuppressWarnings("unchecked")
            LinkedHashMap<Integer, Integer> existing = (LinkedHashMap<Integer, Integer>) raw;
            return existing;
        }
        LinkedHashMap<Integer, Integer> created = new LinkedHashMap<>();
        session.setAttribute(BUY_LIST_SESSION_KEY, created);
        return created;
    }

    private BuyListSnapshot buildBuyListSnapshot(HttpServletRequest req, List<Book> allBooks,
            List<BookPriceRow> bookPrices) {
        Map<Integer, Book> bookById = new HashMap<>();
        for (Book book : allBooks) {
            bookById.put(book.getBookID(), book);
        }

        Map<Integer, BookPriceRow> priceByBookId = new HashMap<>();
        for (BookPriceRow priceRow : bookPrices) {
            priceByBookId.put(priceRow.getBookID(), priceRow);
        }

        List<StudentBuyListRow> rows = new ArrayList<>();
        double totalAmount = 0;

        LinkedHashMap<Integer, Integer> buyList = getOrCreateBuyList(req);
        for (Map.Entry<Integer, Integer> entry : buyList.entrySet()) {
            int bookId = entry.getKey();
            int quantity = entry.getValue() == null || entry.getValue() <= 0 ? 1 : entry.getValue();

            Book book = bookById.get(bookId);
            String bookName = book == null ? ("Book #" + bookId) : book.getBookName();
            int available = book == null ? 0 : book.getAvailable();

            BookPriceRow priceRow = priceByBookId.get(bookId);
            double unitPrice = priceRow == null ? 0 : priceRow.getAmount();
            String currency = priceRow == null ? "" : priceRow.getCurrency();

            boolean canOrder = book != null && available >= quantity && unitPrice > 0;
            double lineTotal = unitPrice * quantity;
            totalAmount += lineTotal;

            rows.add(new StudentBuyListRow(bookId, bookName, quantity, available, unitPrice, currency, lineTotal, canOrder));
        }

        return new BuyListSnapshot(rows, totalAmount);
    }

    private int createPendingOrder(int studentId, int staffId, List<PurchaseRequestItem> items) throws SQLException {
        if (items == null || items.isEmpty()) {
            throw new SQLException("Don mua khong co sach.");
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            double totalAmount = 0;
            for (PurchaseRequestItem item : items) {
                int available = daoBook.getAvailable(con, item.getBookID());
                if (available < item.getQuantity()) {
                    throw new SQLException("Khong du ton kho cho sach id=" + item.getBookID());
                }

                double currentPrice = daoBookPrice.getCurrentSellingPrice(con, item.getBookID());
                if (currentPrice <= 0) {
                    throw new SQLException("Sach id=" + item.getBookID() + " chua co gia ban hop le.");
                }

                item.setUnitPrice(currentPrice);
                totalAmount += currentPrice * item.getQuantity();
            }

            int orderId = daoOrders.insertPending(con, studentId, staffId, totalAmount);
            for (PurchaseRequestItem item : items) {
                int affected = daoOrderDetail.insert(con,
                        new OrderDetail(orderId, item.getBookID(), item.getQuantity(), item.getUnitPrice()));
                if (affected == 0) {
                    throw new SQLException("Khong the tao chi tiet don hang cho sach id=" + item.getBookID());
                }
            }

            con.commit();
            return orderId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }

    private <T> PageSlice<T> paginate(List<T> source, int requestedPage, int pageSize) {
        int safePageSize = Math.max(1, pageSize);
        int totalItems = source == null ? 0 : source.size();
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) safePageSize));
        int page = Math.max(1, Math.min(requestedPage, totalPages));
        int fromIndex = (page - 1) * safePageSize;
        int toIndex = Math.min(fromIndex + safePageSize, totalItems);
        List<T> items = totalItems == 0 ? List.of() : source.subList(fromIndex, toIndex);
        return new PageSlice<>(items, page, totalPages, totalItems);
    }

}
