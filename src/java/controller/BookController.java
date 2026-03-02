/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import DAL.DAOBook;
import entities.Book;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 * @author Administrator
 */
@WebServlet(name = "BookController", urlPatterns = {"/admin/books"})
public class BookController extends HttpServlet {

    private final DAOBook dao = new DAOBook();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "create":
                    req.getRequestDispatcher("/WEB-INF/views/book/create.jsp").forward(req, resp);
                    break;

                case "edit": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Book b = dao.getById(id);
                    req.setAttribute("book", b);
                    req.getRequestDispatcher("/WEB-INF/views/book/edit.jsp").forward(req, resp);
                    break;
                }

                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + "/books?action=list");
                    break;
                }

                case "list":
                default: {
                    List<Book> list = dao.getAll();
                    req.setAttribute("books", list);
                    req.getRequestDispatcher("/WEB-INF/views/book/list.jsp").forward(req, resp);
                    break;
                }
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "create";

        try {
            if ("create".equals(action)) {
                Book b = readBookFromRequest(req, false);
                dao.insert(b);
                resp.sendRedirect(req.getContextPath() + "/books?action=list");
                return;
            }

            if ("edit".equals(action)) {
                Book b = readBookFromRequest(req, true);
                dao.update(b);
                resp.sendRedirect(req.getContextPath() + "/books?action=list");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/books?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
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
