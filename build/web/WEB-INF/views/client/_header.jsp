<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url var="homeUrl" value="/home" />
<c:url var="logoutUrl" value="/logout" />

<nav class="navbar navbar-expand navbar-dark sticky-top" style="background: linear-gradient(135deg, #1a2744 0%, #2a5298 100%);">
    <div class="container-fluid px-3">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${homeUrl}">
            <span class="brand-icon d-flex align-items-center justify-content-center rounded-2 fw-bold"
                  style="width:32px;height:32px;background:rgba(255,255,255,.15);font-size:13px;color:#fff;">LM</span>
            <span class="fw-semibold" style="font-size:15px;">Cổng thư viện</span>
        </a>

        <div class="d-flex align-items-center gap-3 ms-auto">
            <%-- === BORROW CART ICON === --%>
            <c:if test="${not empty sessionScope.staff}">
                <div class="dropdown">
                    <button class="btn btn-link text-white-50 p-1 position-relative" type="button"
                            data-bs-toggle="dropdown" data-bs-auto-close="outside"
                            aria-expanded="false" title="Giỏ mượn sách">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                        <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                        </svg>
                        <c:if test="${borrowCartSize gt 0}">
                            <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" style="font-size:10px;padding:3px 5px;">
                                ${borrowCartSize}
                            </span>
                        </c:if>
                    </button>

                    <div class="dropdown-menu dropdown-menu-end shadow-lg border-0 p-0" style="width:340px;border-radius:12px;overflow:hidden;">
                        <%-- Cart header --%>
                        <div class="d-flex justify-content-between align-items-center px-3 py-2 bg-light border-bottom">
                            <span class="fw-semibold" style="font-size:14px;">Giỏ mượn sách</span>
                            <span class="badge text-bg-primary" style="font-size:11px;">${borrowCartSize} / ${maxCartSize != null ? maxCartSize : 3}</span>
                        </div>

                        <c:choose>
                            <%-- Empty cart --%>
                            <c:when test="${empty borrowCart}">
                                <div class="text-center text-muted py-4 px-3" style="font-size:14px;">
                                    Giỏ mượn đang trống.<br>
                                    <small class="text-body-tertiary">Thêm sách từ trang chủ để bắt đầu.</small>
                                </div>
                            </c:when>

                            <%-- Cart items --%>
                            <c:otherwise>
                                <div style="max-height:260px;overflow-y:auto;">
                                    <c:forEach var="cartBook" items="${borrowCart}">
                                        <div class="d-flex align-items-center gap-2 px-3 py-2 border-bottom">
                                            <div class="flex-shrink-0 d-flex align-items-center justify-content-center rounded-1 text-white fw-semibold"
                                                 style="width:34px;height:42px;background:linear-gradient(135deg,#1a2744,#2a5298);font-size:11px;">
                                                ${fn:toUpperCase(fn:substring(cartBook.bookName, 0, 1))}
                                            </div>
                                            <div class="flex-grow-1 min-w-0">
                                                <div class="text-truncate fw-medium" style="font-size:13px;">${cartBook.bookName}</div>
                                                <div class="text-muted" style="font-size:11px;">${cartBook.available} có sẵn</div>
                                            </div>
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                                <input type="hidden" name="action" value="removeFromCart">
                                                <input type="hidden" name="bookID" value="${cartBook.bookID}">
                                                <button type="submit" class="btn btn-sm btn-link text-muted p-0" title="Xóa">
                                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                    <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
                                                    </svg>
                                                </button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </div>

                                <%-- Cart footer: submit or warnings --%>
                                <div class="px-3 py-2 bg-light border-top">
                                    <c:choose>
                                        <c:when test="${eligibility != null && eligibility.eligible}">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                                <input type="hidden" name="action" value="submitBorrow">
                                                <button type="submit" class="btn btn-primary w-100 fw-semibold" style="font-size:13px;">
                                                    Gửi yêu cầu mượn (${borrowCartSize} quyển)
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-center">
                                                <c:if test="${eligibility != null && eligibility.hasOverdue}">
                                                    <small class="text-danger d-block">Có sách quá hạn chưa trả.</small>
                                                </c:if>
                                                <c:if test="${eligibility != null && eligibility.hasUnpaidFine}">
                                                    <small class="text-danger d-block">Đang nợ tiền phạt.</small>
                                                </c:if>
                                                <c:if test="${eligibility != null && eligibility.remainingSlots le 0}">
                                                    <small class="text-danger d-block">Đã đạt giới hạn mượn.</small>
                                                </c:if>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>

            <%-- User chip --%>
            <c:if test="${not empty sessionScope.staff}">
                <div class="d-flex align-items-center gap-2">
                    <div class="d-flex align-items-center justify-content-center rounded-circle text-white fw-semibold"
                         style="width:30px;height:30px;background:rgba(255,255,255,.2);font-size:12px;">
                        <c:choose>
                            <c:when test="${not empty sessionScope.staff.staffName}">${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}</c:when>
                            <c:otherwise>U</c:otherwise>
                        </c:choose>
                    </div>
                    <span class="text-white-50 d-none d-md-inline" style="font-size:13px;">${sessionScope.staff.staffName}</span>
                </div>
                <a href="${logoutUrl}" class="btn btn-sm btn-outline-light" style="font-size:12px;">Đăng xuất</a>
            </c:if>
        </div>
    </div>
</nav>
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
                                        || data.type === 'RETURN_CONFIRMED' || data.type === 'BOOK_AVAILABLE'
                                        || data.type === 'BOOK_CHANGED') {
                                    setTimeout(function () {
                                        location.reload();
                                    }, 1000);
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
                                + '<div class="px-3 pb-2" style="font-size:12px;opacity:.8;">Trang sẽ tự cập nhật...</div>'
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