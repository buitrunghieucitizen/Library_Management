package Utils;

import Entities.BookHold;
import Entities.Staff;
import Model.DAOBookHold;
import Model.DAOStaff;
import Model.DBConnection;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoldNotificationService {
    
    private static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    private final DAOBookHold daoBookHold;
    private final DAOStaff daoStaff;
    private final EmailService emailService;

    public HoldNotificationService() {
        this.daoBookHold = new DAOBookHold();
        this.daoStaff = new DAOStaff();
        this.emailService = new EmailService();
    }

    public boolean processHoldQueue(int bookId, String bookName) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) {
                return false;
            }

            BookHold nextHold = daoBookHold.getNextWaiting(con, bookId);
            if (nextHold == null) {
                return false;
            }

            int updated = daoBookHold.markNotified(con, nextHold.getHoldID());
            if (updated == 0) {
                return false;
            }

            // Lấy email từ Staff
            Staff staff = daoStaff.getById(nextHold.getStudentID());
            if (staff == null || staff.getEmail() == null || staff.getEmail().isBlank()) {
                System.out.println("[HoldNotification] Hold marked nhưng không có email. StaffID=" + nextHold.getStudentID());
                return true;
            }

            // GỬI EMAIL BẤT ĐỒNG BỘ
            final String toEmail = staff.getEmail();
            final String toName = staff.getStaffName();
            emailExecutor.submit(() -> {
                try {
                    sendHoldAvailableEmail(toEmail, toName, bookName);
                } catch (Exception e) {
                    System.err.println("[HoldNotification] Async email error: " + e.getMessage());
                }
            });

            // WebSocket thông báo student ngay lập tức (không chờ email)
            NotificationBroadcaster.notifyStudentBookAvailable(nextHold.getStudentID(), bookName);

            return true;

        } catch (Exception e) {
            System.err.println("[HoldNotification] Error bookId=" + bookId + ": " + e.getMessage());
            return false;
        } finally {
            if (con != null) try {
                con.close();
            } catch (SQLException ignored) {
            }
        }
    }

    private void sendHoldAvailableEmail(String email, String name, String bookName) {
        if (!EmailConfig.isConfigured()) {
            System.err.println("[HoldNotification] Email chưa cấu hình.");
            return;
        }
        try {
            String subject = "Sách \"" + bookName + "\" đã có sẵn — Thư viện";
            String html = buildAvailableEmailHtml(name, bookName);
            emailService.sendHtml(email, subject, html);
            System.out.println("[HoldNotification] Đã gửi email tới " + email);
        } catch (Exception e) {
            System.err.println("[HoldNotification] Lỗi gửi email: " + e.getMessage());
        }
    }

    private String buildAvailableEmailHtml(String name, String bookName) {
        return "<!DOCTYPE html>"
                + "<html><head><meta charset='UTF-8'></head>"
                + "<body style='font-family:Segoe UI,Tahoma,Arial,sans-serif;color:#1f2937;max-width:600px;margin:0 auto;'>"
                + "<div style='background:linear-gradient(135deg,#1a2744,#2a5298);padding:24px;border-radius:12px 12px 0 0;'>"
                + "<h2 style='color:#fff;margin:0;'>Sách đã có sẵn!</h2></div>"
                + "<div style='background:#fff;padding:24px;border:1px solid #e2e8f0;border-top:none;border-radius:0 0 12px 12px;'>"
                + "<p>Xin chào <strong>" + esc(name) + "</strong>,</p>"
                + "<p>Sách bạn đặt giữ chỗ đã có sẵn:</p>"
                + "<div style='background:#f0fdf4;border:1px solid #bbf7d0;border-left:4px solid #16a34a;border-radius:8px;padding:16px;margin:16px 0;'>"
                + "<strong style='font-size:18px;color:#166534;'>" + esc(bookName) + "</strong></div>"
                + "<p>Bạn có <strong>48 giờ</strong> để đến thư viện mượn sách.</p>"
                + "<p style='margin-top:24px;'>Trân trọng,<br><strong>Hệ thống quản lý thư viện</strong></p>"
                + "</div></body></html>";
    }

    private String esc(String t) {
        return t == null ? "" : t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
