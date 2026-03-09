package Controller;

import Model.DAOStudent;
import Entities.Student;
import ViewModel.PageSlice;
import Utils.PaginationUtils;
import Utils.RoleUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "StudentController", urlPatterns = { "/admin/students" })
public class StudentController extends HttpServlet {

    private static final String STUDENTS_PATH = "/admin/students";
    private static final int PAGE_SIZE = 10;

    private final DAOStudent dao = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!canView(req)) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp?error=Truy%20c%E1%BA%ADp%20b%E1%BB%8B%20t%E1%BB%AB%20ch%E1%BB%91i");
            return;
        }

        String action = req.getParameter("action");
        if (action == null)
            action = "list";

        try {
            switch (action) {
                case "create":
                case "edit":
                case "delete":
                    if (!isAdmin(req)) {
                        resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list&error=Permission Denied");
                        return;
                    }
                    if ("create".equals(action)) {
                        req.getRequestDispatcher("/WEB-INF/views/student/create.jsp").forward(req, resp);
                    } else if ("edit".equals(action)) {
                        int id = Integer.parseInt(req.getParameter("id"));
                        Student s = dao.getById(id);
                        req.setAttribute("student", s);
                        req.getRequestDispatcher("/WEB-INF/views/student/edit.jsp").forward(req, resp);
                    } else if ("delete".equals(action)) {
                        int id = Integer.parseInt(req.getParameter("id"));
                        dao.delete(id);
                        resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list");
                    }
                    break;
                case "list":
                default: {
                    int page = PaginationUtils.parsePage(req.getParameter("page"), 1);
                    List<Student> list = dao.getAll();
                    PageSlice<Student> pageSlice = PaginationUtils.paginate(list, page, PAGE_SIZE);
                    req.setAttribute("students", pageSlice.getItems());
                    req.setAttribute("currentPage", pageSlice.getPage());
                    req.setAttribute("totalPages", pageSlice.getTotalPages());
                    req.setAttribute("totalItems", pageSlice.getTotalItems());
                    req.getRequestDispatcher("/WEB-INF/views/student/list.jsp").forward(req, resp);
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

    private boolean canView(HttpServletRequest req) {
        return RoleUtils.isAdmin(req) || RoleUtils.isStaff(req);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action == null)
            action = "create";

        if (!isAdmin(req)) {
            resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list&error=Permission Denied");
            return;
        }

        try {
            if ("create".equals(action)) {
                Student s = new Student(req.getParameter("studentName"), req.getParameter("email"),
                        req.getParameter("phone"));
                dao.insert(s);
                resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list");
                return;
            }
            if ("edit".equals(action)) {
                Student s = new Student(req.getParameter("studentName"), req.getParameter("email"),
                        req.getParameter("phone"));
                s.setStudentID(Integer.parseInt(req.getParameter("studentID")));
                dao.update(s);
                resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list");
                return;
            }
            resp.sendRedirect(req.getContextPath() + STUDENTS_PATH + "?action=list");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}

