package Controller.borrow;

import Entities.Book;
import Entities.Orders;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBookHold;
import Model.DAOBookPrice;
import Model.DAOBorrow;
import Model.DAOFine;
import Model.DAOOrderDetail;
import Model.DAOOrders;
import Utils.RoleUtils;
import ViewModel.BookPriceRow;
import ViewModel.BorrowRenewalDecision;
import ViewModel.BorrowRow;
import ViewModel.BuyListSnapshot;
import ViewModel.HoldRow;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import Model.DBConnection;
import Utils.NotificationBroadcaster;

public class BorrowStudentHandler {

    private final DAOBook daoBook;
    private final DAOBorrow daoBorrow;
    private final DAOOrders daoOrders;
    private final DAOOrderDetail daoOrderDetail;
    private final DAOBookPrice daoBookPrice;
    private final DAOFine daoFine;
    private final DAOBookHold daoBookHold;
    private final BorrowHelper helper;
    private final BorrowTransactionService transactionService;
    private final BorrowValidator validator;

    public BorrowStudentHandler(DAOBook daoBook, DAOBorrow daoBorrow, DAOOrders daoOrders,
            DAOOrderDetail daoOrderDetail, DAOBookPrice daoBookPrice,
            BorrowHelper helper, BorrowTransactionService transactionService) {
        this.daoBook = daoBook;
        this.daoBorrow = daoBorrow;
        this.daoOrders = daoOrders;
        this.daoOrderDetail = daoOrderDetail;
        this.daoBookPrice = daoBookPrice;
        this.daoFine = new DAOFine();
        this.daoBookHold = new DAOBookHold();
        this.helper = helper;
        this.transactionService = transactionService;
        this.validator = new BorrowValidator(daoBorrow, daoFine);
    }

