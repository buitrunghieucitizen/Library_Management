package Controller;

import Entities.GoogleAccount;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Entities.Staff;
import Entities.StaffRole;
import Utils.GoogleOAuthService;
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

    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60;

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = trim(request.getParameter("code"));
        String oauthError = trim(request.getParameter("error"));
        if (!code.isEmpty() || !oauthError.isEmpty()) {
            handleGoogleCallback(request, response, code, oauthError);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
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
                completeLogin(request, response, staff, false);
                return;
            }

            request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response,
            String code, String oauthError) throws ServletException, IOException {
        if (!oauthError.isEmpty()) {
            request.setAttribute("error", "Bạn đã hủy đăng nhập Google.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String accessToken;
        GoogleAccount googleAccount;
        try {
            accessToken = GoogleOAuthService.getToken(code);
            googleAccount = GoogleOAuthService.getUserInfo(accessToken);
        } catch (IOException ex) {
            request.setAttribute("error", "Đăng nhập Google thất bại. Vui lòng thử lại.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            Staff staff = daoStaff.loginByGoogle(googleAccount);
            if (staff == null) {
                request.setAttribute("error", "Không lấy được tài khoản Google hợp lệ.");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            completeLogin(request, response, staff, true);
        } catch (SQLException ex) {
            request.setAttribute("error", "Lỗi hệ thống: " + ex.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void completeLogin(HttpServletRequest request, HttpServletResponse response,
            Staff staff, boolean preferStudentRole) throws SQLException, IOException {
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        session.setAttribute("staff", staff);

        List<Integer> roleIds = loadRoleIds(staff, preferStudentRole);
        session.setAttribute("roles", roleIds);

        boolean isAdmin = roleIds.contains(RoleUtils.ROLE_ADMIN);
        boolean isStaff = roleIds.contains(RoleUtils.ROLE_STAFF) || roleIds.contains(RoleUtils.ROLE_STAFF_ALT);
        boolean isStudent = roleIds.contains(RoleUtils.ROLE_STUDENT) || roleIds.contains(RoleUtils.ROLE_STUDENT_ALT);

        if (isStudent && !isAdmin && !isStaff) {
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    private List<Integer> loadRoleIds(Staff staff, boolean preferStudentRole) throws SQLException {
        List<StaffRole> staffRoles = daoStaffRole.getByStaffId(staff.getStaffID());
        List<Integer> roleIds = new ArrayList<>();
        for (StaffRole sr : staffRoles) {
            roleIds.add(sr.getRoleID());
        }
        ensureDefaultRoleIfMissing(staff, roleIds, preferStudentRole);
        return roleIds;
    }

    private void ensureDefaultRoleIfMissing(Staff staff, List<Integer> roleIds,
            boolean preferStudentRole) throws SQLException {
        if (staff == null || roleIds == null || !roleIds.isEmpty()) {
            return;
        }

        Integer inferredRole = preferStudentRole ? RoleUtils.ROLE_STUDENT : inferRole(staff);
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

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
