package Utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

    /**
     * Send OTP email (existing — giữ nguyên).
     */
    public void sendOtpEmail(String toEmail, String otpCode) throws MessagingException, UnsupportedEncodingException {
        String subject = "Mã OTP đặt lại mật khẩu";
        String html = buildOtpContent(otpCode);
        sendHtml(toEmail, subject, html);
    }

    /**
     * NEW: Generic send HTML email. Used by HoldNotificationService and any
     * future email needs.
     */
    public void sendHtml(String toEmail, String subject, String htmlContent) throws MessagingException, UnsupportedEncodingException {
        if (!EmailConfig.isConfigured()) {
            throw new MessagingException("Email chưa được cấu hình (MAIL_USERNAME / MAIL_PASSWORD trống).");
        }

        Session session = Session.getInstance(buildProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.getUsername(), EmailConfig.getPassword());
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.getFromEmail(), EmailConfig.getFromName(), "UTF-8"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject(subject, StandardCharsets.UTF_8.name());
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    private Properties buildProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", EmailConfig.getSmtpHost());
        props.put("mail.smtp.port", EmailConfig.getSmtpPort());
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(EmailConfig.isStartTlsEnabled()));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    private String buildOtpContent(String otpCode) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='font-family:Segoe UI,Tahoma,Arial,sans-serif;color:#1f2937'>"
                + "<h2 style='color:#1e3c72'>Khôi phục mật khẩu — Library Manager</h2>"
                + "<p>Bạn vừa yêu cầu đặt lại mật khẩu. Mã OTP của bạn là:</p>"
                + "<p style='font-size:28px;font-weight:700;letter-spacing:4px;color:#2a5298'>" + otpCode + "</p>"
                + "<p>Mã có hiệu lực trong 10 phút.</p>"
                + "<p>Nếu bạn không thực hiện thao tác này, vui lòng bỏ qua email.</p>"
                + "</body></html>";
    }
}
