package Controller;

import Model.DAOStudent;
import Entities.Student;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentController", urlPatterns = { "/students" })
public class StudentController extends HttpServlet {

    private final DAOStudent dao = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null)
            action = "list";

        try {
            switch (action) {
                case "create":
                    req.getRequestDispatcher("/WEB-INF/views/student/create.jsp").forward(req, resp);
                    break;
                case "edit": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    Student s = dao.getById(id);
                    req.setAttribute("student", s);
                    req.getRequestDispatcher("/WEB-INF/views/student/edit.jsp").forward(req, resp);
                    break;
                }
                case "delete": {
                    int id = Integer.parseInt(req.getParameter("id"));
                    dao.delete(id);
                    resp.sendRedirect(req.getContextPath() + "/students?action=list");
                    break;
                }
                case "list":
                default: {
                    List<Student> list = dao.getAll();
                    req.setAttribute("students", list);
                    req.getRequestDispatcher("/WEB-INF/views/student/list.jsp").forward(req, resp);
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
                Student s = new Student(req.getParameter("studentName"), req.getParameter("email"),
                        req.getParameter("phone"));
                dao.insert(s);
                resp.sendRedirect(req.getContextPath() + "/students?action=list");
                return;
            }
            if ("edit".equals(action)) {
                Student s = new Student(req.getParameter("studentName"), req.getParameter("email"),
                        req.getParameter("phone"));
                s.setStudentID(Integer.parseInt(req.getParameter("studentID")));
                dao.update(s);
                resp.sendRedirect(req.getContextPath() + "/students?action=list");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/students?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
