package Controller;

import Entities.Staff;
import Model.DAOStaff;
import Utils.EmailConfig;
import Utils.EmailService;
import Utils.PasswordResetUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import javax.mail.MessagingException;

@WebServlet(name = "ForgotPasswordController", urlPatterns = {"/forgot-password"})
public class ForgotPasswordController extends HttpServlet {

    static final String RESET_USERNAME = "resetUsername";
    static final String RESET_OTP_HASH = "resetOtpHash";
    static final String RESET_OTP_EXPIRES_AT = "resetOtpExpiresAt";
    static final String RESET_OTP_ATTEMPTS = "resetOtpAttempts";
    static final String RESET_OTP_VERIFIED = "resetOtpVerified";

    private static final long OTP_TTL_MILLIS = 10 * 60 * 1000L;

    private final DAOStaff daoStaff = new DAOStaff();
    private final EmailService emailService = new EmailService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        if (!EmailConfig.isConfigured()) {
            forwardWithError(request, response, "", "He thong chua cau hinh email (MAIL_USERNAME/MAIL_PASSWORD).");
            return;
        }

        String emailInput = trim(request.getParameter("email"));
        if (emailInput.isEmpty()) {
            forwardWithError(request, response, "", "Vui long nhap email.");
            return;
        }

        if (!PasswordResetUtils.isEmail(emailInput)) {
            forwardWithError(request, response, emailInput, "Email khong hop le.");
            return;
        }

        Staff staff;
        try {
            staff = daoStaff.getByEmail(emailInput);
            if (staff == null) {
                staff = daoStaff.getByUsername(emailInput);
            }
        } catch (SQLException ex) {
            forwardWithError(request, response, emailInput, "Loi he thong: " + ex.getMessage());
            return;
        }

        if (staff == null) {
            forwardWithError(request, response, emailInput, "Khong tim thay tai khoan voi email nay.");
            return;
        }

        String recoveryEmail = staff.getEmail();
        if (!PasswordResetUtils.isEmail(recoveryEmail)) {
            recoveryEmail = staff.getUsername();
        }

        if (!PasswordResetUtils.isEmail(recoveryEmail)) {
            forwardWithError(request, response, emailInput, "Tai khoan nay chua co email de nhan OTP.");
            return;
        }

        String otp = PasswordResetUtils.generateOtp();
        HttpSession session = request.getSession();
        clearResetState(session);

        session.setAttribute(RESET_USERNAME, staff.getUsername());
        session.setAttribute(RESET_OTP_HASH, PasswordResetUtils.sha256(otp));
        session.setAttribute(RESET_OTP_EXPIRES_AT, System.currentTimeMillis() + OTP_TTL_MILLIS);
        session.setAttribute(RESET_OTP_ATTEMPTS, 0);
        session.setAttribute(RESET_OTP_VERIFIED, Boolean.FALSE);

        try {
            emailService.sendOtpEmail(recoveryEmail, otp);
        } catch (MessagingException ex) {
            clearResetState(session);
            forwardWithError(request, response, emailInput, "Gui email OTP that bai. Vui long thu lai sau.");
            return;
        }

        request.setAttribute("message", "OTP da duoc gui den email dang ky.");
        request.setAttribute("username", staff.getUsername());
        request.getRequestDispatcher("/verify-otp.jsp").forward(request, response);
    }

    static void clearResetState(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(RESET_USERNAME);
        session.removeAttribute(RESET_OTP_HASH);
        session.removeAttribute(RESET_OTP_EXPIRES_AT);
        session.removeAttribute(RESET_OTP_ATTEMPTS);
        session.removeAttribute(RESET_OTP_VERIFIED);
    }

    private void forwardWithError(HttpServletRequest request, HttpServletResponse response, String email, String error)
            throws ServletException, IOException {
        request.setAttribute("email", email);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
