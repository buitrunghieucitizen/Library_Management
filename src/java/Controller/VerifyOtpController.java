package Controller;

import Utils.PasswordResetUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "VerifyOtpController", urlPatterns = {"/verify-otp"})
public class VerifyOtpController extends HttpServlet {

    private static final int MAX_ATTEMPTS = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (!hasPendingReset(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        request.setAttribute("username", session.getAttribute(ForgotPasswordController.RESET_USERNAME));
        request.getRequestDispatcher("/verify-otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (!hasPendingReset(session)) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }

        String otpInput = trim(request.getParameter("otp"));
        request.setAttribute("username", session.getAttribute(ForgotPasswordController.RESET_USERNAME));

        Long expiresAt = (Long) session.getAttribute(ForgotPasswordController.RESET_OTP_EXPIRES_AT);
        if (expiresAt == null || System.currentTimeMillis() > expiresAt) {
            ForgotPasswordController.clearResetState(session);
            request.setAttribute("error", "OTP da het han. Vui long gui lai OTP moi.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        Integer attempts = (Integer) session.getAttribute(ForgotPasswordController.RESET_OTP_ATTEMPTS);
        if (attempts == null) {
            attempts = 0;
        }
        if (attempts >= MAX_ATTEMPTS) {
            ForgotPasswordController.clearResetState(session);
            request.setAttribute("error", "Ban da nhap sai qua so lan cho phep. Vui long gui lai OTP moi.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
            return;
        }

        String expectedHash = (String) session.getAttribute(ForgotPasswordController.RESET_OTP_HASH);
        if (expectedHash == null || !expectedHash.equals(PasswordResetUtils.sha256(otpInput))) {
            attempts++;
            session.setAttribute(ForgotPasswordController.RESET_OTP_ATTEMPTS, attempts);
            if (attempts >= MAX_ATTEMPTS) {
                ForgotPasswordController.clearResetState(session);
                request.setAttribute("error", "Ban da nhap sai qua so lan cho phep. Vui long gui lai OTP moi.");
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                return;
            }
            request.setAttribute("error", "OTP khong dung. Ban con " + (MAX_ATTEMPTS - attempts) + " lan thu.");
            request.getRequestDispatcher("/verify-otp.jsp").forward(request, response);
            return;
        }

        session.setAttribute(ForgotPasswordController.RESET_OTP_VERIFIED, Boolean.TRUE);
        session.removeAttribute(ForgotPasswordController.RESET_OTP_HASH);
        session.removeAttribute(ForgotPasswordController.RESET_OTP_EXPIRES_AT);
        session.removeAttribute(ForgotPasswordController.RESET_OTP_ATTEMPTS);

        response.sendRedirect(request.getContextPath() + "/reset-password");
    }

    private boolean hasPendingReset(HttpSession session) {
        return session != null
                && session.getAttribute(ForgotPasswordController.RESET_USERNAME) != null
                && session.getAttribute(ForgotPasswordController.RESET_OTP_HASH) != null;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
