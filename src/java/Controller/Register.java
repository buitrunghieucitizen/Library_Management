package Controller;

import Entities.Staff;
import Entities.StaffRole;
import Model.DAORole;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Model.DAOStudent;
import Utils.PasswordResetUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_USERNAME_LENGTH = 50;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}\\s]{2,100}$");

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAORole daoRole = new DAORole();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();
    private final DAOStudent daoStudent = new DAOStudent();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("staff") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = trim(request.getParameter("name"));
        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        // === VALIDATE ===
        if (name.isEmpty() || username.isEmpty() || email.isEmpty()
                || password == null || password.isEmpty()) {
            forwardError(request, response, name, username, email,
                    "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // Validate name
        if (!NAME_PATTERN.matcher(name).matches()) {
            forwardError(request, response, name, username, email,
                    "Họ tên chỉ được chứa chữ cái và khoảng trắng (2-100 ký tự).");
            return;
        }

        // Validate username
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            forwardError(request, response, name, username, email,
                    "Tên đăng nhập chỉ gồm chữ cái, số và dấu _ (3-50 ký tự).");
            return;
        }

        // Validate email
        if (!PasswordResetUtils.isEmail(email)) {
            forwardError(request, response, name, username, email, "Email không hợp lệ.");
            return;
        }

        // Validate password
        if (password.length() < MIN_PASSWORD_LENGTH) {
            forwardError(request, response, name, username, email,
                    "Mật khẩu phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return;
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            forwardError(request, response, name, username, email, "Mật khẩu quá dài.");
            return;
        }

        if (!password.equals(confirm)) {
            forwardError(request, response, name, username, email, "Mật khẩu xác nhận không khớp.");
            return;
        }

        // Check password strength: ít nhất 1 chữ + 1 số
        if (!password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            forwardError(request, response, name, username, email,
                    "Mật khẩu phải có ít nhất 1 chữ cái và 1 chữ số.");
            return;
        }

        // Prevent username == password
        if (username.equalsIgnoreCase(password)) {
            forwardError(request, response, name, username, email,
                    "Mật khẩu không được trùng với tên đăng nhập.");
            return;
        }

        try {
            if (daoStaff.existsByUsername(username)) {
                forwardError(request, response, name, username, email, "Tên đăng nhập đã tồn tại.");
                return;
            }

            if (daoStaff.existsByEmail(email)) {
                forwardError(request, response, name, username, email, "Email đã được sử dụng.");
                return;
            }

            Staff staff = new Staff(0, name, username, email, password);
            daoStaff.insert(staff);
            Integer studentRoleId = resolveStudentRoleId();
            if (studentRoleId == null) {
                throw new SQLException("Không tìm thấy vai trò sinh viên trong bảng Role.");
            }
            daoStaffRole.insert(new StaffRole(staff.getStaffID(), studentRoleId));
            daoStudent.ensureMirrorFromStaff(staff);

            response.sendRedirect(request.getContextPath() + "/LoginURL?registered=1");

        } catch (SQLException e) {
            forwardError(request, response, name, username, email, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
            String name, String username, String email, String error)
            throws ServletException, IOException {
        request.setAttribute("name", name);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer resolveStudentRoleId() throws SQLException {
        Entities.Role role = daoRole.getByName("Student");
        if (role != null) {
            return role.getRoleID();
        }
        if (daoRole.existsById(9)) {
            return 9;
        }
        if (daoRole.existsById(8)) {
            return 8;
        }
        return null;
    }
}
