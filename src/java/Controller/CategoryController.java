package Controller;

import Model.DAOCategory;
import Entities.Category;
import Utils.RoleUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CategoryController", urlPatterns = { "/admin/categories" })
public class CategoryController extends HttpServlet {

    private static final String CATEGORIES_PATH = "/admin/categories";

    private final DAOCategory dao = new DAOCategory();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!RoleUtils.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null)
            action = "list";

        try {
            switch (action) {
                case "create":
                    req.getRequestDispatcher("/WEB-INF/views/category/create.jsp").forward(req, resp);
                    break;
                case "edit": {
                    Integer id = parseInt(req.getParameter("id"));
                    if (id == null || id <= 0) {
                        resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list&error=Id%20th%E1%BB%83%20lo%E1%BA%A1i%20kh%C3%B4ng%20h%E1%BB%A3p%20l%E1%BB%87");
                        return;
                    }
                    Category c = dao.getById(id);
                    if (c == null) {
                        resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list&error=Kh%C3%B4ng%20t%C3%ACm%20th%E1%BA%A5y%20th%E1%BB%83%20lo%E1%BA%A1i");
                        return;
                    }
                    req.setAttribute("category", c);
                    req.getRequestDispatcher("/WEB-INF/views/category/edit.jsp").forward(req, resp);
                    break;
                }
                case "delete": {
                    Integer id = parseInt(req.getParameter("id"));
                    if (id == null || id <= 0) {
                        resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list&error=Id%20th%E1%BB%83%20lo%E1%BA%A1i%20kh%C3%B4ng%20h%E1%BB%A3p%20l%E1%BB%87");
                        return;
                    }
                    dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
                    break;
                }
                case "list":
                default: {
                    List<Category> list = dao.getAll();
                    req.setAttribute("categories", list);
                    req.getRequestDispatcher("/WEB-INF/views/category/list.jsp").forward(req, resp);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        if (!RoleUtils.isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null)
            action = "create";

        if ("create".equals(action)) {
            handleCreate(req, resp);
            return;
        }
        if ("edit".equals(action)) {
            handleEdit(req, resp);
            return;
        }
        resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String categoryName = trimToNull(req.getParameter("categoryName"));
        if (categoryName == null) {
            req.setAttribute("error", "Tên thể loại không được để trống.");
            req.setAttribute("categoryName", "");
            req.getRequestDispatcher("/WEB-INF/views/category/create.jsp").forward(req, resp);
            return;
        }

        try {
            Category c = new Category(categoryName);
            dao.insert(c);
            resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                req.setAttribute("error", "Tên thể loại đã tồn tại.");
                req.setAttribute("categoryName", categoryName);
                req.getRequestDispatcher("/WEB-INF/views/category/create.jsp").forward(req, resp);
                return;
            }
            throw new ServletException(e);
        }
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer categoryId = parseInt(req.getParameter("categoryID"));
        if (categoryId == null || categoryId <= 0) {
            resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list&error=Id%20th%E1%BB%83%20lo%E1%BA%A1i%20kh%C3%B4ng%20h%E1%BB%A3p%20l%E1%BB%87");
            return;
        }

        String categoryName = trimToNull(req.getParameter("categoryName"));
        Category c = new Category(categoryName);
        c.setCategoryID(categoryId);

        if (categoryName == null) {
            req.setAttribute("error", "Tên thể loại không được để trống.");
            req.setAttribute("category", c);
            req.getRequestDispatcher("/WEB-INF/views/category/edit.jsp").forward(req, resp);
            return;
        }

        try {
            int affected = dao.update(c);
            if (affected == 0) {
                resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list&error=Kh%C3%B4ng%20t%C3%ACm%20th%E1%BA%A5y%20th%E1%BB%83%20lo%E1%BA%A1i");
                return;
            }
            resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
        } catch (SQLException e) {
            if (isDuplicateKeyError(e)) {
                req.setAttribute("error", "Tên thể loại đã tồn tại.");
                req.setAttribute("category", c);
                req.getRequestDispatcher("/WEB-INF/views/category/edit.jsp").forward(req, resp);
                return;
            }
            throw new ServletException(e);
        }
    }

    private Integer parseInt(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isDuplicateKeyError(SQLException e) {
        SQLException current = e;
        while (current != null) {
            int errorCode = current.getErrorCode();
            if (errorCode == 2601 || errorCode == 2627) {
                return true;
            }
            current = current.getNextException();
        }
        return false;
    }
}

