package Controller;

import Model.DAOPublisher;
import Entities.Publisher;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "PublisherController", urlPatterns = { "/publishers" })
public class PublisherController extends HttpServlet {

    private final DAOPublisher dao = new DAOPublisher();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null)
            action = "list";

        try {
            switch (action) {
                case "create":
                    req.getRequestDispatcher("/WEB-INF/views/publisher/create.jsp").forward(req, resp);
                    break;
                case "edit": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Publisher p = dao.getById(id);
                    req.setAttribute("publisher", p);
                    req.getRequestDispatcher("/WEB-INF/views/publisher/edit.jsp").forward(req, resp);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + "/publishers?action=list");
                    break;
                }
                case "list":
                default: {
                    List<Publisher> list = dao.getAll();
                    req.setAttribute("publishers", list);
                    req.getRequestDispatcher("/WEB-INF/views/publisher/list.jsp").forward(req, resp);
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
        String action = req.getParameter("action");
        if (action == null)
            action = "create";

        try {
            if ("create".equals(action)) {
                Publisher p = new Publisher(req.getParameter("publisherName"));
                dao.insert(p);
                resp.sendRedirect(req.getContextPath() + "/publishers?action=list");
                return;
            }
            if ("edit".equals(action)) {
                Publisher p = new Publisher(req.getParameter("publisherName"));
                p.setPublisherID(Integer.parseInt(req.getParameter("publisherID")));
                dao.update(p);
                resp.sendRedirect(req.getContextPath() + "/publishers?action=list");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/publishers?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
