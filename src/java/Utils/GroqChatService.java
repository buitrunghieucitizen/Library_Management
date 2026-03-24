package Utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GroqChatService {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "llama-3.1-8b-instant";
    private static final int MAX_CONTEXT_MESSAGES = 12;
    private static final int MAX_MESSAGE_LENGTH = 2500;
    private static final int MAX_COMPLETION_TOKENS = 700;
    private static final Duration HTTP_TIMEOUT = Duration.ofSeconds(45);
    private static final String SYSTEM_PROMPT
            = "Ban la tro ly AI cho he thong Library Manager. "
            + "Hay tra loi bang tieng Viet, ngan gon, ro rang va uu tien huong dan thuc te. "
            + "Neu cau hoi lien quan den sach, muon tra, quy trinh thu vien hoac cach dung he thong, hay tra loi theo ngu canh thu vien. "
            + "Neu khong du du lieu dac thu, hay noi ro gioi han thay vi bia thong tin.";

    private static final Gson GSON = new Gson();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public boolean isConfigured() {
        return !getApiKey().isEmpty();
    }

    public String getModel() {
        String fromProperty = trim(System.getProperty("GROQ_MODEL"));
        if (!fromProperty.isEmpty()) {
            return fromProperty;
        }

        String fromEnv = trim(System.getenv("GROQ_MODEL"));
        if (!fromEnv.isEmpty()) {
            return fromEnv;
        }

        return DEFAULT_MODEL;
    }

    public ChatResult chat(List<ChatMessage> conversation)
            throws ChatServiceException, IOException, InterruptedException {
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Chatbot chua duoc cau hinh GROQ_API_KEY.");
        }

        List<ChatMessage> sanitizedConversation = sanitizeConversation(conversation);
        if (sanitizedConversation.isEmpty()) {
            throw new IllegalArgumentException("Noi dung hoi thoai khong hop le.");
        }

        JsonObject payload = new JsonObject();
        payload.addProperty("model", getModel());
        payload.addProperty("temperature", 0.4d);
        payload.addProperty("max_completion_tokens", MAX_COMPLETION_TOKENS);

        JsonArray messages = new JsonArray();
        messages.add(toMessageJson("system", SYSTEM_PROMPT));
        for (ChatMessage message : sanitizedConversation) {
            messages.add(toMessageJson(message.getRole(), message.getContent()));
        }
        payload.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                .timeout(HTTP_TIMEOUT)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(payload), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new ChatServiceException(response.statusCode(), extractErrorMessage(response.body()));
        }

        JsonObject responseJson = new JsonParser().parse(response.body()).getAsJsonObject();
        String reply = extractReply(responseJson);
        if (reply.isEmpty()) {
            throw new ChatServiceException(502, "Groq khong tra ve noi dung phan hoi.");
        }

        String model = responseJson.has("model") && !responseJson.get("model").isJsonNull()
                ? responseJson.get("model").getAsString()
                : getModel();

        return new ChatResult(reply, model);
    }

    private List<ChatMessage> sanitizeConversation(List<ChatMessage> conversation) {
        ArrayDeque<ChatMessage> queue = new ArrayDeque<>();
        if (conversation == null) {
            return List.of();
        }

        for (ChatMessage rawMessage : conversation) {
            if (rawMessage == null) {
                continue;
            }

            String role = normalizeRole(rawMessage.getRole());
            String content = trim(rawMessage.getContent());
            if (role == null || content.isEmpty()) {
                continue;
            }

            if (content.length() > MAX_MESSAGE_LENGTH) {
                content = content.substring(0, MAX_MESSAGE_LENGTH);
            }

            queue.addLast(new ChatMessage(role, content));
            while (queue.size() > MAX_CONTEXT_MESSAGES) {
                queue.removeFirst();
            }
        }

        if (queue.isEmpty() || !"user".equals(queue.peekLast().getRole())) {
            return List.of();
        }

        return new ArrayList<>(queue);
    }

    private JsonObject toMessageJson(String role, String content) {
        JsonObject message = new JsonObject();
        message.addProperty("role", role);
        message.addProperty("content", content);
        return message;
    }

    private String extractReply(JsonObject responseJson) {
        if (responseJson == null || !responseJson.has("choices")) {
            return "";
        }

        JsonArray choices = responseJson.getAsJsonArray("choices");
        if (choices == null || choices.size() == 0) {
            return "";
        }

        JsonObject choice = choices.get(0).getAsJsonObject();
        if (choice == null || !choice.has("message")) {
            return "";
        }

        JsonObject message = choice.getAsJsonObject("message");
        if (message == null || !message.has("content")) {
            return "";
        }

        JsonElement content = message.get("content");
        if (content == null || content.isJsonNull()) {
            return "";
        }

        if (content.isJsonPrimitive()) {
            return trim(content.getAsString());
        }

        if (!content.isJsonArray()) {
            return trim(content.toString());
        }

        StringBuilder builder = new StringBuilder();
        JsonArray parts = content.getAsJsonArray();
        for (JsonElement partElement : parts) {
            if (partElement == null || partElement.isJsonNull()) {
                continue;
            }

            if (partElement.isJsonPrimitive()) {
                builder.append(partElement.getAsString());
                continue;
            }

            JsonObject part = partElement.getAsJsonObject();
            if (part.has("text") && !part.get("text").isJsonNull()) {
                builder.append(part.get("text").getAsString());
            }
        }

        return trim(builder.toString());
    }

    private String extractErrorMessage(String responseBody) {
        String fallback = "Khong the nhan phan hoi tu Groq.";
        String trimmedBody = trim(responseBody);
        if (trimmedBody.isEmpty()) {
            return fallback;
        }

        try {
            JsonObject responseJson = new JsonParser().parse(trimmedBody).getAsJsonObject();
            JsonObject error = responseJson.getAsJsonObject("error");
            if (error != null && error.has("message") && !error.get("message").isJsonNull()) {
                return error.get("message").getAsString();
            }
        } catch (Exception ignored) {
        }

        return trimmedBody.length() > 200 ? trimmedBody.substring(0, 200) : trimmedBody;
    }

    private String normalizeRole(String role) {
        String normalizedRole = trim(role).toLowerCase(Locale.ROOT);
        if ("user".equals(normalizedRole) || "assistant".equals(normalizedRole)) {
            return normalizedRole;
        }
        return null;
    }

    private String getApiKey() {
        String fromProperty = trim(System.getProperty("GROQ_API_KEY"));
        if (!fromProperty.isEmpty()) {
            return fromProperty;
        }

        return trim(System.getenv("GROQ_API_KEY"));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static final class ChatMessage {

        private final String role;
        private final String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static final class ChatResult {

        private final String reply;
        private final String model;

        public ChatResult(String reply, String model) {
            this.reply = reply;
            this.model = model;
        }

        public String getReply() {
            return reply;
        }

        public String getModel() {
            return model;
        }
    }

    public static final class ChatServiceException extends Exception {

        private final int statusCode;

        public ChatServiceException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
