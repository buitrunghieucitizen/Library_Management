package Controller;

import Model.DAOAuthor;
import Entities.Author;
import Utils.RoleUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "AuthorController", urlPatterns = { "/admin/authors" })
public class AuthorController extends HttpServlet {

    private static final String AUTHORS_PATH = "/admin/authors";

    private final DAOAuthor dao = new DAOAuthor();

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
                    req.getRequestDispatcher("/WEB-INF/views/author/create.jsp").forward(req, resp);
                    break;
                case "edit": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Author a = dao.getById(id);
                    req.setAttribute("author", a);
                    req.getRequestDispatcher("/WEB-INF/views/author/edit.jsp").forward(req, resp);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + AUTHORS_PATH + "?action=list");
                    break;
                }
                case "list":
                default: {
                    List<Author> list = dao.getAll();
                    req.setAttribute("authors", list);
                    req.getRequestDispatcher("/WEB-INF/views/author/list.jsp").forward(req, resp);
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
                Author a = new Author(req.getParameter("authorName"));
                dao.insert(a);
                resp.sendRedirect(req.getContextPath() + AUTHORS_PATH + "?action=list");
                return;
            }
            if ("edit".equals(action)) {
                Author a = new Author(req.getParameter("authorName"));
                a.setAuthorID(Integer.parseInt(req.getParameter("authorID")));
                dao.update(a);
                resp.sendRedirect(req.getContextPath() + AUTHORS_PATH + "?action=list");
                return;
            }
            resp.sendRedirect(req.getContextPath() + AUTHORS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
