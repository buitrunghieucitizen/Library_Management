package Controller;

import Entities.Book;
import Entities.Price;
import Model.DAOBook;
import Model.DAOBookPrice;
import Model.DAOPrice;
import Model.DBConnection;
import Utils.RoleUtils;
import Utils.PaginationUtils;
import ViewModel.CurrentPriceInfo;
import ViewModel.PageSlice;
import ViewModel.PriceInput;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "BookController", urlPatterns = {"/books", "/admin/books"})
public class BookController extends HttpServlet {

    private static final String PUBLIC_BOOKS_PATH = "/books";
    private static final String ADMIN_BOOKS_PATH = "/admin/books";
    private static final int PAGE_SIZE = 10;

    private final DAOBook daoBook = new DAOBook();
    private final DAOPrice daoPrice = new DAOPrice();
    private final DAOBookPrice daoBookPrice = new DAOBookPrice();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
            return;
        }

        if (isAdminSection(req) && !canAccessAdminSection(req)) {
            resp.sendRedirect(req.getContextPath() + PUBLIC_BOOKS_PATH + "?action=list&error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "create":
                case "edit":
                case "delete":
                    if (!isAdminSection(req) || !isAdmin(req)) {
                        resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&error=Permission Denied");
                        return;
                    }
                    if ("create".equals(action)) {
                        req.getRequestDispatcher("/WEB-INF/views/book/create.jsp").forward(req, resp);
                    } else if ("edit".equals(action)) {
                        showEdit(req, resp);
                    } else {
                        int id = Integer.parseInt(req.getParameter("id"));
                        daoBook.delete(id);
                        resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
                    }
                    break;

                case "list":
                default:
                    int page = PaginationUtils.parsePage(req.getParameter("page"), 1);
                    List<Book> list = daoBook.getAll();
                    PageSlice<Book> pageSlice = PaginationUtils.paginate(list, page, PAGE_SIZE);
                    req.setAttribute("books", pageSlice.getItems());
                    req.setAttribute("currentPage", pageSlice.getPage());
                    req.setAttribute("totalPages", pageSlice.getTotalPages());
                    req.setAttribute("totalItems", pageSlice.getTotalItems());
                    req.setAttribute("adminSection", isAdminSection(req));
                    req.getRequestDispatcher("/WEB-INF/views/book/list.jsp").forward(req, resp);
                    break;
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String action = req.getParameter("action");
        if (action == null) {
            action = "create";
        }

        if (!isAdminSection(req) || !isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&error=Permission Denied");
            return;
        }

        try {
            if ("create".equals(action)) {
                createBookWithPrice(req);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&msg=Them%20sach%20va%20gia%20thanh%20cong");
                return;
            }

            if ("edit".equals(action)) {
                updateBookWithPrice(req);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&msg=Cap%20nhat%20sach%20va%20gia%20thanh%20cong");
                return;
            }

            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Book book = daoBook.getById(id);
        if (book == null) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list&error=Khong%20tim%20thay%20sach");
            return;
        }

        req.setAttribute("book", book);
        req.setAttribute("currentPrice", daoBookPrice.getCurrentPriceInfo(id));
        req.getRequestDispatcher("/WEB-INF/views/book/edit.jsp").forward(req, resp);
    }

    private boolean isAdmin(HttpServletRequest req) {
        return RoleUtils.isAdmin(req);
    }

    private boolean shouldRedirectToAdminRoute(HttpServletRequest req) {
        return PUBLIC_BOOKS_PATH.equals(req.getServletPath()) && !RoleUtils.isStudentOnly(req);
    }

    private boolean canAccessAdminSection(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    private boolean isAdminSection(HttpServletRequest req) {
        return ADMIN_BOOKS_PATH.equals(req.getServletPath());
    }

    private String getListPath(HttpServletRequest req) {
        return isAdminSection(req) ? ADMIN_BOOKS_PATH : PUBLIC_BOOKS_PATH;
    }

    private Book readBookFromRequest(HttpServletRequest req, boolean hasId) {
        String name = req.getParameter("bookName");
        int quantity = Integer.parseInt(req.getParameter("quantity"));
        int available = Integer.parseInt(req.getParameter("available"));
        int categoryID = Integer.parseInt(req.getParameter("categoryID"));
        int publisherID = Integer.parseInt(req.getParameter("publisherID"));

        Book book = new Book(name, quantity, available, categoryID, publisherID);
        if (hasId) {
            book.setBookID(Integer.parseInt(req.getParameter("bookID")));
        }
        return book;
    }

    private PriceInput readPriceInput(HttpServletRequest req) {
        double amount = Double.parseDouble(req.getParameter("priceAmount"));
        String currency = trimToDefault(req.getParameter("priceCurrency"), "VND");
        String note = trimToNull(req.getParameter("priceNote"));
        return new PriceInput(amount, currency, note);
    }

    private void createBookWithPrice(HttpServletRequest req) throws SQLException {
        Book book = readBookFromRequest(req, false);
        PriceInput priceInput = readPriceInput(req);

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            if (daoBook.insert(con, book) == 0) {
                throw new SQLException("Khong the tao sach.");
            }

            Price price = priceInput.toEntity();
            if (daoPrice.insert(con, price) == 0) {
                throw new SQLException("Khong the tao gia sach.");
            }

            if (daoBookPrice.insertCurrent(con, book.getBookID(), price.getPriceID(), LocalDate.now()) == 0) {
                throw new SQLException("Khong the gan gia cho sach.");
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

    private void updateBookWithPrice(HttpServletRequest req) throws SQLException {
        Book book = readBookFromRequest(req, true);
        PriceInput priceInput = readPriceInput(req);

        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to database!");
        }

        try {
            con.setAutoCommit(false);

            if (daoBook.update(con, book) == 0) {
                throw new SQLException("Cap nhat sach that bai.");
            }

            CurrentPriceInfo currentPrice = daoBookPrice.getCurrentPriceInfo(con, book.getBookID());
            if (currentPrice == null) {
                Price price = priceInput.toEntity();
                if (daoPrice.insert(con, price) == 0) {
                    throw new SQLException("Khong the tao gia sach.");
                }
                if (daoBookPrice.insertCurrent(con, book.getBookID(), price.getPriceID(), LocalDate.now()) == 0) {
                    throw new SQLException("Khong the gan gia cho sach.");
                }
            } else if (isPriceChanged(currentPrice, priceInput)) {
                if (LocalDate.now().equals(currentPrice.getStartDate())) {
                    Price price = new Price(currentPrice.getPriceID(), priceInput.getAmount(),
                            priceInput.getCurrency(), priceInput.getNote());
                    if (daoPrice.update(con, price) == 0) {
                        throw new SQLException("Cap nhat gia sach that bai.");
                    }
                } else {
                    daoBookPrice.closeCurrent(
                            con,
                            book.getBookID(),
                            currentPrice.getPriceID(),
                            currentPrice.getStartDate(),
                            LocalDate.now().minusDays(1));

                    Price price = priceInput.toEntity();
                    if (daoPrice.insert(con, price) == 0) {
                        throw new SQLException("Khong the tao gia sach.");
                    }
                    if (daoBookPrice.insertCurrent(con, book.getBookID(), price.getPriceID(), LocalDate.now()) == 0) {
                        throw new SQLException("Khong the gan gia cho sach.");
                    }
                }
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

    private boolean isPriceChanged(CurrentPriceInfo currentPrice, PriceInput priceInput) {
        if (currentPrice == null) {
            return true;
        }
        if (Double.compare(currentPrice.getAmount(), priceInput.getAmount()) != 0) {
            return true;
        }
        if (!safeEquals(currentPrice.getCurrency(), priceInput.getCurrency())) {
            return true;
        }
        return !safeEquals(currentPrice.getNote(), priceInput.getNote());
    }

    private boolean safeEquals(String left, String right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    private String trimToDefault(String value, String defaultValue) {
        String trimmed = trimToNull(value);
        return trimmed == null ? defaultValue : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
