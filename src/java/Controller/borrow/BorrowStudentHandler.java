package Controller.borrow;

import Entities.Book;
import Entities.Orders;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Utils.RoleUtils;
import ViewModel.BookPriceRow;
import ViewModel.BuyListSnapshot;
import ViewModel.OrderItemRow;
import ViewModel.OrderRow;
import ViewModel.PageSlice;
import ViewModel.PurchaseRequestItem;
import ViewModel.StudentBuyListRow;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import Model.DBConnection;

public class BorrowStudentHandler {

    private final DAOBook daoBook;
    private final DAOBorrow daoBorrow;
    private final DAOOrders daoOrders;
    private final DAOOrderDetail daoOrderDetail;
    private final DAOBookPrice daoBookPrice;
    private final BorrowHelper helper;
    private final BorrowTransactionService transactionService;

    public BorrowStudentHandler(DAOBook daoBook, DAOBorrow daoBorrow, DAOOrders daoOrders,
            DAOOrderDetail daoOrderDetail, DAOBookPrice daoBookPrice,
            BorrowHelper helper, BorrowTransactionService transactionService) {
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoOrders = daoOrders;
        this.daoOrderDetail = daoOrderDetail;
        this.daoBookPrice = daoBookPrice;
        this.helper = helper;
        this.transactionService = transactionService;
    }

    public void showList(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
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
            req.getRequestDispatcher("/WEB-INF/views/borrow/student.jsp").forward(req, resp);
            return;
        }

        String bookSearch = helper.trim(req.getParameter("bookSearch"));
        String purchaseSearch = helper.trim(req.getParameter("purchaseSearch"));
        int bookPage = helper.parsePage(req.getParameter("bookPage"), 1);
        int purchasePage = helper.parsePage(req.getParameter("purchasePage"), 1);

        List<Book> allBooks = daoBook.getAll();
        List<Book> availableBooks = helper.filterBooksByKeyword(helper.filterBorrowableBooks(allBooks), bookSearch);
        PageSlice<Book> availablePage = helper.paginate(availableBooks, bookPage, BorrowHelper.STUDENT_BOOK_PAGE_SIZE);

        List<BookPriceRow> bookPrices = daoBookPrice.getBookPriceRows();
        BuyListSnapshot buyListSnapshot = helper.buildBuyListSnapshot(req, allBooks, bookPrices);

