/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controller;

import Model.DAOBook;
import Entities.Book;
import Utils.RoleUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author Administrator
 */
@WebServlet(name = "BookController", urlPatterns = {"/books", "/admin/books"})
public class BookController extends HttpServlet {

    private static final String PUBLIC_BOOKS_PATH = "/books";
    private static final String ADMIN_BOOKS_PATH = "/admin/books";

    private final DAOBook dao = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (shouldRedirectToAdminRoute(req)) {
            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
            return;
        }

        if (isAdminSection(req) && !canAccessAdminSection(req)) {
            resp.sendRedirect(req.getContextPath() + PUBLIC_BOOKS_PATH + "?action=list&error=Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if (action == null) action = "list";

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
                        int id = Integer.parseInt(req.getParameter("id"));
                        Book b = dao.getById(id);
                        req.setAttribute("book", b);
                        req.getRequestDispatcher("/WEB-INF/views/book/edit.jsp").forward(req, resp);
                    } else if ("delete".equals(action)) {
                        int id = Integer.parseInt(req.getParameter("id"));
                        dao.delete(id);
                        resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
                    }
                    break;

                case "list":
                default: {
                    List<Book> list = dao.getAll();
                    req.setAttribute("books", list);
                    req.setAttribute("adminSection", isAdminSection(req));
                    req.getRequestDispatcher("/WEB-INF/views/book/list.jsp").forward(req, resp);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private boolean isAdmin(HttpServletRequest req) {
        return RoleUtils.isAdmin(req);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "create";

        if (!isAdminSection(req) || !isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + getListPath(req) + "?action=list&error=Permission Denied");
            return;
        }

        try {
            if ("create".equals(action)) {
                Book b = readBookFromRequest(req, false);
                dao.insert(b);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
                return;
            }

            if ("edit".equals(action)) {
                Book b = readBookFromRequest(req, true);
                dao.update(b);
                resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
                return;
            }

            resp.sendRedirect(req.getContextPath() + ADMIN_BOOKS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
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

        Book b = new Book(name, quantity, available, categoryID, publisherID);
        if (hasId) b.setBookID(Integer.parseInt(req.getParameter("bookID")));
        return b;
    }
}
