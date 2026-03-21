package Controller.client;

import Controller.borrow.BorrowHelper;
import Controller.borrow.BorrowValidator;
import Entities.Book;
import Entities.Borrow;
import Entities.Category;
import Entities.Publisher;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOCategory;
import Model.DAOFine;
import Model.DAOPublisher;
import Model.DAOStudent;
import Utils.RoleUtils;
import ViewModel.PageSlice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StudentHome", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private static final int PAGE_SIZE = 12;

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCategory = new DAOCategory();
    private final DAOPublisher daoPublisher = new DAOPublisher();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOStudent daoStudent = new DAOStudent();
    private final DAOFine daoFine = new DAOFine();
    private final BorrowHelper borrowHelper = new BorrowHelper(daoStudent);
    private final BorrowValidator validator = new BorrowValidator(daoBorrow, daoFine);

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
            List<Book> books = daoBook.getFiltered(search, letter, categoryId, publisherId, author);
            PageSlice<Book> pageSlice = paginate(books, requestedPage, PAGE_SIZE);
            List<Category> categories = daoCategory.getAll();
            List<Publisher> publishers = daoPublisher.getAll();

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

            // Set all attributes
            request.setAttribute("books", pageSlice.getItems());
            request.setAttribute("categories", categories);
            request.setAttribute("publishers", publishers);
            request.setAttribute("holds", holds);
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
}
