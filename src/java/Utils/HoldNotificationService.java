package Utils;

import Entities.BookHold;
import Entities.Student;
import Model.DAOBookHold;
import Model.DAOStudent;
import Model.DBConnection;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.mail.MessagingException;

/**
 * Service that handles hold notifications: - When a book is returned, check
 * hold queue → notify first student in line - Send email with "Sách đã có sẵn"
 * message
 */
public class HoldNotificationService {

    private final DAOBookHold daoBookHold;
    private final DAOStudent daoStudent;
    private final EmailService emailService;

    public HoldNotificationService() {
        this.daoBookHold = new DAOBookHold();
        this.daoStudent = new DAOStudent();
        this.emailService = new EmailService();
    }

    public HoldNotificationService(DAOBookHold daoBookHold, DAOStudent daoStudent,
            EmailService emailService) {
        this.daoBookHold = daoBookHold;
        this.daoStudent = daoStudent;
        this.emailService = emailService;
    }

    /**
     * Called AFTER a book is returned (Available increased). Checks if anyone
     * is waiting for this book → notifies them via email.
     *
     * @param bookId the returned book
     * @param bookName book name for email content
     * @return true if someone was notified, false if no one waiting
     */
    public boolean processHoldQueue(int bookId, String bookName) throws UnsupportedEncodingException {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                return false;
            }

            // Get first person in hold queue
            BookHold nextHold = daoBookHold.getNextWaiting(con, bookId);
            if (nextHold == null) {
                return false;
            }

            // Mark as Notified (24h to pick up)
            int updated = daoBookHold.markNotified(con, nextHold.getHoldID());
            if (updated == 0) {
                return false;
            }

            // Get student info for email
            Student student = daoStudent.getById(nextHold.getStudentID());
            if (student == null || student.getEmail() == null || student.getEmail().isBlank()) {
                return true; // hold marked but can't email
            }

            // Send email notification
            sendHoldAvailableEmail(student, bookName);
            return true;

        } catch (SQLException e) {
            System.err.println("[HoldNotification] SQL error processing hold for bookId=" + bookId + ": " + e.getMessage());
            return false;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    /**
     * Send "book available" email to student.
     */
    private void sendHoldAvailableEmail(Student student, String bookName) throws UnsupportedEncodingException {
        if (!EmailConfig.isConfigured()) {
            System.err.println("[HoldNotification] Email chưa cấu hình. Bỏ qua gửi mail cho " + student.getEmail());
            return;
        }

        try {
            String subject = "Sách \"" + bookName + "\" đã có sẵn — Thư viện";
            String html = buildAvailableEmailHtml(student.getStudentName(), bookName);
            emailService.sendHtml(student.getEmail(), subject, html);
            System.out.println("[HoldNotification] Đã gửi email thông báo tới " + student.getEmail() + " cho sách: " + bookName);
        } catch (MessagingException e) {
            System.err.println("[HoldNotification] Lỗi gửi email tới " + student.getEmail() + ": " + e.getMessage());
        }
    }

    private String buildAvailableEmailHtml(String studentName, String bookName) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='font-family:Segoe UI,Tahoma,Arial,sans-serif;color:#1f2937;max-width:600px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#1a2744,#2a5298);padding:24px;border-radius:12px 12px 0 0;'>"
                + "<h2 style='color:#fff;margin:0;'>Sách đã có sẵn!</h2>"
                + "</div>"
                + "<div style='background:#fff;padding:24px;border:1px solid #e2e8f0;border-top:none;border-radius:0 0 12px 12px;'>"
                + "<p>Xin chào <strong>" + escapeHtml(studentName) + "</strong>,</p>"
                + "<p>Sách mà bạn đặt giữ chỗ đã có sẵn:</p>"
                + "<div style='background:#f0fdf4;border:1px solid #bbf7d0;border-left:4px solid #16a34a;border-radius:8px;padding:16px;margin:16px 0;'>"
                + "<strong style='font-size:18px;color:#166534;'>" + escapeHtml(bookName) + "</strong>"
                + "</div>"
                + "<p>Bạn có <strong>24 giờ</strong> để đến thư viện mượn sách. Sau thời gian này, quyền ưu tiên sẽ chuyển cho người tiếp theo trong hàng chờ.</p>"
                + "<p style='margin-top:24px;'>Trân trọng,<br><strong>Hệ thống quản lý thư viện</strong></p>"
                + "</div>"
                + "</body></html>";
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
