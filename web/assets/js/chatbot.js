(function () {
    var app = document.getElementById('chatbotApp');
    if (!app) {
        return;
    }

    var endpoint = app.getAttribute('data-endpoint') || '';
    var configured = app.getAttribute('data-configured') === 'true';
    var defaultModel = app.getAttribute('data-model') || '';
    var thread = document.getElementById('chatThread');
    var form = document.getElementById('chatForm');
    var input = document.getElementById('chatInput');
    var submit = document.getElementById('chatSubmit');
    var status = document.getElementById('chatStatus');
    var modelNote = document.getElementById('chatModelNote');
    var promptButtons = document.querySelectorAll('[data-chatbot-prompt]');
    var history = [];
    var typingNode = null;

    function setStatus(message) {
        if (status) {
            status.textContent = message;
        }
    }

    function scrollThread() {
        if (thread) {
            thread.scrollTop = thread.scrollHeight;
        }
    }

    function createMessageNode(role, content) {
        var wrapper = document.createElement('div');
        wrapper.className = 'chatbot-message ' + role;

        var avatar = document.createElement('span');
        avatar.className = 'chatbot-avatar';
        avatar.textContent = role === 'user' ? 'YOU' : 'AI';

        var bubble = document.createElement('div');
        bubble.className = 'chatbot-bubble';
        bubble.textContent = content;

        wrapper.appendChild(avatar);
        wrapper.appendChild(bubble);
        return wrapper;
    }

    function appendMessage(role, content) {
        var node = createMessageNode(role, content);
        thread.appendChild(node);
        scrollThread();
        return node;
    }

    function appendError(content) {
        var node = createMessageNode('error', content);
        thread.appendChild(node);
        scrollThread();
        return node;
    }

    function showTyping() {
        if (typingNode) {
            return;
        }

        typingNode = document.createElement('div');
        typingNode.className = 'chatbot-message assistant';

        var avatar = document.createElement('span');
        avatar.className = 'chatbot-avatar';
        avatar.textContent = 'AI';

        var bubble = document.createElement('div');
        bubble.className = 'chatbot-bubble';

        var typing = document.createElement('div');
        typing.className = 'chatbot-typing';
        typing.innerHTML = '<span></span><span></span><span></span>';

        bubble.appendChild(typing);
        typingNode.appendChild(avatar);
        typingNode.appendChild(bubble);
        thread.appendChild(typingNode);
        scrollThread();
    }

    function hideTyping() {
        if (typingNode && typingNode.parentNode) {
            typingNode.parentNode.removeChild(typingNode);
        }
        typingNode = null;
    }

    function setBusy(isBusy) {
        input.disabled = isBusy || !configured;
        submit.disabled = isBusy || !configured;
        if (isBusy) {
            setStatus('Đang chờ Groq phản hồi...');
            showTyping();
        } else {
            hideTyping();
            setStatus(configured ? 'Sẵn sàng nhận câu hỏi.' : 'GROQ_API_KEY chưa được cấu hình.');
        }
    }

    function resizeInput() {
        input.style.height = 'auto';
        input.style.height = Math.min(input.scrollHeight, 180) + 'px';
    }

    function pushHistory(role, content) {
        history.push({role: role, content: content});
        if (history.length > 12) {
            history = history.slice(history.length - 12);
        }
    }

    function setInputValue(value) {
        input.value = value;
        resizeInput();
        input.focus();
    }

    promptButtons.forEach(function (button) {
        button.addEventListener('click', function () {
            setInputValue(button.getAttribute('data-chatbot-prompt') || '');
        });
    });

    input.addEventListener('input', resizeInput);
    input.addEventListener('keydown', function (event) {
        if (event.key === 'Enter' && !event.shiftKey) {
            event.preventDefault();
            form.requestSubmit();
        }
    });

    appendMessage('assistant',
            'Xin chào. Tôi có thể hỗ trợ bạn về cách dùng Library Manager, quy trình thư viện và các câu hỏi ngắn bằng tiếng Việt.');

    if (!configured) {
        setBusy(false);
        return;
    }

    setBusy(false);

    form.addEventListener('submit', async function (event) {
        event.preventDefault();

        var message = input.value.trim();
        if (!message) {
            input.focus();
            return;
        }

        pushHistory('user', message);
        appendMessage('user', message);
        input.value = '';
        resizeInput();
        setBusy(true);

        try {
            var response = await fetch(endpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify({
                    messages: history
                })
            });

            var data = await response.json().catch(function () {
                return {};
            });

            if (!response.ok) {
                throw new Error(data.error || 'Không thể nhận phản hồi từ chatbot.');
            }

            var reply = (data.reply || '').trim();
            if (!reply) {
                throw new Error('Chatbot không trả về nội dung.');
            }

            pushHistory('assistant', reply);
            appendMessage('assistant', reply);

            if (data.model && modelNote) {
                modelNote.textContent = 'Đang dùng: ' + data.model;
            } else if (defaultModel && modelNote) {
                modelNote.textContent = 'Đang dùng: ' + defaultModel;
            }
            setStatus('Phản hồi xong. Bạn có thể hỏi tiếp.');
        } catch (error) {
            appendError(error.message || 'Đã có lỗi xảy ra.');
            setStatus('Có lỗi khi gửi yêu cầu.');
        } finally {
            setBusy(false);
        }
    });
})();
