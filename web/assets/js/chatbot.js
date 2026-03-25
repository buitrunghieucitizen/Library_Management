(function () {
    var app = document.getElementById("chatbotApp");
    if (!app) {
        return;
    }

    var MAX_CONTEXT_MESSAGES = 12;
    var MAX_MESSAGE_LENGTH = 2500;

    var endpoint = app.getAttribute("data-endpoint") || "";
    var configured = app.getAttribute("data-configured") === "true";
    var defaultModel = app.getAttribute("data-model") || "";
    var roleLabel = app.getAttribute("data-role-label") || "";
    var viewerName = app.getAttribute("data-viewer-name") || "Người dùng thư viện";
    var storageKey = app.getAttribute("data-storage-key") || "library-manager-chatbot-session-v1";

    var thread = document.getElementById("chatThread");
    var form = document.getElementById("chatForm");
    var input = document.getElementById("chatInput");
    var submit = document.getElementById("chatSubmit");
    var status = document.getElementById("chatStatus");
    var modelNote = document.getElementById("chatModelNote");
    var introText = document.getElementById("chatIntroText");
    var promptCaption = document.getElementById("chatPromptCaption");
    var promptList = document.getElementById("chatPromptList");
    var tipsList = document.getElementById("chatTipsList");
    var emptyState = document.getElementById("chatEmptyState");
    var emptyTitle = document.getElementById("chatEmptyTitle");
    var emptyDescription = document.getElementById("chatEmptyDescription");
    var emptySuggestions = document.getElementById("chatEmptySuggestions");
    var sessionState = document.getElementById("chatSessionState");
    var messageCount = document.getElementById("chatMessageCount");
    var sessionHint = document.getElementById("chatSessionHint");
    var storageState = document.getElementById("chatStorageState");
    var charCount = document.getElementById("chatCharCount");
    var focusFab = document.getElementById("chatFocusFab");
    var backButton = document.querySelector("[data-chatbot-back]");

    var storageAvailable = canUseSessionStorage();
    var history = [];
    var typingNode = null;
    var state = configured ? "idle" : "unconfigured";
    var sessionOrigin = "new";
    var roleContent = getRoleContent(resolveRoleKey(roleLabel));

    function setStatus(message) {
        if (status) {
            status.textContent = message;
        }
    }

    function trim(value) {
        return value == null ? "" : String(value).trim();
    }

    function normalizeText(value) {
        var normalized = trim(value).toLowerCase();
        if (typeof normalized.normalize === "function") {
            normalized = normalized.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
        }
        return normalized;
    }

    function resolveRoleKey(label) {
        var normalized = normalizeText(label);
        if (normalized.indexOf("sinh vien") !== -1 || normalized.indexOf("student") !== -1) {
            return "student";
        }
        if (normalized.indexOf("quan tri") !== -1
                || normalized.indexOf("admin") !== -1
                || normalized.indexOf("nhan vien") !== -1
                || normalized.indexOf("staff") !== -1) {
            return "operator";
        }
        return "shared";
    }

    function getRoleContent(roleKey) {
        var contentMap = {
            shared: {
                intro: "Đặt câu hỏi về quy trình thư viện, thao tác trong hệ thống hoặc các tình huống thường gặp để nhận hướng dẫn ngắn gọn, rõ ràng và thực dụng bằng tiếng Việt.",
                promptCaption: "Dùng chung cho cả sinh viên lẫn đội ngũ vận hành",
                emptyTitle: "Chưa có tin nhắn nào trong phiên này",
                emptyDescription: "Chọn một prompt mẫu để mở đầu nhanh, hoặc nhập câu hỏi riêng ở ô chat phía dưới.",
                prompts: [
                    {
                        title: "Gia hạn phiếu mượn",
                        description: "Nhắc nhanh các bước kiểm tra điều kiện và hướng xử lý khi muốn gia hạn.",
                        message: "Cách gia hạn sách trong hệ thống này là gì?"
                    },
                    {
                        title: "Xử lý quá hạn",
                        description: "Tóm tắt quy trình chung khi có sách quá hạn hoặc cần nhắc trả.",
                        message: "Nếu người dùng có sách quá hạn thì nên xử lý thế nào?"
                    },
                    {
                        title: "Tóm tắt thao tác",
                        description: "Hỏi chatbot giải thích nhanh một luồng công việc trong Library Manager.",
                        message: "Hãy tóm tắt quy trình duyệt phiếu mượn trong Library Manager."
                    },
                    {
                        title: "Mẹo đặt câu hỏi",
                        description: "Nhờ AI gợi ý cách đặt câu hỏi rõ hơn để nhận câu trả lời thực dụng hơn.",
                        message: "Hãy cho tôi 3 ví dụ câu hỏi tốt để hỏi trợ lý AI của Library Manager."
                    }
                ],
                tips: [
                    "Nêu rõ bạn đang là sinh viên hay nhân viên nếu câu hỏi liên quan tới quy trình riêng theo vai trò.",
                    "Với thao tác nghiệp vụ quan trọng, hãy dùng chatbot như người hướng dẫn, sau đó xác nhận lại trên giao diện thật.",
                    "Nếu cần câu trả lời ngắn, hãy nói rõ: “trả lời ngắn gọn theo từng bước”."
                ]
            },
            student: {
                intro: "Hỏi nhanh về mượn trả, đặt giữ chỗ, theo dõi đơn mua hoặc cách dùng cổng sinh viên để nhận hướng dẫn ngắn gọn và dễ làm theo.",
                promptCaption: "Ưu tiên các câu hỏi giúp sinh viên thao tác nhanh hơn",
                emptyTitle: "Xin chào " + viewerName + ", bắt đầu với một câu hỏi về mượn hoặc mua sách",
                emptyDescription: "Bạn có thể hỏi về gia hạn, đặt giữ chỗ, theo dõi đơn mua hoặc cách dùng cổng sinh viên.",
                prompts: [
                    {
                        title: "Gia hạn sách",
                        description: "Tìm hiểu cách kiểm tra điều kiện trước khi xin gia hạn phiếu mượn.",
                        message: "Cách gia hạn sách trong hệ thống này là gì?"
                    },
                    {
                        title: "Đặt giữ chỗ",
                        description: "Hỏi khi nào nên đặt giữ chỗ và hệ thống sẽ thông báo ra sao.",
                        message: "Nếu sách đã hết thì em nên đặt giữ chỗ như thế nào?"
                    },
                    {
                        title: "Theo dõi đơn mua",
                        description: "Nhờ AI tóm tắt ý nghĩa các trạng thái đơn mua trong hệ thống.",
                        message: "Các trạng thái đơn mua sách trong hệ thống có ý nghĩa gì?"
                    },
                    {
                        title: "Tránh bị quá hạn",
                        description: "Xin checklist ngắn để không bị khóa mượn vì sách quá hạn hoặc tiền phạt.",
                        message: "Hãy cho em checklist ngắn để tránh bị quá hạn hoặc nợ phạt khi mượn sách."
                    }
                ],
                tips: [
                    "Nêu rõ bạn muốn “theo từng bước” nếu cần chatbot hướng dẫn thao tác trên cổng sinh viên.",
                    "Nếu hỏi về một đơn mua hoặc phiếu mượn cụ thể, hãy mô tả trạng thái bạn đang thấy trên màn hình.",
                    "Chatbot không tự đọc dữ liệu cá nhân hay DB nội bộ, nên hãy mô tả ngữ cảnh nếu cần tư vấn chính xác hơn."
                ]
            },
            operator: {
                intro: "Hỏi nhanh về quy trình vận hành thư viện, xử lý mượn trả, duyệt phiếu, theo dõi đơn hàng hoặc các tình huống nghiệp vụ cho admin và staff.",
                promptCaption: "Ưu tiên các câu hỏi vận hành, quy trình và hỗ trợ nghiệp vụ",
                emptyTitle: "Sẵn sàng hỗ trợ nghiệp vụ cho " + viewerName,
                emptyDescription: "Bạn có thể dùng chatbot để tóm tắt quy trình, xử lý tình huống thường gặp hoặc soạn checklist cho ca trực.",
                prompts: [
                    {
                        title: "Duyệt phiếu mượn",
                        description: "Nhắc nhanh các bước mà staff mới cần nhớ khi xử lý phiếu mượn.",
                        message: "Hãy tóm tắt quy trình duyệt phiếu mượn cho nhân viên mới."
                    },
                    {
                        title: "Xử lý sách quá hạn",
                        description: "Gợi ý thứ tự kiểm tra và cách trao đổi với sinh viên khi có vi phạm quá hạn.",
                        message: "Nếu sinh viên có sách quá hạn thì nhân viên nên xử lý theo thứ tự nào?"
                    },
                    {
                        title: "Điều phối đơn mua",
                        description: "Nhờ chatbot diễn giải ngắn gọn luồng xử lý đơn mua sách cho đội vận hành.",
                        message: "Hãy tóm tắt luồng xử lý một đơn mua sách từ lúc tạo tới lúc hoàn tất."
                    },
                    {
                        title: "Checklist ca trực",
                        description: "Tạo checklist nhanh cho một ca trực tại thư viện hoặc tại dashboard admin.",
                        message: "Hãy tạo checklist mở ca trực ngắn gọn cho nhân viên thư viện sử dụng Library Manager."
                    }
                ],
                tips: [
                    "Hãy nêu rõ vai trò người thao tác nếu bạn muốn câu trả lời phân biệt admin với staff.",
                    "Nếu cần câu trả lời dùng để onboarding, hãy yêu cầu “tóm tắt theo từng bước, tối đa 6 ý”.",
                    "Với quyết định nghiệp vụ ảnh hưởng dữ liệu thật, dùng chatbot để tham khảo rồi xác nhận lại trên dashboard."
                ]
            }
        };

        return contentMap[roleKey] || contentMap.shared;
    }

    function canUseSessionStorage() {
        try {
            var probeKey = storageKey + "-probe";
            window.sessionStorage.setItem(probeKey, "1");
            window.sessionStorage.removeItem(probeKey);
            return true;
        } catch (error) {
            return false;
        }
    }

    function createEntry(role, content, timestamp) {
        return {
            role: role,
            content: content,
            timestamp: timestamp || new Date().toISOString()
        };
    }

    function normalizeRole(role) {
        var normalized = normalizeText(role);
        if (normalized === "user" || normalized === "assistant") {
            return normalized;
        }
        return "";
    }

    function sanitizeEntry(rawEntry) {
        if (!rawEntry) {
            return null;
        }

        var role = normalizeRole(rawEntry.role);
        var content = trim(rawEntry.content);
        if (!role || !content) {
            return null;
        }

        if (content.length > MAX_MESSAGE_LENGTH && role === "user") {
            content = content.substring(0, MAX_MESSAGE_LENGTH);
        }

        return createEntry(role, content, rawEntry.timestamp);
    }

    function trimHistory(entries) {
        if (entries.length <= MAX_CONTEXT_MESSAGES) {
            return entries;
        }
        return entries.slice(entries.length - MAX_CONTEXT_MESSAGES);
    }

    function persistHistory() {
        if (!storageAvailable) {
            return;
        }

        try {
            if (!history.length) {
                window.sessionStorage.removeItem(storageKey);
                return;
            }
            window.sessionStorage.setItem(storageKey, JSON.stringify(history));
        } catch (error) {
        }
    }

    function loadHistory() {
        if (!storageAvailable) {
            return [];
        }

        try {
            var raw = window.sessionStorage.getItem(storageKey);
            if (!raw) {
                return [];
            }
            var parsed = JSON.parse(raw);
            if (!Array.isArray(parsed)) {
                return [];
            }

            var restored = [];
            parsed.forEach(function (entry) {
                var sanitized = sanitizeEntry(entry);
                if (sanitized) {
                    restored.push(sanitized);
                }
            });
            return trimHistory(restored);
        } catch (error) {
            return [];
        }
    }

    function pushHistory(role, content) {
        var nextEntry = sanitizeEntry(createEntry(role, content));
        if (!nextEntry) {
            return null;
        }
        history.push(nextEntry);
        history = trimHistory(history);
        persistHistory();
        return nextEntry;
    }

    function updateModelNote(modelName) {
        if (modelNote) {
            modelNote.textContent = "Đang dùng: " + (modelName || defaultModel || "Groq");
        }
    }

    function formatTime(timestamp) {
        var date = timestamp ? new Date(timestamp) : new Date();
        if (Number.isNaN(date.getTime())) {
            return "Đã lưu tạm";
        }
        return date.toLocaleTimeString("vi-VN", {
            hour: "2-digit",
            minute: "2-digit"
        });
    }

    function appendMultilineText(node, text) {
        var parts = String(text).split("\n");
        parts.forEach(function (part, index) {
            node.appendChild(document.createTextNode(part));
            if (index < parts.length - 1) {
                node.appendChild(document.createElement("br"));
            }
        });
    }

    function buildTextBlocks(text) {
        var lines = String(text || "").replace(/\r\n?/g, "\n").split("\n");
        var blocks = [];
        var buffer = [];
        var inFence = false;
        var fenceLines = [];

        function flushBuffer() {
            if (!buffer.length) {
                return;
            }
            blocks.push({
                type: "text",
                lines: buffer.slice()
            });
            buffer = [];
        }

        lines.forEach(function (line) {
            if (trim(line).indexOf("```") === 0) {
                if (inFence) {
                    blocks.push({
                        type: "code",
                        lines: fenceLines.slice()
                    });
                    fenceLines = [];
                    inFence = false;
                } else {
                    flushBuffer();
                    inFence = true;
                }
                return;
            }

            if (inFence) {
                fenceLines.push(line);
                return;
            }

            if (!trim(line)) {
                flushBuffer();
                return;
            }

            buffer.push(line);
        });

        if (inFence && fenceLines.length) {
            blocks.push({
                type: "code",
                lines: fenceLines.slice()
            });
        }

        flushBuffer();
        return blocks;
    }

    function isBulletList(lines) {
        return lines.length && lines.every(function (line) {
            return /^[-*•]\s+/.test(trim(line));
        });
    }

    function isNumberedList(lines) {
        return lines.length && lines.every(function (line) {
            return /^\d+[.)]\s+/.test(trim(line));
        });
    }

    function isQuoteBlock(lines) {
        return lines.length && lines.every(function (line) {
            return /^>\s*/.test(trim(line));
        });
    }

    function isNoteBlock(lines) {
        if (!lines.length) {
            return false;
        }
        var normalized = normalizeText(lines[0]);
        return normalized.indexOf("luu y:") === 0
                || normalized.indexOf("ghi chu:") === 0
                || normalized.indexOf("note:") === 0;
    }

    function isCommandBlock(lines) {
        var commandPrefixes = /^(?:\$ |npm |pnpm |yarn |git |curl |java |mvn |gradle |select |insert |update |delete )/i;
        return lines.length > 1 && lines.every(function (line) {
            return commandPrefixes.test(trim(line)) || /^\s{4,}/.test(line) || /^\t/.test(line);
        });
    }

    function createListNode(lines, ordered) {
        var list = document.createElement(ordered ? "ol" : "ul");
        lines.forEach(function (line) {
            var item = document.createElement("li");
            item.textContent = ordered
                    ? trim(line).replace(/^\d+[.)]\s+/, "")
                    : trim(line).replace(/^[-*•]\s+/, "");
            list.appendChild(item);
        });
        return list;
    }

    function createParagraphNode(lines) {
        var paragraph = document.createElement("p");
        appendMultilineText(paragraph, lines.join("\n"));
        return paragraph;
    }

    function createCodeNode(lines) {
        var pre = document.createElement("pre");
        pre.textContent = lines.join("\n");
        return pre;
    }

    function createNoteNode(lines, className, prefixPattern) {
        var block = document.createElement("div");
        block.className = className;
        appendMultilineText(block, lines.join("\n").replace(prefixPattern, ""));
        return block;
    }

    function createContentFragment(text) {
        var fragment = document.createDocumentFragment();
        var blocks = buildTextBlocks(text);

        if (!blocks.length) {
            fragment.appendChild(createParagraphNode([trim(text)]));
            return fragment;
        }

        blocks.forEach(function (block) {
            if (block.type === "code") {
                fragment.appendChild(createCodeNode(block.lines));
                return;
            }

            if (isBulletList(block.lines)) {
                fragment.appendChild(createListNode(block.lines, false));
                return;
            }

            if (isNumberedList(block.lines)) {
                fragment.appendChild(createListNode(block.lines, true));
                return;
            }

            if (isQuoteBlock(block.lines)) {
                fragment.appendChild(createNoteNode(block.lines, "chatbot-quote-block", /^>\s*/gm));
                return;
            }

            if (isNoteBlock(block.lines)) {
                fragment.appendChild(createNoteNode(block.lines, "chatbot-note-block", /^(?:Lưu ý:|Luu y:|Ghi chú:|Ghi chu:|Note:)\s*/i));
                return;
            }

            if (isCommandBlock(block.lines)) {
                fragment.appendChild(createCodeNode(block.lines));
                return;
            }

            fragment.appendChild(createParagraphNode(block.lines));
        });

        return fragment;
    }

    function createMessageNode(entry, variant) {
        var article = document.createElement("article");
        var role = variant || entry.role;
        article.className = "chatbot-message " + role;
        article.setAttribute("data-content", entry.content || "");

        var avatar = document.createElement("span");
        avatar.className = "chatbot-avatar";
        avatar.textContent = role === "user" ? "YOU" : role === "error" ? "ERR" : "AI";

        var body = document.createElement("div");
        body.className = "chatbot-message-body";

        var head = document.createElement("div");
        head.className = "chatbot-message-head";

        var labels = document.createElement("div");
        labels.className = "chatbot-message-labels";

        var title = document.createElement("strong");
        var meta = document.createElement("span");

        if (role === "user") {
            title.textContent = "Bạn";
            meta.textContent = formatTime(entry.timestamp);
        } else if (role === "assistant") {
            title.textContent = "Trợ lý AI";
            meta.textContent = formatTime(entry.timestamp);
        } else if (role === "typing") {
            title.textContent = "Trợ lý AI";
            meta.textContent = "Đang soạn phản hồi";
        } else {
            title.textContent = "Kết nối chatbot";
            meta.textContent = "Có lỗi tạm thời";
        }

        labels.appendChild(title);
        labels.appendChild(meta);

        head.appendChild(labels);

        var bubble = document.createElement("div");
        bubble.className = "chatbot-bubble";

        if (role === "typing") {
            var typing = document.createElement("div");
            typing.className = "chatbot-typing";
            typing.innerHTML = "<span></span><span></span><span></span>";
            bubble.appendChild(typing);
        } else {
            bubble.appendChild(createContentFragment(entry.content));
        }

        body.appendChild(head);
        body.appendChild(bubble);

        article.appendChild(avatar);
        article.appendChild(body);
        return article;
    }

    function clearTransientNodes() {
        if (!thread) {
            return;
        }

        Array.prototype.slice.call(thread.querySelectorAll("[data-transient='true']")).forEach(function (node) {
            node.remove();
        });
        typingNode = null;
    }

    function renderHistory() {
        if (!thread) {
            return;
        }

        clearTransientNodes();
        thread.innerHTML = "";

        history.forEach(function (entry) {
            thread.appendChild(createMessageNode(entry, entry.role));
        });

        updateLayoutState();
        scrollThread();
    }

    function appendTransientError(message) {
        if (!thread) {
            return;
        }
        updateLayoutState(true);
        var node = createMessageNode(createEntry("assistant", message), "error");
        node.setAttribute("data-transient", "true");
        thread.appendChild(node);
        scrollThread();
    }

    function showTyping() {
        if (!thread || typingNode) {
            return;
        }

        updateLayoutState(true);
        typingNode = createMessageNode(createEntry("assistant", "..."), "typing");
        typingNode.setAttribute("data-transient", "true");
        thread.appendChild(typingNode);
        scrollThread();
    }

    function hideTyping() {
        if (typingNode && typingNode.parentNode) {
            typingNode.parentNode.removeChild(typingNode);
        }
        typingNode = null;
    }

    function scrollThread() {
        if (thread) {
            thread.scrollTop = thread.scrollHeight;
        }
    }

    function setInputValue(value) {
        if (!input) {
            return;
        }
        input.value = value || "";
        resizeInput();
        updateCharCount();
        input.focus();
    }

    function resizeInput() {
        if (!input) {
            return;
        }
        input.style.height = "auto";
        input.style.height = Math.min(input.scrollHeight, 180) + "px";
    }

    function updateCharCount() {
        if (!charCount || !input) {
            return;
        }
        var length = input.value.length;
        charCount.textContent = length + "/" + MAX_MESSAGE_LENGTH;
        charCount.classList.toggle("is-near-limit", length >= MAX_MESSAGE_LENGTH * 0.8 && length < MAX_MESSAGE_LENGTH);
        charCount.classList.toggle("is-limit", length >= MAX_MESSAGE_LENGTH);
    }

    function updateLayoutState(forceThreadVisible) {
        var hasContent = history.length > 0 || forceThreadVisible || !!typingNode;

        if (emptyState) {
            emptyState.classList.toggle("is-hidden", hasContent);
        }

        if (thread) {
            thread.classList.toggle("is-hidden", !hasContent);
        }

        if (messageCount) {
            messageCount.textContent = history.length + " tin nhắn";
        }

        if (sessionHint) {
            if (!history.length) {
                sessionHint.textContent = configured
                        ? "Chưa có tin nhắn nào. Bạn có thể mở đầu bằng một prompt mẫu."
                        : "Giao diện đã sẵn sàng nhưng chatbot đang bị khóa do thiếu API key.";
            } else if (state === "sending") {
                sessionHint.textContent = "Đang chờ Groq trả lời. Cuộc trò chuyện vẫn được lưu tạm trong phiên hiện tại.";
            } else if (sessionOrigin === "restored") {
                sessionHint.textContent = "Phiên chat này đã được khôi phục từ trình duyệt và sẽ tiếp tục lưu tạm.";
            } else {
                sessionHint.textContent = "Cuộc trò chuyện đang được lưu tạm để bạn tiếp tục nếu tải lại trang.";
            }
        }

        if (storageState) {
            if (!storageAvailable) {
                storageState.textContent = "Không khả dụng";
            } else if (!history.length) {
                storageState.textContent = "Chưa có dữ liệu";
            } else {
                storageState.textContent = "Đang lưu";
            }
        }
    }

    function updateActionAvailability() {
        var isSending = state === "sending";
        var isUnconfigured = state === "unconfigured";

        if (input) {
            input.disabled = isSending || isUnconfigured;
        }
        if (submit) {
            submit.disabled = isSending || isUnconfigured;
        }

        Array.prototype.slice.call(document.querySelectorAll(".chatbot-prompt-card, .chatbot-empty-chip")).forEach(function (button) {
            button.disabled = isSending || isUnconfigured;
        });
    }

    function updateStatePill() {
        if (!sessionState) {
            return;
        }

        sessionState.classList.remove("is-sending", "is-error", "is-unconfigured");

        if (state === "sending") {
            sessionState.textContent = "Đang gửi";
            sessionState.classList.add("is-sending");
            return;
        }

        if (state === "unconfigured") {
            sessionState.textContent = "Chưa cấu hình";
            sessionState.classList.add("is-unconfigured");
            return;
        }

        if (state === "error") {
            sessionState.textContent = "Cần thử lại";
            sessionState.classList.add("is-error");
            return;
        }

        if (!history.length) {
            sessionState.textContent = "Phiên mới";
            return;
        }

        sessionState.textContent = sessionOrigin === "restored" ? "Đã khôi phục" : "Đang hoạt động";
    }

    function setState(nextState, statusMessage) {
        state = nextState;

        if (state === "sending") {
            showTyping();
        } else {
            hideTyping();
        }

        updateStatePill();
        updateLayoutState(state === "sending");
        updateActionAvailability();

        if (statusMessage) {
            setStatus(statusMessage);
            return;
        }

        if (state === "unconfigured") {
            setStatus("GROQ_API_KEY chưa được cấu hình.");
        } else if (state === "sending") {
            setStatus("Đang chờ Groq phản hồi...");
        } else if (!history.length) {
            setStatus(configured ? "Sẵn sàng nhận câu hỏi." : "GROQ_API_KEY chưa được cấu hình.");
        } else {
            setStatus("Bạn có thể tiếp tục cuộc trò chuyện.");
        }
    }

    function renderPrompts() {
        if (introText) {
            introText.textContent = roleContent.intro;
        }
        if (promptCaption) {
            promptCaption.textContent = roleContent.promptCaption;
        }
        if (emptyTitle) {
            emptyTitle.textContent = roleContent.emptyTitle;
        }
        if (emptyDescription) {
            emptyDescription.textContent = roleContent.emptyDescription;
        }

        if (tipsList) {
            tipsList.innerHTML = "";
            roleContent.tips.forEach(function (tip) {
                var item = document.createElement("li");
                item.textContent = tip;
                tipsList.appendChild(item);
            });
        }

        if (promptList) {
            promptList.innerHTML = "";
            roleContent.prompts.forEach(function (prompt) {
                var button = document.createElement("button");
                button.type = "button";
                button.className = "chatbot-prompt-card";
                button.setAttribute("data-prompt", prompt.message);

                var title = document.createElement("strong");
                title.textContent = prompt.title;

                var description = document.createElement("span");
                description.textContent = prompt.description;

                button.appendChild(title);
                button.appendChild(description);
                button.addEventListener("click", function () {
                    setInputValue(prompt.message);
                    setStatus("Đã nạp prompt vào ô nhập. Nhấn Enter để gửi.");
                });

                promptList.appendChild(button);
            });
        }

        if (emptySuggestions) {
            emptySuggestions.innerHTML = "";
            roleContent.prompts.slice(0, 3).forEach(function (prompt) {
                var button = document.createElement("button");
                button.type = "button";
                button.className = "chatbot-empty-chip";
                button.textContent = prompt.title;
                button.addEventListener("click", function () {
                    setInputValue(prompt.message);
                    setStatus("Đã nạp prompt gợi ý vào ô nhập.");
                });
                emptySuggestions.appendChild(button);
            });
        }
    }

    function setupBackButton() {
        if (!backButton) {
            return;
        }

        backButton.addEventListener("click", function () {
            var fallbackUrl = app.getAttribute("data-back-url") || "/";

            if (window.history.length > 1) {
                window.history.back();
                return;
            }

            window.location.href = fallbackUrl;
        });
    }

    function bindUtilityActions() {
        if (focusFab) {
            focusFab.addEventListener("click", function () {
                if (!input) {
                    return;
                }

                if (typeof input.scrollIntoView === "function") {
                    input.scrollIntoView({
                        behavior: "smooth",
                        block: "center"
                    });
                }

                window.setTimeout(function () {
                    input.focus();
                }, 120);
                setStatus("Sẵn sàng để bạn nhập câu hỏi mới.");
            });
        }
    }

    function hydrateFromStorage() {
        history = loadHistory();
        if (history.length) {
            sessionOrigin = "restored";
            renderHistory();
            setState(configured ? "idle" : "unconfigured", configured
                    ? "Đã khôi phục phiên chat gần nhất."
                    : "Đã khôi phục phiên chat, nhưng chatbot hiện chưa được cấu hình.");
        } else {
            renderHistory();
        }
    }

    function buildPayloadMessages() {
        return history.map(function (entry) {
            return {
                role: entry.role,
                content: entry.content
            };
        });
    }

    updateModelNote(defaultModel);
    renderPrompts();
    setupBackButton();
    bindUtilityActions();
    hydrateFromStorage();
    resizeInput();
    updateCharCount();
    updateActionAvailability();
    updateLayoutState();

    if (!history.length) {
        setState(configured ? "idle" : "unconfigured");
    }

    input.addEventListener("input", function () {
        resizeInput();
        updateCharCount();
        if (state === "error" && configured) {
            setState("idle", history.length ? "Bạn có thể thử gửi lại câu hỏi." : "Sẵn sàng nhận câu hỏi.");
        }
    });

    input.addEventListener("keydown", function (event) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            if (typeof form.requestSubmit === "function") {
                form.requestSubmit();
            } else {
                submit.click();
            }
        }
    });

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        var message = trim(input.value);
        if (!message) {
            setStatus("Nhập nội dung trước khi gửi câu hỏi.");
            input.focus();
            return;
        }

        clearTransientNodes();

        var userEntry = pushHistory("user", message);
        if (!userEntry) {
            setStatus("Không thể xử lý nội dung vừa nhập.");
            return;
        }

        sessionOrigin = "live";

        renderHistory();
        input.value = "";
        resizeInput();
        updateCharCount();
        setState("sending");

        try {
            var response = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: JSON.stringify({
                    messages: buildPayloadMessages()
                })
            });

            var data = await response.json().catch(function () {
                return {};
            });

            if (!response.ok) {
                throw new Error(data.error || "Không thể nhận phản hồi từ chatbot.");
            }

            var reply = trim(data.reply);
            if (!reply) {
                throw new Error("Chatbot không trả về nội dung.");
            }

            pushHistory("assistant", reply);
            renderHistory();
            updateModelNote(data.model || defaultModel);
            setState("idle", "Phản hồi xong. Bạn có thể hỏi tiếp.");
        } catch (error) {
            appendTransientError(error.message || "Đã có lỗi xảy ra.");
            setState(configured ? "error" : "unconfigured", "Có lỗi khi gửi yêu cầu.");
        }
    });
}());
