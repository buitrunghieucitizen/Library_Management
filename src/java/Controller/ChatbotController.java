package Controller;

import Entities.Staff;
import Utils.GroqChatService;
import Utils.RoleUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ChatbotController", urlPatterns = {"/chatbot"})
public class ChatbotController extends HttpServlet {

    private final GroqChatService groqChatService = new GroqChatService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Staff viewer = RoleUtils.getLoggedStaff(request);
        if (viewer == null) {
            response.sendRedirect(request.getContextPath() + "/LoginURL");
            return;
        }

        request.setAttribute("chatbotConfigured", groqChatService.isConfigured());
        request.setAttribute("chatbotModel", groqChatService.getModel());
        request.setAttribute("chatbotViewerName", resolveViewerName(viewer));
        request.setAttribute("chatbotRoleLabel", resolveRoleLabel(request));
        request.setAttribute("chatbotBackUrl", request.getContextPath() + resolveBackPath(request));
        request.setAttribute("chatbotBackLabel", resolveBackLabel(request));
        request.getRequestDispatcher("/WEB-INF/views/chatbot.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Staff viewer = RoleUtils.getLoggedStaff(request);
        if (viewer == null) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập để dùng chatbot.");
            return;
        }

        ChatRequest payload;
        try {
            payload = gson.fromJson(request.getReader(), ChatRequest.class);
        } catch (Exception ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Không đọc được dữ liệu gửi lên.");
            return;
        }

        List<GroqChatService.ChatMessage> messages = mapMessages(payload);
        if (messages.isEmpty()) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Vui lòng nhập câu hỏi của bạn.");
            return;
        }

        try {
            GroqChatService.ChatResult result = groqChatService.chat(messages);
            JsonObject json = new JsonObject();
            json.addProperty("reply", result.getReply());
            json.addProperty("model", result.getModel());
            response.getWriter().write(gson.toJson(json));
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "Nội dung hội thoại không hợp lệ.");
        } catch (IllegalStateException ex) {
            writeError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Chatbot chưa được cấu hình GROQ_API_KEY.");
        } catch (GroqChatService.ChatServiceException ex) {
            getServletContext().log("Groq upstream error: " + ex.getMessage(), ex);
            int status = (ex.getStatusCode() == 401 || ex.getStatusCode() == 403)
                    ? HttpServletResponse.SC_SERVICE_UNAVAILABLE
                    : HttpServletResponse.SC_BAD_GATEWAY;
            writeError(response, status, ex.getMessage());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            writeError(response, HttpServletResponse.SC_GATEWAY_TIMEOUT, "Yêu cầu tới Groq bị gián đoạn.");
        } catch (IOException ex) {
            getServletContext().log("Chatbot I/O error", ex);
            writeError(response, HttpServletResponse.SC_BAD_GATEWAY, "Không thể kết nối tới Groq.");
        }
    }

    private List<GroqChatService.ChatMessage> mapMessages(ChatRequest payload) {
        List<GroqChatService.ChatMessage> messages = new ArrayList<>();
        if (payload == null || payload.messages == null) {
            return messages;
        }

        for (ChatRequestMessage message : payload.messages) {
            if (message == null) {
                continue;
            }
            messages.add(new GroqChatService.ChatMessage(message.role, message.content));
        }
        return messages;
    }

    private void writeError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JsonObject json = new JsonObject();
        json.addProperty("error", message);
        response.getWriter().write(gson.toJson(json));
    }

    private String resolveViewerName(Staff viewer) {
        if (viewer == null || viewer.getStaffName() == null || viewer.getStaffName().trim().isEmpty()) {
            return "Người dùng thư viện";
        }
        return viewer.getStaffName().trim();
    }

    private String resolveRoleLabel(HttpServletRequest request) {
        if (RoleUtils.isAdmin(request)) {
            return "Quản trị";
        }
        if (RoleUtils.isStaff(request)) {
            return "Nhân viên";
        }
        if (RoleUtils.isStudentOnly(request)) {
            return "Sinh viên";
        }
        return "Người dùng";
    }

    private String resolveBackPath(HttpServletRequest request) {
        if (RoleUtils.isStudentOnly(request)) {
            return "/home";
        }
        if (RoleUtils.isAdmin(request) || RoleUtils.isStaff(request)) {
            return "/admin/dashboard";
        }
        return "/index.jsp";
    }

    private String resolveBackLabel(HttpServletRequest request) {
        return RoleUtils.isStudentOnly(request) ? "Về cổng sinh viên" : "Về bảng điều khiển";
    }

    private static final class ChatRequest {
    
        private List<ChatRequestMessage> messages;
    }

    private static final class ChatRequestMessage {

        private String role;
        private String content;
    }
}
