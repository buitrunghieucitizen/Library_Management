package controller;

import DAL.DAOStaff;
import DAL.DAOStaffRole;
import entities.Staff;
import entities.StaffRole;

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

@WebServlet(name = "Login", urlPatterns = {"/LoginURL"})
public class Login extends HttpServlet {

    private final DAOStaff daoStaff = new DAOStaff();
    private final DAOStaffRole daoStaffRole = new DAOStaffRole();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu đã đăng nhập rồi thì chuyển về trang chủ
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setMaxInactiveInterval(30 * 60);
        }
        if (session != null && session.getAttribute("staff") != null) {

            List<Integer> roles = (List<Integer>) session.getAttribute("roles");

            if (roles != null
                    && roles.contains(8)
                    && roles.stream().noneMatch(r -> r == 1 || r == 2 || r == 3 || r == 4 || r == 7)) {

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
                session.setAttribute("staff", staff);

                // Lấy danh sách roles
                List<StaffRole> staffRoles = daoStaffRole.getByStaffId(staff.getStaffID());
                List<Integer> roleIds = new ArrayList<>();
                for (StaffRole sr : staffRoles) {
                    roleIds.add(sr.getRoleID());
                }
                session.setAttribute("roles", roleIds);

                // ===== Redirect theo role =====
                boolean isStudent = roleIds.contains(8);
                boolean isStaff = roleIds.stream()
                        .anyMatch(r -> r == 1 || r == 2 || r == 3 || r == 4 || r == 7);

                if (isStudent && !isStaff) {
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    response.sendRedirect(request.getContextPath() + "/index.jsp");
                }
            } else {
                // Đăng nhập thất bại
                request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
