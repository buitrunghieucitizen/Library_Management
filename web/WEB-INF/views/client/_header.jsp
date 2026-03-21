<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:url var="homeUrl" value="/home" />
<c:url var="profileUrl" value="/profile" />
<c:url var="logoutUrl" value="/logout" />
<c:set var="viewerName" value="${empty requestScope.studentDisplayName ? (empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName) : requestScope.studentDisplayName}" />
<c:set var="viewerInitial" value="${empty requestScope.studentDisplayInitial ? (empty viewerName ? 'S' : fn:toUpperCase(fn:substring(viewerName, 0, 1))) : requestScope.studentDisplayInitial}" />
<c:set var="viewerAvatarUrl" value="${empty currentStudent ? '' : currentStudent.avatarUrl}" />

<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">

<nav class="top-nav student-top-nav">
    <div class="top-nav-shell">
        <div class="top-nav-left">
            <button class="student-nav-toggle" type="button" id="studentNavToggle" aria-label="Mở menu sinh viên">
                <span></span>
                <span></span>
                <span></span>
            </button>

            <a href="${homeUrl}" class="brand">
                <span class="brand-mark">LM</span>
                <span class="brand-copy">
                    <span class="brand-overline">Student Portal</span>
                    <strong>Cổng thư viện</strong>
                </span>
            </a>
        </div>

        <div class="nav-right">
            <c:if test="${not empty sessionScope.staff}">
                <a href="${profileUrl}" class="user-chip user-chip-link">
                    <span class="user-avatar">
                        <c:choose>
                            <c:when test="${not empty viewerAvatarUrl}">
                                <img src="${viewerAvatarUrl}" alt="${viewerName}" class="user-avatar-image">
                            </c:when>
                            <c:otherwise>
                                <c:out value="${viewerInitial}" />
                            </c:otherwise>
                        </c:choose>
                    </span>
                    <span class="user-name"><c:out value="${viewerName}" /></span>
                </a>
                <a href="${logoutUrl}" class="nav-button">Đăng xuất</a>
            </c:if>
        </div>
    </div>
</nav>
<div class="student-nav-overlay" id="studentNavOverlay"></div>
<%-- WebSocket: student nhận thông báo từ admin --%>
<c:if test="${not empty studentId}">
    <script>
        (function () {
            var protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
            var wsUrl = protocol + '//' + location.host + '${pageContext.request.contextPath}/ws/notify/student/${studentId}';
                    var ws = null;
                    var reconnectDelay = 3000;

                    function connect() {
                        try {
                            ws = new WebSocket(wsUrl);
                        } catch (e) {
                            return;
                        }

                        ws.onopen = function () {
                            console.log('[WS] Student #${studentId} kết nối thành công.');
                            reconnectDelay = 3000;
                        };

                        ws.onmessage = function (event) {
                            try {
                                var data = JSON.parse(event.data);
                                console.log('[WS] Nhận thông báo:', data);
                                showStudentToast(data);

                                // Auto reload sau 3s khi có thay đổi quan trọng
                                if (data.type === 'BORROW_APPROVED' || data.type === 'BORROW_REJECTED'
                                        || data.type === 'RETURN_CONFIRMED' || data.type === 'BOOK_AVAILABLE') {
                                    setTimeout(function () {
                                        location.reload();
                                    }, 3000);
                                }
                            } catch (e) {
                            }
                        };

                        ws.onclose = function () {
                            setTimeout(connect, reconnectDelay);
                            reconnectDelay = Math.min(reconnectDelay * 1.5, 30000);
                        };

                        ws.onerror = function () {
                            ws.close();
                        };
                    }

                    connect();

                    function showStudentToast(data) {
                        var container = document.getElementById('studentToastContainer');
                        if (!container) {
                            container = document.createElement('div');
                            container.id = 'studentToastContainer';
                            container.className = 'position-fixed top-0 end-0 p-3';
                            container.style.zIndex = '9999';
                            document.body.appendChild(container);
                        }

                        var bgClass = 'text-bg-info';
                        var icon = '';
                        if (data.type === 'BORROW_APPROVED') {
                            bgClass = 'text-bg-success';
                            icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" class="me-2" style="vertical-align:-2px;flex-shrink:0;"><polyline points="20 6 9 17 4 12"/></svg>';
                        } else if (data.type === 'BORROW_REJECTED') {
                            bgClass = 'text-bg-danger';
                            icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-2" style="vertical-align:-2px;flex-shrink:0;"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>';
                        } else if (data.type === 'BOOK_AVAILABLE') {
                            bgClass = 'text-bg-success';
                            icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-2" style="vertical-align:-2px;flex-shrink:0;"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>';
                        } else if (data.type === 'RETURN_CONFIRMED') {
                            bgClass = 'text-bg-info';
                            icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="me-2" style="vertical-align:-2px;flex-shrink:0;"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg>';
                        }

                        var tid = 'stoast-' + Date.now();
                        container.insertAdjacentHTML('beforeend',
                                '<div id="' + tid + '" class="toast align-items-center ' + bgClass + ' border-0 show mb-2" role="alert" style="min-width:340px;border-radius:12px;">'
                                + '<div class="d-flex align-items-center p-3">'
                                + icon
                                + '<div class="flex-grow-1" style="font-size:14px;">' + (data.message || 'Có thông báo mới!') + '</div>'
                                + '<button type="button" class="btn-close btn-close-white ms-2" onclick="this.closest(\'.toast\').remove()"></button>'
                                + '</div>'
                                + '<div class="px-3 pb-2" style="font-size:12px;opacity:.8;">Trang sẽ tự cập nhật sau 3 giây...</div>'
                                + '</div>');

                        setTimeout(function () {
                            var t = document.getElementById(tid);
                            if (t)
                                t.remove();
                        }, 7000);
                    }
                })();
    </script>
</c:if>