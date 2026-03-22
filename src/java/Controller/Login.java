package Controller;

import Entities.GoogleAccount;
import Model.DAOStaff;
import Model.DAOStaffRole;
import Entities.Staff;
import Entities.StaffRole;
import Utils.GoogleOAuthService;
import Utils.GoogleOAuthConfig;
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
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 5 * 60 * 1000; // 5 phút

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
        if (session != null && session.getAttribute("staff") != null) {
            redirectByRole(request, response);
            return;
        }

        // Pass Google config status cho JSP
        request.setAttribute("googleEnabled", GoogleOAuthConfig.isConfigured());
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = trim(request.getParameter("username"));
        String password = request.getParameter("password");

        // === VALIDATION ===
        if (username.isEmpty() || password == null || password.isEmpty()) {
            forwardLoginError(request, response, username, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (username.length() > 200) {
            forwardLoginError(request, response, "", "Tên đăng nhập quá dài.");
            return;
        }

        // === RATE LIMITING (session-based) ===
        HttpSession session = request.getSession();
        Integer attempts = (Integer) session.getAttribute("loginAttempts");
        Long lockUntil = (Long) session.getAttribute("loginLockUntil");

        if (lockUntil != null && System.currentTimeMillis() < lockUntil) {
            long remainSec = (lockUntil - System.currentTimeMillis()) / 1000;
            forwardLoginError(request, response, username,
                    "Tài khoản tạm khóa. Vui lòng thử lại sau " + remainSec + " giây.");
            return;
        }

        if (attempts == null) {
            attempts = 0;
        }

        try {
            Staff staff = daoStaff.login(username, password);
            if (staff != null) {
                // Reset attempts
                session.removeAttribute("loginAttempts");
                session.removeAttribute("loginLockUntil");
                completeLogin(request, response, staff, false);
                return;
            }

            // Login failed
            attempts++;
            session.setAttribute("loginAttempts", attempts);
            if (attempts >= MAX_LOGIN_ATTEMPTS) {
                session.setAttribute("loginLockUntil", System.currentTimeMillis() + LOCKOUT_DURATION_MS);
                session.setAttribute("loginAttempts", 0);
                forwardLoginError(request, response, username,
                        "Đăng nhập sai " + MAX_LOGIN_ATTEMPTS + " lần. Tài khoản tạm khóa 5 phút.");
            } else {
                forwardLoginError(request, response, username,
                        "Sai tên đăng nhập hoặc mật khẩu! (Lần " + attempts + "/" + MAX_LOGIN_ATTEMPTS + ")");
            }
        } catch (SQLException e) {
            forwardLoginError(request, response, username, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void handleGoogleCallback(HttpServletRequest request, HttpServletResponse response,
            String code, String oauthError) throws ServletException, IOException {

        if (!oauthError.isEmpty()) {
            request.setAttribute("error", "Bạn đã hủy đăng nhập Google.");
            request.setAttribute("googleEnabled", GoogleOAuthConfig.isConfigured());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (!GoogleOAuthConfig.isConfigured()) {
            request.setAttribute("error", "Đăng nhập Google chưa được cấu hình.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        String accessToken;
        GoogleAccount googleAccount;
        try {
            accessToken = GoogleOAuthService.getToken(code);
            googleAccount = GoogleOAuthService.getUserInfo(accessToken);
        } catch (IOException ex) {
            System.err.println("[Google Login] Token/UserInfo error: " + ex.getMessage());
            request.setAttribute("error", "Đăng nhập Google thất bại. Vui lòng thử lại.");
            request.setAttribute("googleEnabled", GoogleOAuthConfig.isConfigured());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        // === VALIDATE Google account ===
        if (googleAccount.getEmail() == null || googleAccount.getEmail().isBlank()) {
            request.setAttribute("error", "Tài khoản Google không có email.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (!googleAccount.isVerifiedEmail()) {
            request.setAttribute("error", "Email Google chưa được xác minh. Vui lòng xác minh email trước.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        try {
            Staff staff = daoStaff.loginByGoogle(googleAccount);
            if (staff == null) {
                request.setAttribute("error", "Không thể tạo hoặc tìm tài khoản. Vui lòng thử lại.");
                request.setAttribute("googleEnabled", GoogleOAuthConfig.isConfigured());
                request.getRequestDispatcher("/login.jsp").forward(request, response);
                return;
            }
            completeLogin(request, response, staff, true);
        } catch (SQLException ex) {
            System.err.println("[Google Login] DB error: " + ex.getMessage());
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

        // Cache studentId
        session.setAttribute("cachedStudentId", staff.getStaffID());

        redirectByRole(request, response);
    }

    private void redirectByRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (RoleUtils.isStudentOnly(request)) {
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
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

        // Mặc định: Google login → student
        return RoleUtils.ROLE_STUDENT;
    }

    private void forwardLoginError(HttpServletRequest request, HttpServletResponse response,
            String username, String error) throws ServletException, IOException {
        request.setAttribute("error", error);
        request.setAttribute("username", username);
        request.setAttribute("googleEnabled", GoogleOAuthConfig.isConfigured());
        request.getRequestDispatcher("/login.jsp").forward(request, response);
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
