package Controller.borrow;

import Entities.Book;
import Entities.Borrow;
import Entities.Staff;
import Model.DAOStudent;
import Utils.RoleUtils;
import Utils.StudentContextUtils;
import ViewModel.BookPriceRow;
import ViewModel.BorrowRenewalDecision;
import ViewModel.BorrowRow;
import ViewModel.BuyListSnapshot;
import ViewModel.PageSlice;
import ViewModel.StudentBuyListRow;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BorrowHelper {

    public static final int DEFAULT_STUDENT_BORROW_DAYS = 7;
    public static final int STUDENT_RENEWAL_DAYS = 7;
    public static final int STUDENT_RENEWAL_WINDOW_DAYS = 3;
    public static final int MAX_STUDENT_BORROW_DAYS = DEFAULT_STUDENT_BORROW_DAYS + STUDENT_RENEWAL_DAYS;
    public static final int ADMIN_BORROW_PAGE_SIZE = 10;
    public static final int STUDENT_BOOK_PAGE_SIZE = 8;
    public static final int STUDENT_PURCHASE_PAGE_SIZE = 8;

    public static final String PUBLIC_BORROWS_PATH = "/borrows";
    public static final String ADMIN_BORROWS_PATH = "/admin/borrows";
    public static final String BUY_LIST_SESSION_KEY = "studentBuyList";
    public static final String BORROW_CART_SESSION_KEY = "borrowCart";

    private final DAOStudent daoStudent;

    public BorrowHelper(DAOStudent daoStudent) {
        this.daoStudent = daoStudent;
    }

    // ========== ROUTING ==========
    public boolean shouldRedirectToAdminRoute(HttpServletRequest req) {
        return PUBLIC_BORROWS_PATH.equals(req.getServletPath()) && !RoleUtils.isStudentOnly(req);
    }

    public boolean isAdminSection(HttpServletRequest req) {
        return ADMIN_BORROWS_PATH.equals(req.getServletPath());
    }

    public boolean canAccessAdminSection(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    public String getListPath(HttpServletRequest req) {
        return isAdminSection(req) ? ADMIN_BORROWS_PATH : PUBLIC_BORROWS_PATH;
    }

    public void redirectWithMessage(HttpServletRequest req, HttpServletResponse resp, String key, String value) throws IOException {
        redirectWithMessageAndAnchor(req, resp, key, value, null);
    }

    public void redirectWithMessageAndAnchor(HttpServletRequest req, HttpServletResponse resp,
            String key, String value, String anchor) throws IOException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        String safeAnchor = anchor == null || anchor.trim().isEmpty() ? "" : "#" + anchor.trim();
        resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&" + key + "=" + encoded + safeAnchor);
    }

    public void redirectToHome(HttpServletRequest req, HttpServletResponse resp,
            String key, String value) throws IOException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8);
        resp.sendRedirect(req.getContextPath() + "/home?" + key + "=" + encoded);
    }

    public void redirectToCheckoutSuccess(HttpServletRequest req, HttpServletResponse resp,
            int orderId) throws IOException {
        resp.sendRedirect(req.getContextPath() + PUBLIC_BORROWS_PATH
                + "?action=checkoutSuccess&orderID=" + orderId);
    }

    // ========== PARSING ==========
    public int parsePositiveInt(String raw, String fieldName) {
        int value = Integer.parseInt(raw);
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " phai > 0");
        }
        return value;
    }

    public int parsePage(String raw, int defaultPage) {
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

    public String trim(String value) {
        return value == null ? "" : value.trim();
    }

    // ========== PAGINATION ==========
    public <T> PageSlice<T> paginate(List<T> source, int requestedPage, int pageSize) {
        if (source == null) {
            source = Collections.emptyList();
        }
        int totalItems = source.size();
        int safePageSize = pageSize <= 0 ? 1 : pageSize;
        int totalPages = Math.max(1, (int) Math.ceil(totalItems / (double) safePageSize));
        int currentPage = Math.min(Math.max(1, requestedPage), totalPages);
        int fromIndex = Math.min((currentPage - 1) * safePageSize, totalItems);
        int toIndex = Math.min(fromIndex + safePageSize, totalItems);
        List<T> pageItems = source.subList(fromIndex, toIndex);
        return new PageSlice<>(pageItems, currentPage, totalPages, totalItems);
    }

    // ========== STUDENT ID RESOLUTION ==========
    // Trong BorrowHelper hoặc StudentContextUtils:
    public Integer resolveStudentIdForStaff(Staff staff) throws SQLException {
        if (staff == null) {
            return null;
        }
        // StudentID chính là StaffID — không cần lookup bảng Student
        return StudentContextUtils.resolveStudentId(staff, daoStudent);
    }

    public BorrowRenewalDecision evaluateRenewal(Borrow borrow) {
        return evaluateRenewal(borrow, LocalDate.now());
    }

    public BorrowRenewalDecision evaluateRenewal(Borrow borrow, LocalDate today) {
        if (borrow == null) {
            return BorrowRenewalDecision.ineligible("Khong tim thay phieu muon.");
        }
        return evaluateRenewal(borrow.getBorrowDate(), borrow.getDueDate(), borrow.getStatus(), today);
    }

    public BorrowRenewalDecision evaluateRenewal(BorrowRow row, LocalDate today) {
        if (row == null) {
            return BorrowRenewalDecision.ineligible("Khong tim thay phieu muon.");
        }
        return evaluateRenewal(row.getBorrowDate(), row.getDueDate(), row.getStatus(), today);
    }

    private BorrowRenewalDecision evaluateRenewal(String borrowDateRaw, String dueDateRaw, String status, LocalDate today) {
        LocalDate borrowDate = parseIsoDate(borrowDateRaw);
        LocalDate dueDate = parseIsoDate(dueDateRaw);

        if (borrowDate == null || dueDate == null) {
            return BorrowRenewalDecision.ineligible("Khong the doc ngay muon hoac han tra.");
        }

        if (!"Borrowing".equalsIgnoreCase(trim(status))) {
            if ("Overdue".equalsIgnoreCase(trim(status)) || today.isAfter(dueDate)) {
                return BorrowRenewalDecision.ineligible("Phieu da qua han nen khong the gia han online.");
            }
            if ("Returned".equalsIgnoreCase(trim(status))) {
                return BorrowRenewalDecision.ineligible("Phieu da hoan tat nen khong the gia han.");
            }
            return BorrowRenewalDecision.ineligible("Chi phieu dang muon moi duoc gia han.");
        }

        if (today.isAfter(dueDate)) {
            return BorrowRenewalDecision.ineligible("Phieu da qua han nen khong the gia han online.");
        }

        if (dueDate.isAfter(today.plusDays(STUDENT_RENEWAL_WINDOW_DAYS))) {
            return BorrowRenewalDecision.ineligible(
                    "Chi co the gia han trong " + STUDENT_RENEWAL_WINDOW_DAYS + " ngay cuoi truoc han tra.");
        }

        LocalDate maxDueDate = borrowDate.plusDays(MAX_STUDENT_BORROW_DAYS);
        if (!dueDate.isBefore(maxDueDate)) {
            return BorrowRenewalDecision.ineligible("Phieu nay da dung het luot gia han.");
        }

        LocalDate nextDueDate = dueDate.plusDays(STUDENT_RENEWAL_DAYS);
        if (nextDueDate.isAfter(maxDueDate)) {
            nextDueDate = maxDueDate;
        }

        if (!nextDueDate.isAfter(dueDate)) {
            return BorrowRenewalDecision.ineligible("Khong con ngay gia han hop le cho phieu nay.");
        }

        return BorrowRenewalDecision.eligible(nextDueDate,
                "Gia han them " + STUDENT_RENEWAL_DAYS + " ngay den " + nextDueDate + ".");
    }

    private LocalDate parseIsoDate(String raw) {
        String value = trim(raw);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Book> filterBorrowableBooks(List<Book> allBooks) {
        List<Book> available = new ArrayList<>();
        for (Book book : allBooks == null ? Collections.<Book>emptyList() : allBooks) {
            if (book.getAvailable() > 0) {
                available.add(book);
            }
        }
        return available;
    }

    public List<Book> filterBooksByKeyword(List<Book> books, String keyword) {
        String normalized = trim(keyword).toLowerCase();
        if (normalized.isEmpty()) {
            return books;
        }
        List<Book> filtered = new ArrayList<>();
        for (Book book : books) {
            if (String.valueOf(book.getBookID()).contains(normalized)
                    || (book.getBookName() != null && book.getBookName().toLowerCase().contains(normalized))) {
                filtered.add(book);
            }
        }
        return filtered;
    }

    // ========== BORROW CART (SESSION-BASED, MAX 3 BOOKS) ==========
    /**
     * Get or create the borrow cart from session. Cart is a List of BookIDs
     * (max 3 items).
     */
    @SuppressWarnings("unchecked")
    public List<Integer> getOrCreateBorrowCart(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object raw = session.getAttribute(BORROW_CART_SESSION_KEY);
        if (raw instanceof List<?>) {
            return (List<Integer>) raw;
        }
        List<Integer> cart = new ArrayList<>();
        session.setAttribute(BORROW_CART_SESSION_KEY, cart);
        return cart;
    }

    /**
     * Add a book to borrow cart. Returns error message or null if success.
     */
    public String addToBorrowCart(HttpServletRequest req, int bookId) {
        List<Integer> cart = getOrCreateBorrowCart(req);
        if (cart.size() >= BorrowValidator.MAX_CART_SIZE) {
            return "Gio muon da day (" + BorrowValidator.MAX_CART_SIZE + " quyen). "
                    + "Gui yeu cau muon hoac xoa bot sach.";
        }
        if (cart.contains(bookId)) {
            return "Sach nay da co trong gio muon.";
        }
        cart.add(bookId);
        return null; // success
    }

    /**
     * Remove a book from borrow cart.
     */
    public boolean removeFromBorrowCart(HttpServletRequest req, int bookId) {
        List<Integer> cart = getOrCreateBorrowCart(req);
        return cart.remove(Integer.valueOf(bookId));
    }

    /**
     * Clear the borrow cart.
     */
    public void clearBorrowCart(HttpServletRequest req) {
        getOrCreateBorrowCart(req).clear();
    }

    /**
     * Get cart size.
     */
    public int getBorrowCartSize(HttpServletRequest req) {
        return getOrCreateBorrowCart(req).size();
    }

    /**
     * Build cart items with book details for display.
     */
    public List<Book> getBorrowCartBooks(HttpServletRequest req, Map<Integer, Book> bookMap) {
        List<Integer> cart = getOrCreateBorrowCart(req);
        List<Book> books = new ArrayList<>();
        for (Integer bookId : cart) {
            Book book = bookMap.get(bookId);
            if (book != null) {
                books.add(book);
            }
        }
        return books;
    }

    // ========== BUY LIST (EXISTING, UNCHANGED) ==========
    @SuppressWarnings("unchecked")
    public LinkedHashMap<Integer, Integer> getOrCreateBuyList(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object raw = session.getAttribute(BUY_LIST_SESSION_KEY);
        if (raw instanceof LinkedHashMap<?, ?>) {
            return (LinkedHashMap<Integer, Integer>) raw;
        }
        LinkedHashMap<Integer, Integer> created = new LinkedHashMap<>();
        session.setAttribute(BUY_LIST_SESSION_KEY, created);
        return created;
    }

    public BuyListSnapshot buildBuyListSnapshot(HttpServletRequest req, List<Book> allBooks,
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
            rows.add(new StudentBuyListRow(bookId, bookName, quantity, available,
                    unitPrice, currency, lineTotal, canOrder));
        }
        return new BuyListSnapshot(rows, totalAmount);
    }

    // ========== PRIVATE ==========
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
        try {
            return Integer.parseInt(value.substring(index + 1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
