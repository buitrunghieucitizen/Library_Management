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
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
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
                    int id = Integer.parseInt(req.getParameter("id"));
                    Category c = dao.getById(id);
                    req.setAttribute("category", c);
                    req.getRequestDispatcher("/WEB-INF/views/category/edit.jsp").forward(req, resp);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
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
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if (action == null)
            action = "create";

        try {
            if ("create".equals(action)) {
                Category c = new Category(req.getParameter("categoryName"));
                dao.insert(c);
                resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
                return;
            }
            if ("edit".equals(action)) {
                Category c = new Category(req.getParameter("categoryName"));
                c.setCategoryID(Integer.parseInt(req.getParameter("categoryID")));
                dao.update(c);
                resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
                return;
            }
            resp.sendRedirect(req.getContextPath() + CATEGORIES_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
