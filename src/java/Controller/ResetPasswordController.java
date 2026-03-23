package Controller;

import Model.DAOStaff;
import Utils.PasswordResetUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ResetPasswordController", urlPatterns = {"/reset-password"})
public class ResetPasswordController extends HttpServlet {

    private final DAOStaff daoStaff = new DAOStaff();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!isOtpVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (!isOtpVerified(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password == null || confirmPassword == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Xac nhan mat khau khong khop.");
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }

        if (!PasswordResetUtils.isValidPassword(password)) {
            request.setAttribute("error", "Mat khau phai >= 6 ky tu, co chu hoa, chu thuong va so.");
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }

        String username = (String) session.getAttribute(ForgotPasswordController.RESET_USERNAME);
        if (username == null || username.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        try {
            int updated = daoStaff.updatePasswordByUsername(username, password);
            if (updated <= 0) {
                request.setAttribute("error", "Khong cap nhat duoc mat khau. Vui long thu lai.");
                request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
                return;
            }
        } catch (SQLException ex) {
            request.setAttribute("error", "Loi he thong: " + ex.getMessage());
            request.getRequestDispatcher("/reset-password.jsp").forward(request, response);
            return;
        }

        clearSession(session);
        response.sendRedirect(request.getContextPath() + "/LoginURL?reset=1");
    }

    private boolean isOtpVerified(HttpSession session) {
        return session != null
                && session.getAttribute(ForgotPasswordController.RESET_USERNAME) != null
                && Boolean.TRUE.equals(session.getAttribute(ForgotPasswordController.RESET_OTP_VERIFIED));
    }

    private void clearSession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(ForgotPasswordController.RESET_USERNAME);
        ForgotPasswordController.clearResetState(session);
    }
}
