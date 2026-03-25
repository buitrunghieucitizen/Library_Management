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
              data-model="<c:out value='${chatbotModel}' />"
              data-back-url="${chatbotBackUrl}"
              data-role-label="<c:out value='${chatbotRoleLabel}' />"
              data-viewer-name="<c:out value='${chatbotViewerName}' />"
              data-storage-key="library-manager-chatbot-session-v1">

            <section class="chatbot-intro">
                <div class="chatbot-back-row">
                    <button class="chatbot-back-link" type="button" data-chatbot-back>
                        <span class="chatbot-back-icon" aria-hidden="true">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M19 12H5"></path>
                                <path d="m12 19-7-7 7-7"></path>
                            </svg>
                        </span>
                        <span class="chatbot-back-copy">
                            <strong>Quay lại</strong>
                            <small><c:out value="${chatbotBackLabel}" /></small>
                        </span>
                    </button>

                    <span class="chatbot-presence">
                        <span class="chatbot-presence-dot" aria-hidden="true"></span>
                        Groq AI Assistant
                    </span>
                </div>

                <div class="chatbot-intro-content">
                    <div class="chatbot-intro-copy">
                        <div class="chatbot-eyebrow">Workspace trợ lý</div>
                        <h1>Trợ lý AI cho Library Manager</h1>
                        <p id="chatIntroText">
                            Đặt câu hỏi về quy trình thư viện, thao tác trong hệ thống hoặc các tình huống thường gặp
                            để nhận hướng dẫn ngắn gọn, rõ ràng và thực dụng bằng tiếng Việt.
                        </p>
                    </div>

                    <div class="chatbot-intro-meta">
                        <article class="chatbot-meta-card">
                            <span>Người dùng</span>
                            <strong><c:out value="${chatbotViewerName}" /></strong>
                        </article>
                        <article class="chatbot-meta-card">
                            <span>Vai trò</span>
                            <strong><c:out value="${chatbotRoleLabel}" /></strong>
                        </article>
                        <article class="chatbot-meta-card">
                            <span>Model</span>
                            <strong><c:out value="${chatbotModel}" /></strong>
                        </article>
                    </div>
                </div>
            </section>

            <div class="chatbot-workspace">
                <section class="chatbot-panel">
                    <div class="chatbot-panel-header">
                        <div class="chatbot-panel-copy">
                            <span class="chatbot-panel-kicker">Cuộc trò chuyện hiện tại</span>
                            <h2>Hỏi nhanh, theo dõi rõ, tiếp tục liền mạch</h2>
                        </div>

                        <div class="chatbot-panel-meta">
                            <span class="chatbot-state-pill" id="chatSessionState">Phiên mới</span>
                            <span class="chatbot-thread-count" id="chatMessageCount">0 tin nhắn</span>
                        </div>
                    </div>

                    <c:if test="${not chatbotConfigured}">
                        <div class="chatbot-banner warning" role="alert">
                            GROQ_API_KEY chưa được cấu hình. Hãy thiết lập biến môi trường rồi tải lại trang để bắt đầu chat.
                        </div>
                    </c:if>

                    <div class="chatbot-thread-wrap">
                        <div class="chatbot-empty-state" id="chatEmptyState">
                            <div class="chatbot-empty-icon" aria-hidden="true">
                                <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                    <path d="M8 10h8"></path>
                                    <path d="M8 7h5"></path>
                                </svg>
                            </div>
                            <div class="chatbot-empty-copy">
                                <span class="chatbot-empty-kicker">Bắt đầu cuộc trò chuyện</span>
                                <h3 id="chatEmptyTitle">Chưa có tin nhắn nào trong phiên này</h3>
                                <p id="chatEmptyDescription">
                                    Chọn một prompt mẫu để mở đầu nhanh, hoặc nhập câu hỏi riêng ở ô chat phía dưới.
                                </p>
                            </div>
                            <div class="chatbot-empty-grid" id="chatEmptySuggestions"></div>
                        </div>

                        <div class="chatbot-thread" id="chatThread" aria-live="polite" aria-label="Lịch sử hội thoại"></div>
                    </div>

                    <div class="chatbot-footer">
                        <div class="chatbot-status-row">
                            <div class="chatbot-status" id="chatStatus">Sẵn sàng nhận câu hỏi.</div>
                            <div class="chatbot-model-note" id="chatModelNote">Đang dùng: <c:out value="${chatbotModel}" /></div>
                        </div>

                        <form class="chatbot-composer" id="chatForm">
                            <div class="chatbot-composer-field">
                                <label class="sr-only" for="chatInput">Nhập nội dung cần hỏi</label>
                                <textarea id="chatInput" name="message" rows="1"
                                          placeholder="Nhập câu hỏi của bạn, ví dụ: làm sao để duyệt phiếu mượn?"
                                          maxlength="2500"></textarea>

                                <div class="chatbot-composer-meta">
                                    <span class="chatbot-key-hint">Enter để gửi • Shift+Enter để xuống dòng</span>
                                    <span class="chatbot-char-count" id="chatCharCount">0/2500</span>
                                </div>
                            </div>

                            <button type="submit" id="chatSubmit">
                                <span>Gửi câu hỏi</span>
                            </button>
                        </form>
                    </div>
                </section>

                <aside class="chatbot-side-panel">
                    <div class="chatbot-side-card">
                        <div class="chatbot-side-head">
                            <span class="chatbot-side-kicker">Gợi ý theo vai trò</span>
                            <span class="chatbot-side-caption" id="chatPromptCaption">Chọn một câu để bắt đầu nhanh</span>
                        </div>

                        <div class="chatbot-prompt-list" id="chatPromptList"></div>
                    </div>

                    <div class="chatbot-side-card subtle">
                        <div class="chatbot-side-head">
                            <span class="chatbot-side-kicker">Nhịp trò chuyện</span>
                            <span class="chatbot-side-caption" id="chatSessionHint">Chưa có tin nhắn nào.</span>
                        </div>

                        <div class="chatbot-session-note">
                            <strong>Không cần thao tác phụ, chỉ cần hỏi và tiếp tục trò chuyện.</strong>
                            <p>Phiên chat sẽ tự lưu tạm trong trình duyệt để bạn có thể quay lại đúng mạch trao đổi gần nhất.</p>
                        </div>

                        <div class="chatbot-session-grid">
                            <div class="chatbot-session-item">
                                <span>Người dùng</span>
                                <strong><c:out value="${chatbotViewerName}" /></strong>
                            </div>
                            <div class="chatbot-session-item">
                                <span>Vai trò</span>
                                <strong><c:out value="${chatbotRoleLabel}" /></strong>
                            </div>
                            <div class="chatbot-session-item">
                                <span>Model</span>
                                <strong><c:out value="${chatbotModel}" /></strong>
                            </div>
                            <div class="chatbot-session-item">
                                <span>Lưu tạm</span>
                                <strong id="chatStorageState">SessionStorage</strong>
                            </div>
                        </div>
                    </div>

                    <div class="chatbot-side-card subtle">
                        <div class="chatbot-side-head">
                            <span class="chatbot-side-kicker">Lưu ý khi hỏi</span>
                        </div>

                        <ul class="chatbot-side-list" id="chatTipsList"></ul>
                    </div>
                </aside>
            </div>

            <button class="chatbot-fab" type="button" id="chatFocusFab" aria-label="Nhảy tới ô nhập chat">
                <span class="chatbot-fab-icon" aria-hidden="true">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                        <path d="M8 10h8"></path>
                        <path d="M8 7h5"></path>
                    </svg>
                </span>
                <span class="chatbot-fab-copy">
                    <strong>Hỏi ngay</strong>
                    <small>Nhảy tới ô nhập</small>
                </span>
            </button>
        </main>

        <script src="${pageContext.request.contextPath}/assets/js/chatbot.js?v=20260325-workspace-refresh"></script>
    </body>
</html>
