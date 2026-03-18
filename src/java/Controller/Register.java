package Controller;

import Entities.Staff;
import Entities.StaffRole;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Utils.PasswordResetUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {

    private static final int STUDENT_ROLE_ID = 8;

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("staff") != null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
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

        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password == null || password.isEmpty()) {
            forwardError(request, response, name, username, email, "Vui long nhap day du thong tin.");
            return;
        }

        if (!password.equals(confirm)) {
            forwardError(request, response, name, username, email, "Mat khau xac nhan khong khop.");
            return;
        }

        if (!PasswordResetUtils.isEmail(email)) {
            forwardError(request, response, name, username, email, "Email khong hop le.");
            return;
        }

        try {
            if (daoStaff.existsByUsername(username)) {
                forwardError(request, response, name, username, email, "Ten dang nhap da ton tai.");
                return;
            }

            if (daoStaff.existsByEmail(email) || daoStaff.getByUsername(email) != null) {
                forwardError(request, response, name, username, email, "Email da duoc su dung.");
                return;
            }

            Staff staff = new Staff(0, name, username, email, password);
            daoStaff.insert(staff);
            daoStaffRole.insert(new StaffRole(staff.getStaffID(), STUDENT_ROLE_ID));
            response.sendRedirect(request.getContextPath() + "/LoginURL?registered=1");
        } catch (SQLException e) {
            forwardError(request, response, name, username, email, "Loi he thong: " + e.getMessage());
        }
    }

    private void forwardError(HttpServletRequest request, HttpServletResponse response,
            String name, String username, String email, String error) throws ServletException, IOException {
        request.setAttribute("name", name);
        request.setAttribute("username", username);
        request.setAttribute("email", email);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
