package Controller;

import Model.DAOStaff;
import Model.DAOStaffRole;
import Entities.Staff;
import Entities.StaffRole;
import Utils.RoleUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "Login", urlPatterns = {"/LoginURL"})
public class Login extends HttpServlet {

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);
        }
        if (session != null && session.getAttribute("staff") != null) {
            if (RoleUtils.isStudentOnly(request)) {
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Staff staff = daoStaff.login(username, password);
            if (staff != null) {
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30 * 60);
                session.setAttribute("staff", staff);

                List<StaffRole> staffRoles = daoStaffRole.getByStaffId(staff.getStaffID());
                List<Integer> roleIds = new ArrayList<>();
                for (StaffRole sr : staffRoles) {
                    roleIds.add(sr.getRoleID());
                }
                ensureDefaultRoleIfMissing(staff, roleIds);
                session.setAttribute("roles", roleIds);

                boolean isAdmin = roleIds.contains(RoleUtils.ROLE_ADMIN);
                boolean isStaff = roleIds.contains(RoleUtils.ROLE_STAFF) || roleIds.contains(RoleUtils.ROLE_STAFF_ALT);
                boolean isStudent = roleIds.contains(RoleUtils.ROLE_STUDENT) || roleIds.contains(RoleUtils.ROLE_STUDENT_ALT);

                if (isStudent && !isAdmin && !isStaff) {
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    response.sendRedirect(request.getContextPath() + "/index.jsp");
                }
            } else {
                request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void ensureDefaultRoleIfMissing(Staff staff, List<Integer> roleIds) throws SQLException {
        if (staff == null || roleIds == null || !roleIds.isEmpty()) {
            return;
        }

        Integer inferredRole = inferRole(staff);
        if (inferredRole == null) {
            return;
        }

        daoStaffRole.insert(new StaffRole(staff.getStaffID(), inferredRole));
        roleIds.add(inferredRole);
    }

    private Integer inferRole(Staff staff) {
        String username = normalize(staff.getUsername());
        String staffName = normalize(staff.getStaffName());

        if ("admin".equals(username) || username.startsWith("admin") || staffName.contains("admin")) {
            return RoleUtils.ROLE_ADMIN;
        }

        if (username.startsWith("student") || staffName.contains("student")) {
            return RoleUtils.ROLE_STUDENT;
        }

        if (username.startsWith("staff") || username.startsWith("librarian") || staffName.contains("staff")) {
            return RoleUtils.ROLE_STAFF;
        }

        return null;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
