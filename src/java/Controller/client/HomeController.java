package Controller.client;

import Controller.borrow.BorrowHelper;
import Controller.borrow.BorrowValidator;
import Entities.Book;
import Entities.Borrow;
import Entities.Category;
import Entities.Publisher;
import Entities.Student;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOCategory;
import Model.DAOFine;
import Model.DAOOrders;
import Model.DAOPublisher;
import Utils.RoleUtils;
import Utils.StudentContextUtils;
import ViewModel.OrderRow;
import ViewModel.PageSlice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

@WebServlet(name = "StudentHome", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private static final int PAGE_SIZE = 12;
    private static final int DUE_SOON_WINDOW_DAYS = 3;
    private static final String STATUS_ORDER_WAITING_VI = "h\u00e0ng ch\u1edd";
    private static final String STATUS_ORDER_READY_VI = "s\u1eb5n s\u00e0ng";
    private static final String STATUS_ORDER_CANCELLED_VI = "\u0111\u00e3 h\u1ee7y";

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCategory = new DAOCategory();
    private final DAOPublisher daoPublisher = new DAOPublisher();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOFine daoFine = new DAOFine();
    private final BorrowHelper borrowHelper = new BorrowHelper(daoStudent);
    private final BorrowValidator validator = new BorrowValidator(daoBorrow, daoFine);
    private final DAOOrders daoOrders = new DAOOrders();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        // Cache studentId trong session — không query DB mỗi request
        Staff staff = RoleUtils.getLoggedStaff(request);
        Integer studentId = resolveAndCacheStudentId(request, staff);

        String search = trim(request.getParameter("search"));
        String letter = trim(request.getParameter("letter"));
        String categoryIdRaw = trim(request.getParameter("categoryId"));
        String publisherIdRaw = trim(request.getParameter("publisherId"));
        String author = trim(request.getParameter("author"));
        int requestedPage = parsePage(request.getParameter("page"), 1);

        Integer categoryId = parseNullableInt(categoryIdRaw);
        Integer publisherId = parseNullableInt(publisherIdRaw);
        if (letter.isEmpty()) {
            letter = "ALL";
        }

        try {
            // DB queries: getFiltered + getAll categories + getAll publishers = 3 queries
            Student currentStudent = StudentContextUtils.resolveCurrentStudent(request);
            List<Book> books = daoBook.getFiltered(search, letter, categoryId, publisherId, author);
            PageSlice<Book> pageSlice = paginate(books, requestedPage, PAGE_SIZE);
            List<Category> categories = daoCategory.getAll();
            List<Publisher> publishers = daoPublisher.getAll();
            List<Borrow> holds = resolveActiveBorrows(currentStudent);
            List<OrderRow> studentOrders = resolveOrders(currentStudent);

            // Student-specific: 2 queries (active borrows + eligibility)
            List<Borrow> holds = Collections.emptyList();
            BorrowValidator.BorrowEligibility eligibility = null;
            if (studentId != null) {
                holds = daoBorrow.getActiveByStudentId(studentId);
                eligibility = validator.getEligibility(studentId);
                request.setAttribute("studentId", studentId);
            }

            // Cart data — chỉ đọc session, không query DB
            Map<Integer, Book> bookMap = new HashMap<>();
            for (Book b : books) {
                bookMap.put(b.getBookID(), b);
            }
            List<Book> cartBooks = borrowHelper.getBorrowCartBooks(request, bookMap);
            int cartSize = borrowHelper.getBorrowCartSize(request);
            List<Integer> cartBookIds = borrowHelper.getOrCreateBorrowCart(request);

            int overdueCount = countOverdueBorrows(holds);
            int dueSoonCount = countDueSoonBorrows(holds);
            int orderCount = countActiveOrders(studentOrders);
            int pendingOrderCount = countPendingOrders(studentOrders);

            // Set all attributes
            request.setAttribute("books", pageSlice.getItems());
            request.setAttribute("categories", categories);
            request.setAttribute("publishers", publishers);
            request.setAttribute("holds", holds);
            request.setAttribute("currentStudent", currentStudent);
            request.setAttribute("search", search);
            request.setAttribute("letter", letter);
            request.setAttribute("letters", buildLetters());
            request.setAttribute("categoryId", categoryIdRaw);
            request.setAttribute("selectedCategoryId", categoryId);
            request.setAttribute("publisherId", publisherIdRaw);
            request.setAttribute("selectedPublisherId", publisherId);
            request.setAttribute("author", author);
            request.setAttribute("currentPage", pageSlice.getPage());
            request.setAttribute("totalPages", pageSlice.getTotalPages());
            request.setAttribute("totalBooks", pageSlice.getTotalItems());
            request.setAttribute("borrowCart", cartBooks);
            request.setAttribute("borrowCartSize", cartSize);
            request.setAttribute("borrowCartIds", cartBookIds);
            request.setAttribute("eligibility", eligibility);
            request.setAttribute("maxCartSize", BorrowValidator.MAX_CART_SIZE);
            request.setAttribute("studentBorrowingCount", holds.size());
            request.setAttribute("studentDueSoonCount", dueSoonCount);
            request.setAttribute("studentOrderCount", orderCount);
            request.setAttribute("studentOverdueCount", overdueCount);
            request.setAttribute("studentPendingOrderCount", pendingOrderCount);
            request.setAttribute("studentHasViolation", overdueCount > 0);
            request.setAttribute("studentDueSoonWindowDays", DUE_SOON_WINDOW_DAYS);

            request.getRequestDispatcher("/WEB-INF/views/client/home/index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Cache studentId trong session. Chỉ query DB lần đầu.
     */
    private Integer resolveAndCacheStudentId(HttpServletRequest request, Staff staff) throws ServletException {
        if (staff == null) {
            return null;
        }
        HttpSession session = request.getSession();
        Integer cached = (Integer) session.getAttribute("cachedStudentId");
        if (cached != null) {
            return cached;
        }
        try {
            Integer studentId = borrowHelper.resolveStudentIdForStaff(staff);
            if (studentId != null) {
                session.setAttribute("cachedStudentId", studentId);
            }
            return studentId;
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private List<Borrow> resolveActiveBorrows(Student currentStudent) throws SQLException {
        if (currentStudent == null) {
            return Collections.emptyList();
        }
        return daoBorrow.getActiveByStudentId(currentStudent.getStudentID());
    }

    private List<OrderRow> resolveOrders(Student currentStudent) throws SQLException {
        if (currentStudent == null) {
            return Collections.emptyList();
        }
        return daoOrders.getOrderRowsByStudent(currentStudent.getStudentID());
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> buildLetters() {
        List<String> letters = new ArrayList<>(26);
        for (char c = 'A'; c <= 'Z'; c++) {
            letters.add(String.valueOf(c));
        }
        return letters;
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

    private int countDueSoonBorrows(List<Borrow> holds) {
        int count = 0;
        LocalDate today = LocalDate.now();
        LocalDate dueSoonDate = today.plusDays(DUE_SOON_WINDOW_DAYS);
        for (Borrow hold : holds) {
            LocalDate dueDate = parseDate(hold.getDueDate());
            if (dueDate == null) {
                continue;
            }
            if (isOverdueBorrow(hold, dueDate, today)) {
                continue;
            }
            if (!dueDate.isAfter(dueSoonDate)) {
                count++;
            }
        }
        return count;
    }

    private int countOverdueBorrows(List<Borrow> holds) {
        int count = 0;
        LocalDate today = LocalDate.now();
        for (Borrow hold : holds) {
            LocalDate dueDate = parseDate(hold.getDueDate());
            if (isOverdueBorrow(hold, dueDate, today)) {
                count++;
            }
        }
        return count;
    }

    private int countActiveOrders(List<OrderRow> orders) {
        int count = 0;
        for (OrderRow order : orders) {
            if (!isCancelledOrder(order.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private int countPendingOrders(List<OrderRow> orders) {
        int count = 0;
        for (OrderRow order : orders) {
            if (isPendingOrder(order.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean isPendingOrder(String status) {
        String normalized = trim(status).toLowerCase(Locale.ROOT);
        return "pending".equals(normalized)
                || STATUS_ORDER_WAITING_VI.equals(normalized)
                || STATUS_ORDER_READY_VI.equals(normalized);
    }

    private boolean isCancelledOrder(String status) {
        String normalized = trim(status).toLowerCase(Locale.ROOT);
        return "rejected".equals(normalized) || STATUS_ORDER_CANCELLED_VI.equals(normalized);
    }

    private boolean isOverdueBorrow(Borrow hold, LocalDate dueDate, LocalDate today) {
        return "overdue".equalsIgnoreCase(trim(hold.getStatus()))
                || (dueDate != null && dueDate.isBefore(today));
    }

    private LocalDate parseDate(String value) {
        String normalized = trim(value);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(normalized);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
