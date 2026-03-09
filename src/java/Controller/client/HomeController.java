package Controller.client;

import Entities.Book;
import Entities.Borrow;
import Entities.Category;
import Entities.Publisher;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBorrow;
import Model.DAOCategory;
import Model.DAOPublisher;
import Model.DAOStudent;
import Utils.RoleUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "StudentHome", urlPatterns = {"/home"})
public class HomeController extends HttpServlet {

    private static final int PAGE_SIZE = 12;

    private final DAOBook daoBook = new DAOBook();
    private final DAOCategory daoCategory = new DAOCategory();
    private final DAOPublisher daoPublisher = new DAOPublisher();
    private final DAOBorrow daoBorrow = new DAOBorrow();
    private final DAOStudent daoStudent = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

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
            List<Book> books = daoBook.getFiltered(search, letter, categoryId, publisherId, author);
            PageSlice<Book> pageSlice = paginate(books, requestedPage, PAGE_SIZE);
            List<Category> categories = daoCategory.getAll();
            List<Publisher> publishers = daoPublisher.getAll();
            List<Borrow> holds = resolveActiveBorrows(request);

            request.setAttribute("books", pageSlice.items);
            request.setAttribute("categories", categories);
            request.setAttribute("publishers", publishers);
            request.setAttribute("holds", holds);
            request.setAttribute("search", search);
            request.setAttribute("letter", letter);
            request.setAttribute("categoryId", categoryIdRaw);
            request.setAttribute("publisherId", publisherIdRaw);
            request.setAttribute("author", author);
            request.setAttribute("currentPage", pageSlice.page);
            request.setAttribute("totalPages", pageSlice.totalPages);
            request.setAttribute("totalBooks", pageSlice.totalItems);

            request.getRequestDispatcher("/WEB-INF/views/client/home/index.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private List<Borrow> resolveActiveBorrows(HttpServletRequest request) throws SQLException {
        Staff staff = RoleUtils.getLoggedStaff(request);
        if (staff == null) {
            return Collections.emptyList();
        }

        Integer studentId = resolveStudentId(staff);
        if (studentId == null) {
            return Collections.emptyList();
        }
        return daoBorrow.getActiveByStudentId(studentId);
    }

    private Integer resolveStudentId(Staff staff) throws SQLException {
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
        try {
            return Integer.parseInt(value.substring(index + 1));
        } catch (NumberFormatException e) {
            return null;
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

    private static class PageSlice<T> {
        private final List<T> items;
        private final int page;
        private final int totalPages;
        private final int totalItems;

        private PageSlice(List<T> items, int page, int totalPages, int totalItems) {
            this.items = items;
            this.page = page;
            this.totalPages = totalPages;
            this.totalItems = totalItems;
        }
    }
}
