package Controller.client;

import Entities.Student;
import Model.DAOStudent;
import Utils.RoleUtils;
import Utils.StudentContextUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet(name = "StudentProfileController", urlPatterns = {"/profile"})
public class StudentProfileController extends HttpServlet {

    private final DAOStudent daoStudent = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Student profileStudent = resolveProfileStudent(request);
            if (profileStudent == null) {
                request.setAttribute("profileError", "Không tìm thấy hồ sơ sinh viên tương ứng với tài khoản hiện tại.");
            }
            request.setAttribute("profileStudent", profileStudent);
            request.getRequestDispatcher("/WEB-INF/views/client/profile.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        try {
            Student currentStudent = resolveProfileStudent(request);
            if (currentStudent == null) {
                redirectWithMessage(request, response, "error",
                        "Không tìm thấy hồ sơ sinh viên để cập nhật.");
                return;
            }

            String studentName = trim(request.getParameter("studentName"));
            String email = trim(request.getParameter("email"));
            String phone = trim(request.getParameter("phone"));

            if (studentName.isEmpty()) {
                forwardWithError(request, response, currentStudent, "Họ và tên không được để trống.");
                return;
            }

            if (!email.isEmpty() && !isValidEmail(email)) {
                forwardWithError(request, response, currentStudent, "Email không hợp lệ.");
                return;
            }

            Student updatedStudent = new Student(
                    currentStudent.getStudentID(),
                    studentName,
                    email.isEmpty() ? null : email,
                    phone.isEmpty() ? null : phone);

            daoStudent.update(updatedStudent);
            redirectWithMessage(request, response, "msg", "Cập nhật hồ sơ sinh viên thành công.");
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private Student resolveProfileStudent(HttpServletRequest request) throws SQLException {
        Student currentStudent = StudentContextUtils.resolveCurrentStudent(request);
        if (currentStudent == null) {
            return null;
        }

        Student freshStudent = daoStudent.getById(currentStudent.getStudentID());
        return freshStudent == null ? currentStudent : freshStudent;
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response,
            Student currentStudent, String message) throws ServletException, IOException {
        request.setAttribute("profileStudent", currentStudent);
        request.setAttribute("profileError", message);
        request.getRequestDispatcher("/WEB-INF/views/client/profile.jsp").forward(request, response);
    }

    private void redirectWithMessage(HttpServletRequest request, HttpServletResponse response,
            String key, String message) throws IOException {
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/profile?" + key + "=" + encodedMessage);
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
