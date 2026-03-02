package controller;

import DAL.DAOStaff;
import DAL.DAOStaffRole;
import entities.Staff;
import entities.StaffRole;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "Register", urlPatterns = {"/register"})
public class Register extends HttpServlet {

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

        String name     = request.getParameter("name").trim();
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password");
        String confirm  = request.getParameter("confirm");

        // Validate
        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            request.setAttribute("name", name);
            request.setAttribute("username", username);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirm)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.setAttribute("name", name);
            request.setAttribute("username", username);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            if (daoStaff.existsByUsername(username)) {
                request.setAttribute("error", "Tên đăng nhập đã tồn tại!");
                request.setAttribute("name", name);
                request.setAttribute("username", username);
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }

            Staff staff = new Staff(0, name, username, password);
            daoStaff.insert(staff); // staff.getStaffID() đã được set sau insert

            // Gán role Student (roleId = 8)
            daoStaffRole.insert(new StaffRole(staff.getStaffID(), 8));

            response.sendRedirect(request.getContextPath() + "/LoginURL?registered=1");

        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}