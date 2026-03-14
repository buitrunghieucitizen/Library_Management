package Utils;

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

    public void sendOtpEmail(String toEmail, String otpCode) throws MessagingException {
        Session session = Session.getInstance(buildProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConfig.getUsername(), EmailConfig.getPassword());
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.getFromEmail()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
        message.setSubject("Ma OTP dat lai mat khau", StandardCharsets.UTF_8.name());
        message.setContent(buildOtpContent(otpCode), "text/html; charset=UTF-8");

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
                + "<html><head><meta charset='UTF-8'></head><body style='font-family:Segoe UI,Tahoma,Arial,sans-serif;color:#1f2937'>"
                + "<h2 style='color:#1e3c72'>Khoi phuc mat khau - Library Manager</h2>"
                + "<p>Ban vua yeu cau dat lai mat khau. Ma OTP cua ban la:</p>"
                + "<p style='font-size:28px;font-weight:700;letter-spacing:4px;color:#2a5298'>" + otpCode + "</p>"
                + "<p>Ma co hieu luc trong 10 phut.</p>"
                + "<p>Neu ban khong thuc hien thao tac nay, vui long bo qua email.</p>"
                + "</body></html>";
    }
}
