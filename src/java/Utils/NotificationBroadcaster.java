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

public class NotificationBroadcaster {

    private static final Set<Session> adminSessions = new CopyOnWriteArraySet<>();
    private static final Map<Integer, Set<Session>> studentSessions = new ConcurrentHashMap<>();

    @ServerEndpoint("/ws/notify/admin")
    public static class AdminEndpoint {

        @OnOpen
        public void onOpen(Session s) {
            adminSessions.add(s);
            System.out.println("[WS] Admin connected. Total: " + adminSessions.size());
        }

        @OnClose
        public void onClose(Session s) {
            adminSessions.remove(s);
        }
    }

    @ServerEndpoint("/ws/notify/student/{id}")
    public static class StudentEndpoint {

        @OnOpen
        public void onOpen(Session s, @PathParam("id") int id) {
            s.getUserProperties().put("sid", id);
            studentSessions.computeIfAbsent(id, k -> new CopyOnWriteArraySet<>()).add(s);
            System.out.println("[WS] Student #" + id + " connected.");
        }

        @OnClose
        public void onClose(Session s) {
            Object id = s.getUserProperties().get("sid");
            if (id instanceof Integer) {
                Set<Session> set = studentSessions.get(id);
                if (set != null) {
                    set.remove(s);
                    if (set.isEmpty()) {
                        studentSessions.remove(id);
                    }
                }
            }
        }
    }

    // === Send methods ===
    public static void notifyAdminNewBorrow(int borrowId, String name, int count) {
        sendAdmin("{\"type\":\"NEW_BORROW\",\"message\":\"Phiếu mượn mới #" + borrowId + " từ " + esc(name) + " (" + count + " quyển)\"}");
    }

    public static void notifyAdminNewHold(String name, String book) {
        sendAdmin("{\"type\":\"NEW_HOLD\",\"message\":\"" + esc(name) + " đặt giữ chỗ \\\"" + esc(book) + "\\\"\"}");
    }

    public static void notifyStudentApproved(int sid, int bid) {
        sendStudent(sid, "{\"type\":\"BORROW_APPROVED\",\"message\":\"Phiếu mượn #" + bid + " đã được duyệt!\"}");
    }

    public static void notifyStudentRejected(int sid, int bid) {
        sendStudent(sid, "{\"type\":\"BORROW_REJECTED\",\"message\":\"Phiếu mượn #" + bid + " đã bị từ chối.\"}");
    }

    public static void notifyStudentReturnConfirmed(int sid, int bid) {
        sendStudent(sid, "{\"type\":\"RETURN_CONFIRMED\",\"message\":\"Phiếu #" + bid + " đã xác nhận trả thành công.\"}");
    }

    public static void notifyStudentBookAvailable(int sid, String book) {
        sendStudent(sid, "{\"type\":\"BOOK_AVAILABLE\",\"message\":\"Sách \\\"" + esc(book) + "\\\" đã có sẵn!\"}");
    }

    /**
     * Broadcast tới TẤT CẢ student đang online: sách vừa thay đổi available.
     * Client sẽ reload trang để cập nhật số lượng.
     */
    public static void notifyAllStudentsBookChanged(int bookId, String bookName, int newAvailable) {
        String json = "{\"type\":\"BOOK_CHANGED\""
                + ",\"bookId\":" + bookId
                + ",\"bookName\":\"" + esc(bookName) + "\""
                + ",\"available\":" + newAvailable
                + ",\"message\":\"Sách \\\"" + esc(bookName) + "\\\" vừa cập nhật: còn " + newAvailable + " quyển.\"}";
        // Gửi tới TẤT CẢ student sessions
        for (var entry : studentSessions.entrySet()) {
            for (Session s : entry.getValue()) {
                send(s, json);
            }
        }
    }

    private static void sendAdmin(String json) {
        for (Session s : adminSessions) {
            send(s, json);
        }
    }

    private static void sendStudent(int id, String json) {
        Set<Session> set = studentSessions.get(id);
        if (set != null) {
            for (Session s : set) {
                send(s, json);
            }
        }
    }

    private static void send(Session s, String json) {
        if (s.isOpen()) try {
            s.getBasicRemote().sendText(json);
        } catch (IOException e) {
        }
    }

    private static String esc(String t) {
        return t == null ? "" : t.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
