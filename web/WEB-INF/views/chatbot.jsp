<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trợ lý AI | Library Manager</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/chatbot.css">
    </head>
    <body class="chatbot-page">
        <main class="chatbot-shell" id="chatbotApp"
              data-endpoint="${pageContext.request.contextPath}/chatbot"
              data-configured="${chatbotConfigured}"
              data-model="<c:out value='${chatbotModel}' />">

            <section class="chatbot-hero">
                <div class="chatbot-hero-copy">
                    <a class="chatbot-back-link" href="${chatbotBackUrl}">
                        <span>←</span>
                        <span><c:out value="${chatbotBackLabel}" /></span>
                    </a>

                    <div class="chatbot-eyebrow">Groq AI Assistant</div>
                    <h1>Trợ lý AI cho Library Manager</h1>
                    <p>
                        Hỏi nhanh về quy trình mượn trả, cách dùng hệ thống, tra cứu thao tác trong thư viện
                        hoặc các câu hỏi học tập thông thường. Chatbot ưu tiên trả lời bằng tiếng Việt rõ ràng,
                        ngắn gọn và thực dụng.
                    </p>

                    <div class="chatbot-hero-meta">
                        <span class="chatbot-pill">Người dùng: <c:out value="${chatbotViewerName}" /></span>
                        <span class="chatbot-pill">Vai trò: <c:out value="${chatbotRoleLabel}" /></span>
                        <span class="chatbot-pill">Model: <c:out value="${chatbotModel}" /></span>
                    </div>
                </div>

                <aside class="chatbot-side-panel">
                    <div class="chatbot-side-card">
                        <span class="chatbot-side-kicker">Gợi ý nhanh</span>
                        <button class="chatbot-prompt-chip" type="button" data-chatbot-prompt="Cách gia hạn sách trong hệ thống này là gì?">Cách gia hạn sách</button>
                        <button class="chatbot-prompt-chip" type="button" data-chatbot-prompt="Nếu sinh viên có sách quá hạn thì nên xử lý thế nào?">Xử lý sách quá hạn</button>
                        <button class="chatbot-prompt-chip" type="button" data-chatbot-prompt="Hãy tóm tắt quy trình duyệt phiếu mượn cho nhân viên mới.">Quy trình duyệt phiếu mượn</button>
                    </div>

                    <div class="chatbot-side-card subtle">
                        <span class="chatbot-side-kicker">Lưu ý</span>
                        <ul class="chatbot-side-list">
                            <li>Chatbot không tự đọc dữ liệu DB nội bộ của bạn.</li>
                            <li>Với thao tác đặc thù, hãy xác nhận lại trên giao diện quản trị.</li>
                            <li>API key được đọc từ biến môi trường <code>GROQ_API_KEY</code>.</li>
                        </ul>
                    </div>
                </aside>
            </section>

            <section class="chatbot-panel">
                <c:if test="${not chatbotConfigured}">
                    <div class="chatbot-banner warning" role="alert">
                        GROQ_API_KEY chưa được cấu hình. Hãy thiết lập biến môi trường rồi tải lại trang.
                    </div>
                </c:if>

                <div class="chatbot-thread" id="chatThread" aria-live="polite" aria-label="Lịch sử hội thoại"></div>

                <div class="chatbot-status-row">
                    <div class="chatbot-status" id="chatStatus">Sẵn sàng nhận câu hỏi.</div>
                    <div class="chatbot-model-note" id="chatModelNote">Đang dùng: <c:out value="${chatbotModel}" /></div>
                </div>

                <form class="chatbot-composer" id="chatForm">
                    <label class="sr-only" for="chatInput">Nhập nội dung cần hỏi</label>
                    <textarea id="chatInput" name="message" rows="1"
                              placeholder="Nhập câu hỏi của bạn, ví dụ: làm sao để duyệt phiếu mượn?" maxlength="2500"></textarea>
                    <button type="submit" id="chatSubmit">Gửi</button>
                </form>
            </section>
        </main>

        <script src="${pageContext.request.contextPath}/assets/js/chatbot.js"></script>
    </body>
</html>
