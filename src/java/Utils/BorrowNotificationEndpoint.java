package Utils;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket 2 chiều: /ws/notify/admin → tất cả admin/staff lắng nghe
 * /ws/notify/student/5 → student có studentId=5 lắng nghe
 */
@ServerEndpoint("/ws/notify/{channel}")
public class BorrowNotificationEndpoint {

    // Admin sessions
    private static final Set<Session> adminSessions = new CopyOnWriteArraySet<>();

    // Student sessions: key = studentId, value = set of sessions (1 student có thể mở nhiều tab)
    private static final Map<Integer, Set<Session>> studentSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("channel") String channel) {
        if ("admin".equals(channel)) {
            adminSessions.add(session);
            System.out.println("[WS] Admin connected. Total admin: " + adminSessions.size());
        } else if (channel != null && channel.startsWith("student-")) {
            try {
                int studentId = Integer.parseInt(channel.substring(8));
                session.getUserProperties().put("studentId", studentId);
                studentSessions.computeIfAbsent(studentId, k -> new CopyOnWriteArraySet<>()).add(session);
                System.out.println("[WS] Student #" + studentId + " connected.");
            } catch (NumberFormatException e) {
                System.err.println("[WS] Invalid student channel: " + channel);
            }
        }
    }

    @OnClose
    public void onClose(Session session, @PathParam("channel") String channel) {
        if ("admin".equals(channel)) {
            adminSessions.remove(session);
        } else {
            Object sid = session.getUserProperties().get("studentId");
            if (sid instanceof Integer) {
                Set<Session> sessions = studentSessions.get((Integer) sid);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        studentSessions.remove((Integer) sid);
                    }
                }
            }
        }
    }

    // ============ GỬI TỚI ADMIN ============
    /**
     * Thông báo tới tất cả admin: có phiếu mượn mới từ student.
     */
    public static void notifyAdminNewBorrow(int borrowId, String studentName, int bookCount) {
        String json = "{\"type\":\"NEW_BORROW\""
                + ",\"borrowId\":" + borrowId
                + ",\"studentName\":\"" + esc(studentName) + "\""
                + ",\"bookCount\":" + bookCount
                + ",\"message\":\"Phiếu mượn mới #" + borrowId + " từ " + esc(studentName)
                + " (" + bookCount + " quyển)\"" + "}";
        sendToAdmins(json);
    }

    /**
     * Thông báo tới admin: có student đặt giữ chỗ.
     */
    public static void notifyAdminNewHold(String studentName, String bookName) {
        String json = "{\"type\":\"NEW_HOLD\""
                + ",\"studentName\":\"" + esc(studentName) + "\""
                + ",\"bookName\":\"" + esc(bookName) + "\""
                + ",\"message\":\"" + esc(studentName) + " đặt giữ chỗ sách \\\"" + esc(bookName) + "\\\"\"" + "}";
        sendToAdmins(json);
    }

    // ============ GỬI TỚI STUDENT ============
    /**
     * Thông báo tới 1 student cụ thể: phiếu mượn đã được duyệt.
     */
    public static void notifyStudentApproved(int studentId, int borrowId) {
        String json = "{\"type\":\"BORROW_APPROVED\""
                + ",\"borrowId\":" + borrowId
                + ",\"message\":\"Phiếu mượn #" + borrowId + " đã được duyệt! Bạn có thể đến lấy sách.\"" + "}";
        sendToStudent(studentId, json);
    }

    /**
     * Thông báo tới 1 student: phiếu mượn bị từ chối.
     */
    public static void notifyStudentRejected(int studentId, int borrowId) {
        String json = "{\"type\":\"BORROW_REJECTED\""
                + ",\"borrowId\":" + borrowId
                + ",\"message\":\"Phiếu mượn #" + borrowId + " đã bị từ chối.\"" + "}";
        sendToStudent(studentId, json);
    }

    /**
     * Thông báo tới 1 student: sách đã có sẵn (hold notification).
     */
    public static void notifyStudentBookAvailable(int studentId, String bookName) {
        String json = "{\"type\":\"BOOK_AVAILABLE\""
                + ",\"bookName\":\"" + esc(bookName) + "\""
                + ",\"message\":\"Sách \\\"" + esc(bookName) + "\\\" đã có sẵn! Bạn có 24h để mượn.\"" + "}";
        sendToStudent(studentId, json);
    }

    /**
     * Thông báo tới 1 student: admin đã xác nhận trả sách.
     */
    public static void notifyStudentReturnConfirmed(int studentId, int borrowId) {
        String json = "{\"type\":\"RETURN_CONFIRMED\""
                + ",\"borrowId\":" + borrowId
                + ",\"message\":\"Phiếu mượn #" + borrowId + " đã được xác nhận trả thành công.\"" + "}";
        sendToStudent(studentId, json);
    }

    // ============ INTERNAL ============
    private static void sendToAdmins(String json) {
        for (Session s : adminSessions) {
            send(s, json);
        }
    }

    private static void sendToStudent(int studentId, String json) {
        Set<Session> sessions = studentSessions.get(studentId);
        if (sessions == null) {
            return;
        }
        for (Session s : sessions) {
            send(s, json);
        }
    }

    private static void send(Session session, String json) {
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(json);
            } catch (IOException e) {
                System.err.println("[WS] Send error: " + e.getMessage());
            }
        }
    }

    private static String esc(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }
}