        List<OrderRow> purchasedOrders = daoOrders.getOrderRows(studentId, purchaseSearch, "Approved");
        PageSlice<OrderRow> purchasePageSlice = helper.paginate(purchasedOrders, purchasePage, BorrowHelper.STUDENT_PURCHASE_PAGE_SIZE);

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
        req.getRequestDispatcher("/WEB-INF/views/borrow/student.jsp").forward(req, resp);
    }

    public void showCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc checkout.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        List<Book> allBooks = daoBook.getAll();
        List<BookPriceRow> bookPrices = daoBookPrice.getBookPriceRows();
        BuyListSnapshot buyListSnapshot = helper.buildBuyListSnapshot(req, allBooks, bookPrices);

        List<StudentBuyListRow> checkoutItems = buyListSnapshot.getItems();
        if (checkoutItems.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sach can mua dang trong.");
            return;
        }

        int invalidCount = 0;
        int totalQuantity = 0;
        for (StudentBuyListRow item : checkoutItems) {
            if (!item.isCanOrder()) {
                invalidCount++;
            }
            totalQuantity += Math.max(item.getQuantity(), 0);
        }

        req.setAttribute("studentId", studentId);
        req.setAttribute("checkoutItems", checkoutItems);
        req.setAttribute("checkoutItemCount", checkoutItems.size());
        req.setAttribute("checkoutQuantity", totalQuantity);
        req.setAttribute("checkoutTotal", buyListSnapshot.getTotalAmount());
        req.setAttribute("checkoutInvalidCount", invalidCount);
        req.getRequestDispatcher("/WEB-INF/views/borrow/checkout.jsp").forward(req, resp);
    }

    public void showCheckoutSuccess(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc xem ket qua checkout.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        int orderId;
        try {
            orderId = helper.parsePositiveInt(req.getParameter("orderID"), "OrderID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "OrderID khong hop le.");
            return;
        }

        Orders order = daoOrders.getById(orderId);
        if (order == null || order.getStudentID() != studentId) {
            helper.redirectWithMessage(req, resp, "error", "Khong tim thay don hang vua dat.");
            return;
        }

        List<OrderItemRow> orderItems = daoOrderDetail.getOrderItemsWithBookName(orderId);
        if (orderItems.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Don hang khong co chi tiet.");
            return;
        }

        int totalQuantity = 0;
        for (OrderItemRow item : orderItems) {
            totalQuantity += Math.max(item.getQuantity(), 0);
        }

        req.setAttribute("studentId", studentId);
        req.setAttribute("successOrder", order);
        req.setAttribute("successItems", orderItems);
        req.setAttribute("successItemCount", orderItems.size());
        req.setAttribute("successTotalQuantity", totalQuantity);
        req.getRequestDispatcher("/WEB-INF/views/borrow/checkout-success.jsp").forward(req, resp);
    }

    public void borrowAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc muon sach nhanh.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien cho tai khoan hien tai.");
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BorrowHelper.DEFAULT_STUDENT_BORROW_DAYS);

        try {
            transactionService.createBorrowTransaction(studentId, staff.getStaffID(), bookId, 1, borrowDate, dueDate);
            helper.redirectWithMessage(req, resp, "msg", "Muon sach thanh cong. Han tra: " + dueDate);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void buyBookAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc mua sach.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        try {
            int orderId = transactionService.createPendingOrder(
                    studentId,
                    staff.getStaffID(),
                    List.of(new PurchaseRequestItem(bookId, 1)));
            helper.redirectToCheckoutSuccess(req, resp, orderId);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void addToBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc them vao danh sach mua.")) {
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            int available = daoBook.getAvailable(con, bookId);
            if (available <= 0) {
                helper.redirectWithMessage(req, resp, "error", "Sach da het hang.");
                return;
            }

            double unitPrice = daoBookPrice.getCurrentSellingPrice(con, bookId);
            if (unitPrice <= 0) {
                helper.redirectWithMessage(req, resp, "error", "Sach chua co gia ban hop le.");
                return;
            }

            LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
            int currentQty = buyList.getOrDefault(bookId, 0);
            if (currentQty + 1 > available) {
                helper.redirectWithMessage(req, resp, "error", "So luong vuot qua ton kho hien tai.");
                return;
            }

            buyList.put(bookId, currentQty + 1);
            helper.redirectWithMessage(req, resp, "msg", "Da them sach vao danh sach can mua.");
        } finally {
            con.close();
        }
    }

    public void removeFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc sua danh sach mua.")) {
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (buyList.remove(bookId) != null) {
            helper.redirectWithMessage(req, resp, "msg", "Da xoa sach khoi danh sach can mua.");
        } else {
            helper.redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
        }
    }

    public void updateBuyListQuantity(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc sua danh sach mua.")) {
            return;
        }

        int bookId;
        int quantity;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = helper.parsePositiveInt(req.getParameter("quantity"), "Quantity");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "Du lieu cap nhat so luong khong hop le.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (!buyList.containsKey(bookId)) {
            helper.redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
            return;
        }

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            int available = daoBook.getAvailable(con, bookId);
            if (quantity > available) {
                helper.redirectWithMessage(req, resp, "error", "So luong vuot qua ton kho hien tai.");
                return;
            }

            buyList.put(bookId, quantity);
            helper.redirectWithMessage(req, resp, "msg", "Da cap nhat so luong.");
        } finally {
            con.close();
        }
    }

    public void orderOneFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc gui don mua.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID khong hop le.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        Integer quantity = buyList.get(bookId);
        if (quantity == null || quantity <= 0) {
            helper.redirectWithMessage(req, resp, "error", "Sach khong ton tai trong danh sach can mua.");
            return;
        }

        PurchaseRequestItem item = new PurchaseRequestItem(bookId, quantity);
        try {
            int orderId = transactionService.createPendingOrder(studentId, staff.getStaffID(), List.of(item));
            buyList.remove(bookId);
            helper.redirectToCheckoutSuccess(req, resp, orderId);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void orderAllFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc gui don mua.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien.");
            return;
        }

        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (buyList.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sach can mua dang trong.");
            return;
        }

        List<PurchaseRequestItem> items = new ArrayList<>();
        for (var entry : buyList.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                items.add(new PurchaseRequestItem(entry.getKey(), entry.getValue()));
            }
        }
        if (items.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sach can mua dang trong.");
            return;
        }

        try {
            int orderId = transactionService.createPendingOrder(studentId, staff.getStaffID(), items);
            buyList.clear();
            helper.redirectToCheckoutSuccess(req, resp, orderId);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void requestReturnAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc gui yeu cau tra.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Khong xac dinh duoc ma sinh vien cho tai khoan hien tai.");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BorrowID khong hop le.");
            return;
        }

        if (!daoBorrow.existsOwnedByStudentAndNotReturned(borrowId, studentId)) {
            helper.redirectWithMessage(req, resp, "error", "Khong tim thay phieu muon hop le de yeu cau tra.");
            return;
        }

        helper.redirectWithMessage(req, resp, "msg", "Da gui yeu cau tra sach. Vui long cho staff/admin xac nhan.");
    }

    private boolean ensureStudentOnly(HttpServletRequest req, HttpServletResponse resp, String message) throws IOException {
        if (RoleUtils.isStudentOnly(req)) {
            return true;
        }
        helper.redirectWithMessage(req, resp, "error", message);
        return false;
    }

    private Staff requireLoggedStaff(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            resp.sendRedirect(req.getContextPath() + "/LoginURL");
            return null;
        }
        return staff;
    }
}