    // ========== SHOW LIST ==========
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
            req.setAttribute("renewalDecisionByBorrowId", Collections.emptyMap());
            req.setAttribute("renewableBorrowCount", 0);
            req.setAttribute("studentRenewalDays", BorrowHelper.STUDENT_RENEWAL_DAYS);
            req.setAttribute("studentRenewalWindowDays", BorrowHelper.STUDENT_RENEWAL_WINDOW_DAYS);
            req.setAttribute("buyListItems", Collections.emptyList());
            req.setAttribute("purchasedOrders", Collections.emptyList());
            req.setAttribute("bookCurrentPage", 1);
            req.setAttribute("bookTotalPages", 1);
            req.setAttribute("purchaseCurrentPage", 1);
            req.setAttribute("purchaseTotalPages", 1);
            req.setAttribute("bookSearch", "");
            req.setAttribute("purchaseSearch", "");
            req.setAttribute("buyListTotal", 0.0d);
            req.getRequestDispatcher("/WEB-INF/views/client/borrow/student.jsp").forward(req, resp);
            return;
        }

        String bookSearch = helper.trim(req.getParameter("bookSearch"));
        String purchaseSearch = helper.trim(req.getParameter("purchaseSearch"));
        int bookPage = helper.parsePage(req.getParameter("bookPage"), 1);
        int purchasePage = helper.parsePage(req.getParameter("purchasePage"), 1);

        List<Book> allBooks = daoBook.getAll();
        List<Book> availableBooks = helper.filterBooksByKeyword(
                helper.filterBorrowableBooks(allBooks), bookSearch);
        PageSlice<Book> availablePage = helper.paginate(availableBooks, bookPage,
                BorrowHelper.STUDENT_BOOK_PAGE_SIZE);

        List<BookPriceRow> bookPrices = daoBookPrice.getBookPriceRows();
        BuyListSnapshot buyListSnapshot = helper.buildBuyListSnapshot(req, allBooks, bookPrices);

        List<OrderRow> purchasedOrders = daoOrders.getOrderRows(studentId, purchaseSearch, "Approved");
        PageSlice<OrderRow> purchasePageSlice = helper.paginate(purchasedOrders, purchasePage,
                BorrowHelper.STUDENT_PURCHASE_PAGE_SIZE);

        // Eligibility + cart
        BorrowValidator.BorrowEligibility eligibility = validator.getEligibility(studentId);
        Map<Integer, Book> bookMap = new HashMap<>();
        for (Book b : allBooks) {
            bookMap.put(b.getBookID(), b);
        }
        List<Book> cartBooks = helper.getBorrowCartBooks(req, bookMap);

        // Student's active holds
        List<HoldRow> studentHolds = daoBookHold.getActiveByStudentId(studentId);

        req.setAttribute("studentId", studentId);
        req.setAttribute("eligibility", eligibility);
        req.setAttribute("borrowCart", cartBooks);
        req.setAttribute("borrowCartSize", cartBooks.size());
        req.setAttribute("studentHolds", studentHolds);

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

        List<BorrowRow> borrows = daoBorrow.getBorrowRowsByStudent(studentId);
        Map<Integer, BorrowRenewalDecision> renewalDecisionByBorrowId = new HashMap<>();
        int renewableBorrowCount = 0;
        LocalDate today = LocalDate.now();
        for (BorrowRow borrow : borrows) {
            BorrowRenewalDecision renewalDecision = helper.evaluateRenewal(borrow, today);
            renewalDecisionByBorrowId.put(borrow.getBorrowID(), renewalDecision);
            if (renewalDecision.isEligible()) {
                renewableBorrowCount++;
            }
        }

        req.setAttribute("renewalDecisionByBorrowId", renewalDecisionByBorrowId);
        req.setAttribute("renewableBorrowCount", renewableBorrowCount);
        req.setAttribute("studentRenewalDays", BorrowHelper.STUDENT_RENEWAL_DAYS);
        req.setAttribute("studentRenewalWindowDays", BorrowHelper.STUDENT_RENEWAL_WINDOW_DAYS);
        req.setAttribute("borrows", borrows);
        req.getRequestDispatcher("/WEB-INF/views/client/borrow/student.jsp").forward(req, resp);
    }

    // ========== BORROW CART ACTIONS ==========
    public void addToBorrowCart(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản sinh viên mới được thêm vào giỏ mượn.")) {
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectToHome(req, resp, "error", "BookID không hợp lệ.");
            return;
        }

        Book book = daoBook.getById(bookId);
        if (book == null || book.getAvailable() <= 0) {
            helper.redirectToHome(req, resp, "error", "Sách không có sẵn để mượn.");
            return;
        }

        // CHECK: Nếu sách có người hold Notified và student hiện tại KHÔNG phải người hold
        // → không cho mượn, phải đợi hold expire
        Staff staff = RoleUtils.getLoggedStaff(req);
        Integer studentId = (staff != null) ? staff.getStaffID() : null;
        if (studentId != null && daoBookHold.hasNotifiedHoldByOther(bookId, studentId)) {
            helper.redirectToHome(req, resp, "error",
                    "Sách này đang được giữ cho người đặt trước. Vui lòng đặt giữ chỗ và chờ.");
            return;
        }

        String error = helper.addToBorrowCart(req, bookId);
        if (error != null) {
            helper.redirectToHome(req, resp, "error", error);
        } else {
            helper.redirectToHome(req, resp, "msg", "Đã thêm \"" + book.getBookName() + "\" vào giỏ mượn.");
        }
    }

    public void removeFromBorrowCart(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản sinh viên.")) {
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectToHome(req, resp, "error", "BookID không hợp lệ.");
            return;
        }

        if (helper.removeFromBorrowCart(req, bookId)) {
            helper.redirectToHome(req, resp, "msg", "Đã xóa sách khỏi giỏ mượn.");
        } else {
            helper.redirectToHome(req, resp, "error", "Sách không có trong giỏ mượn.");
        }
    }

    public void submitBorrowRequest(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản sinh viên mới được gửi yêu cầu mượn.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectToHome(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }

        List<Integer> cart = helper.getOrCreateBorrowCart(req);
        if (cart.isEmpty()) {
            helper.redirectToHome(req, resp, "error", "Giỏ mượn đang trống. Vui lòng thêm sách.");
            return;
        }

        List<String> errors = validator.validateBorrowEligibility(studentId, cart.size());
        if (!errors.isEmpty()) {
            helper.redirectToHome(req, resp, "error", String.join(" | ", errors));
            return;
        }

        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(BorrowHelper.DEFAULT_STUDENT_BORROW_DAYS);

        try {
            int size = cart.size();
            int borrowId = transactionService.createPendingBorrow(
                    studentId, staff.getStaffID(), cart, borrowDate, dueDate);
            helper.clearBorrowCart(req);
            NotificationBroadcaster.notifyAdminNewBorrow(borrowId, staff.getStaffName(), size);
            helper.redirectToHome(req, resp, "msg",
                    "Đã gửi yêu cầu mượn " + size + " quyển sách (Phiếu #" + borrowId
                    + "). Hạn trả dự kiến: " + dueDate + ". Vui lòng chờ admin duyệt.");
        } catch (SQLException e) {
            helper.redirectToHome(req, resp, "error", e.getMessage());
        }
    }

    // ========== HOLD ACTIONS (NEW) ==========
    /**
     * Student places a hold on an out-of-stock book.
     */
    public void placeHold(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản sinh viên mới được đặt giữ chỗ.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectToHome(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }

        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectToHome(req, resp, "error", "BookID không hợp lệ.");
            return;
        }

        Book book = daoBook.getById(bookId);
        if (book == null) {
            helper.redirectToHome(req, resp, "error", "Sách không tồn tại.");
            return;
        }

        // If book is available, redirect to add to cart instead
        if (book.getAvailable() > 0) {
            helper.redirectToHome(req, resp, "error", "Sách vẫn còn có sẵn. Hãy thêm vào giỏ mượn thay vì đặt giữ.");
            return;
        }

        // Check if already has active hold
        if (daoBookHold.hasActiveHold(studentId, bookId)) {
            helper.redirectToHome(req, resp, "error", "Bạn đã đặt giữ sách này rồi. Vui lòng chờ thông báo.");
            return;
        }

        // Check business rules
        List<String> eligibilityErrors = validator.validateBorrowEligibility(studentId, 0);
        if (!eligibilityErrors.isEmpty()) {
            helper.redirectToHome(req, resp, "error",
                    "Không thể đặt giữ: " + String.join(" | ", eligibilityErrors));
            return;
        }

        int holdId = daoBookHold.insert(studentId, bookId);
        NotificationBroadcaster.notifyAdminNewHold(staff.getStaffName(), book.getBookName());
        int queuePosition = daoBookHold.countWaiting(bookId);
        helper.redirectToHome(req, resp, "msg",
                "Đã đặt giữ chỗ sách \"" + book.getBookName() + "\" thành công. "
                + "Vị trí hàng chờ: #" + queuePosition + ". "
                + "Bạn sẽ nhận email khi sách có sẵn.");
    }

    /**
     * Student cancels their hold.
     */
    public void cancelHold(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản sinh viên.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectToHome(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }

        int holdId;
        try {
            holdId = helper.parsePositiveInt(req.getParameter("holdID"), "HoldID");
        } catch (Exception e) {
            helper.redirectToHome(req, resp, "error", "HoldID không hợp lệ.");
            return;
        }

        int affected = daoBookHold.cancel(holdId, studentId);
        if (affected > 0) {
            helper.redirectToHome(req, resp, "msg", "Đã hủy đặt giữ chỗ thành công.");
        } else {
            helper.redirectToHome(req, resp, "error", "Không tìm thấy yêu cầu giữ chỗ hoặc đã hết hiệu lực.");
        }
    }

    // ========== BACKWARD COMPAT ==========
    public void borrowAsStudent(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        addToBorrowCart(req, resp);
    }

    // ========== CHECKOUT (giữ nguyên) ==========
    public void showCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh mới được checkout.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }

        List<Book> allBooks = daoBook.getAll();
        List<BookPriceRow> bookPrices = daoBookPrice.getBookPriceRows();
        BuyListSnapshot buyListSnapshot = helper.buildBuyListSnapshot(req, allBooks, bookPrices);
        List<StudentBuyListRow> checkoutItems = buyListSnapshot.getItems();
        if (checkoutItems.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sách cần mua đang trống.");
            return;
        }

        int invalidCount = 0, totalQuantity = 0;
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
        req.getRequestDispatcher("/WEB-INF/views/client/borrow/checkout.jsp").forward(req, resp);
    }

    public void showCheckoutSuccess(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, ServletException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh mới được xem kết quả.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }

        int orderId;
        try {
            orderId = helper.parsePositiveInt(req.getParameter("orderID"), "OrderID");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "OrderID không hợp lệ.");
            return;
        }

        Orders order = daoOrders.getById(orderId);
        if (order == null || order.getStudentID() != studentId) {
            helper.redirectWithMessage(req, resp, "error", "Không tìm thấy đơn hàng.");
            return;
        }

        List<OrderItemRow> orderItems = daoOrderDetail.getOrderItemsWithBookName(orderId);
        if (orderItems.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Đơn hàng không có chi tiết.");
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
        req.getRequestDispatcher("/WEB-INF/views/client/borrow/checkout-success.jsp").forward(req, resp);
    }

    // ========== BUY LIST (giữ nguyên) ==========
    public void buyBookAsStudent(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh mới được mua sách.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }
        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID không hợp lệ.");
            return;
        }
        try {
            int orderId = transactionService.createPendingOrder(studentId, staff.getStaffID(), List.of(new PurchaseRequestItem(bookId, 1)));
            helper.redirectToCheckoutSuccess(req, resp, orderId);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void addToBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh mới được thêm vào danh sách mua.")) {
            return;
        }
        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID không hợp lệ.");
            return;
        }
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            int available = daoBook.getAvailable(con, bookId);
            if (available <= 0) {
                helper.redirectWithMessage(req, resp, "error", "Sách đã hết hàng.");
                return;
            }
            double unitPrice = daoBookPrice.getCurrentSellingPrice(con, bookId);
            if (unitPrice <= 0) {
                helper.redirectWithMessage(req, resp, "error", "Sách chưa có giá bán hợp lệ.");
                return;
            }
            LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
            int currentQty = buyList.getOrDefault(bookId, 0);
            if (currentQty + 1 > available) {
                helper.redirectWithMessage(req, resp, "error", "Số lượng vượt quá tồn kho.");
                return;
            }
            buyList.put(bookId, currentQty + 1);
            helper.redirectWithMessage(req, resp, "msg", "Đã thêm sách vào danh sách cần mua.");
        } finally {
            con.close();
        }
    }

    public void removeFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh.")) {
            return;
        }
        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID không hợp lệ.");
            return;
        }
        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (buyList.remove(bookId) != null) {
            helper.redirectWithMessage(req, resp, "msg", "Đã xóa sách khỏi danh sách.");
        } else {
            helper.redirectWithMessage(req, resp, "error", "Sách không có trong danh sách.");
        }
    }

    public void updateBuyListQuantity(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc sua danh sach mua.")) {
            return;
        }
        int bookId, quantity;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
            quantity = helper.parsePositiveInt(req.getParameter("quantity"), "Quantity");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "Dữ liệu không hợp lệ.");
            return;
        }
        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (!buyList.containsKey(bookId)) {
            helper.redirectWithMessage(req, resp, "error", "Sách không có trong danh sách.");
            return;
        }
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }
        try {
            int available = daoBook.getAvailable(con, bookId);
            if (quantity > available) {
                helper.redirectWithMessage(req, resp, "error", "Số lượng vượt quá tồn kho.");
                return;
            }
            buyList.put(bookId, quantity);
            helper.redirectWithMessage(req, resp, "msg", "Đã cập nhật số lượng.");
        } finally {
            con.close();
        }
    }

    public void orderOneFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }
        int bookId;
        try {
            bookId = helper.parsePositiveInt(req.getParameter("bookID"), "Book");
        } catch (Exception e) {
            helper.redirectWithMessage(req, resp, "error", "BookID không hợp lệ.");
            return;
        }
        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        Integer quantity = buyList.get(bookId);
        if (quantity == null || quantity <= 0) {
            helper.redirectWithMessage(req, resp, "error", "Sách không có trong danh sách.");
            return;
        }
        try {
            int orderId = transactionService.createPendingOrder(studentId, staff.getStaffID(), List.of(new PurchaseRequestItem(bookId, quantity)));
            buyList.remove(bookId);
            helper.redirectToCheckoutSuccess(req, resp, orderId);
        } catch (SQLException e) {
            helper.redirectWithMessage(req, resp, "error", e.getMessage());
        }
    }

    public void orderAllFromBuyList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chỉ tài khoản học sinh.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }
        LinkedHashMap<Integer, Integer> buyList = helper.getOrCreateBuyList(req);
        if (buyList.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sách cần mua đang trống.");
            return;
        }
        List<PurchaseRequestItem> items = new ArrayList<>();
        for (var entry : buyList.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > 0) {
                items.add(new PurchaseRequestItem(entry.getKey(), entry.getValue()));
            }
        }
        if (items.isEmpty()) {
            helper.redirectWithMessage(req, resp, "error", "Danh sách cần mua đang trống.");
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

    public void requestReturnAsStudent(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc gui yeu cau tra.")) {
            return;
        }
        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }
        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessage(req, resp, "error", "Không xác định được mã sinh viên.");
            return;
        }
        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessageAndAnchor(req, resp, "error", "BorrowID khong hop le.", "borrow-panel");
            return;
        }
        if (!daoBorrow.existsOwnedByStudentAndNotReturned(borrowId, studentId)) {
            helper.redirectWithMessageAndAnchor(req, resp, "error", "Khong tim thay phieu muon hop le de yeu cau tra.",
                    "borrow-panel");
            return;
        }

        helper.redirectWithMessageAndAnchor(req, resp, "msg",
                "Da gui yeu cau tra sach. Vui long cho staff/admin xac nhan.", "borrow-panel");
    }

    public void renewBorrowAsStudent(HttpServletRequest req, HttpServletResponse resp)
            throws SQLException, IOException {
        if (!ensureStudentOnly(req, resp, "Chi tai khoan hoc sinh moi duoc gia han online.")) {
            return;
        }

        Staff staff = requireLoggedStaff(req, resp);
        if (staff == null) {
            return;
        }

        Integer studentId = helper.resolveStudentIdForStaff(staff);
        if (studentId == null) {
            helper.redirectWithMessageAndAnchor(req, resp, "error",
                    "Khong xac dinh duoc ma sinh vien cho tai khoan hien tai.", "borrow-panel");
            return;
        }

        int borrowId;
        try {
            borrowId = helper.parsePositiveInt(req.getParameter("borrowID"), "BorrowID");
        } catch (Exception e) {
            helper.redirectWithMessageAndAnchor(req, resp, "error", "BorrowID khong hop le.", "borrow-panel");
            return;
        }

        try {
            BorrowRenewalDecision decision = transactionService.renewBorrowTransaction(borrowId, studentId);
            helper.redirectWithMessageAndAnchor(req, resp, "msg",
                    "Da gia han phieu #" + borrowId + " den " + decision.getNextDueDateLabel() + ".", "borrow-panel");
        } catch (SQLException e) {
            helper.redirectWithMessageAndAnchor(req, resp, "error", e.getMessage(), "borrow-panel");
        }
    }

    private boolean ensureStudentOnly(HttpServletRequest req, HttpServletResponse resp, String message)
            throws IOException {
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
        }
        return staff;
    }

    private void setEmptyAttributes(HttpServletRequest req) {
        req.setAttribute("availableBooks", Collections.emptyList());
        req.setAttribute("bookPrices", Collections.emptyList());
        req.setAttribute("borrows", Collections.emptyList());
        req.setAttribute("buyListItems", Collections.emptyList());
        req.setAttribute("purchasedOrders", Collections.emptyList());
        req.setAttribute("borrowCart", Collections.emptyList());
        req.setAttribute("borrowCartSize", 0);
        req.setAttribute("studentHolds", Collections.emptyList());
        req.setAttribute("bookCurrentPage", 1);
        req.setAttribute("bookTotalPages", 1);
        req.setAttribute("purchaseCurrentPage", 1);
        req.setAttribute("purchaseTotalPages", 1);
        req.setAttribute("bookSearch", "");
        req.setAttribute("purchaseSearch", "");
        req.setAttribute("buyListTotal", 0.0d);
    }
}
