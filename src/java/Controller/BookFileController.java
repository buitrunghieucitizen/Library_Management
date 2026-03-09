package Controller;

import Entities.Book;
import Entities.BookFile;
import Entities.Staff;
import Model.DAOBook;
import Model.DAOBookFile;
import Utils.RoleUtils;
import Utils.PaginationUtils;
import ViewModel.BookFileRow;
import ViewModel.PageSlice;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "BookFileController", urlPatterns = {"/admin/bookfiles"})
public class BookFileController extends HttpServlet {

    private static final String BOOKFILES_PATH = "/admin/bookfiles";
    private static final int PAGE_SIZE = 10;

    private final DAOBookFile daoBookFile = new DAOBookFile();
    private final DAOBook daoBook = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canManageBookFiles(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                    prepareForm(req, null);
                    req.getRequestDispatcher("/WEB-INF/views/bookfile/create.jsp").forward(req, resp);
                    break;
                case "edit":
                    showEdit(req, resp);
                    break;
                case "delete":
                    deleteBookFile(req, resp);
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
        if (!canManageBookFiles(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "create";
        }

        try {
            if ("edit".equals(action)) {
                updateBookFile(req, resp);
                return;
            }
            if ("create".equals(action)) {
                createBookFile(req, resp);
                return;
            }
            resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private boolean canManageBookFiles(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int page = PaginationUtils.parsePage(req.getParameter("page"), 1);
        List<BookFileRow> rows = daoBookFile.getBookFileRows();
        PageSlice<BookFileRow> pageSlice = PaginationUtils.paginate(rows, page, PAGE_SIZE);
        req.setAttribute("bookFiles", pageSlice.getItems());
        req.setAttribute("currentPage", pageSlice.getPage());
        req.setAttribute("totalPages", pageSlice.getTotalPages());
        req.setAttribute("totalItems", pageSlice.getTotalItems());
        req.setAttribute("isAdmin", RoleUtils.isAdmin(req));
        req.getRequestDispatcher("/WEB-INF/views/bookfile/list.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int bookFileId = Integer.parseInt(req.getParameter("id"));
        BookFile bookFile = daoBookFile.getById(bookFileId);
        if (bookFile == null) {
            resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?error=Khong%20tim%20thay%20bookfile");
            return;
        }

        prepareForm(req, bookFile);
        req.getRequestDispatcher("/WEB-INF/views/bookfile/edit.jsp").forward(req, resp);
    }

    private void createBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        BookFile bookFile = readBookFile(req, false);
        daoBookFile.insert(bookFile);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Them%20bookfile%20thanh%20cong");
    }

    private void updateBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        BookFile bookFile = readBookFile(req, true);
        daoBookFile.update(bookFile);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Cap%20nhat%20bookfile%20thanh%20cong");
    }

    private void deleteBookFile(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int bookFileId = Integer.parseInt(req.getParameter("id"));
        daoBookFile.delete(bookFileId);
        resp.sendRedirect(req.getContextPath() + BOOKFILES_PATH + "?msg=Xoa%20bookfile%20thanh%20cong");
    }

    private BookFile readBookFile(HttpServletRequest req, boolean hasId) {
        Staff staff = RoleUtils.getLoggedStaff(req);
        if (staff == null) {
            throw new IllegalStateException("Staff session is missing.");
        }
        long fileSize = 0;
        String rawFileSize = req.getParameter("fileSize");
        if (rawFileSize != null && !rawFileSize.trim().isEmpty()) {
            fileSize = Long.parseLong(rawFileSize);
        }

        BookFile bookFile = new BookFile(
                Integer.parseInt(req.getParameter("bookID")),
                staff.getStaffID(),
                req.getParameter("fileName"),
                req.getParameter("fileUrl"),
                req.getParameter("fileType"),
                fileSize);
        if (hasId) {
            bookFile.setBookFileID(Integer.parseInt(req.getParameter("bookFileID")));
            bookFile.setIsActive(req.getParameter("isActive") != null);
        }
        return bookFile;
    }

    private void prepareForm(HttpServletRequest req, BookFile bookFile) throws SQLException {
        List<Book> books = daoBook.getAll();
        req.setAttribute("books", books);
        req.setAttribute("bookFile", bookFile);
    }
}
