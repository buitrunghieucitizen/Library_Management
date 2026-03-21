<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="isAdminNav" value="false" />
<c:if test="${not empty sessionScope.roles}">
    <c:forEach var="roleId" items="${sessionScope.roles}">
        <c:if test="${roleId == 1}"><c:set var="isAdminNav" value="true" /></c:if>
    </c:forEach>
</c:if>

<nav class="navbar navbar-expand-lg navbar-dark sticky-top" style="background:linear-gradient(135deg,#1a2744 0%,#2a5298 100%);">
    <div class="container-fluid px-3">
        <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/index.jsp">
            <span class="d-flex align-items-center justify-content-center rounded-2 fw-bold"
                  style="width:32px;height:32px;background:rgba(255,255,255,.15);font-size:13px;color:#fff;">LM</span>
            <span class="fw-semibold" style="font-size:15px;">Quản lý thư viện</span>
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="adminNav">
            <ul class="navbar-nav me-auto gap-1">
                <li class="nav-item">
                    <a class="nav-link ${activeTab eq 'home' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab eq 'books' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/books?action=list">Sách</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab eq 'students' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/students?action=list">Sinh viên</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link position-relative ${activeTab eq 'borrows' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/borrows?action=list">
                        Mượn trả
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
                              style="font-size:10px;display:none;" id="pendingBadge"></span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab eq 'orders' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/orders?action=list">Đơn hàng</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${activeTab eq 'bookfiles' ? 'active' : ''}"
                       href="${pageContext.request.contextPath}/admin/bookfiles?action=list">Tệp sách</a>
                </li>
                <c:if test="${isAdminNav}">
                    <li class="nav-item">
                        <a class="nav-link ${activeTab eq 'authors' ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/admin/authors?action=list">Tác giả</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeTab eq 'categories' ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/admin/categories?action=list">Thể loại</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeTab eq 'publishers' ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/admin/publishers?action=list">Nhà xuất bản</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeTab eq 'staffs' ? 'active' : ''}"
                           href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
                    </li>
                </c:if>
            </ul>

            <div class="d-flex align-items-center gap-2">
                <c:if test="${not empty sessionScope.staff}">
                    <div class="d-flex align-items-center gap-2">
                        <div class="d-flex align-items-center justify-content-center rounded-circle text-white fw-semibold"
                             style="width:30px;height:30px;background:rgba(255,255,255,.2);font-size:12px;">
                            ${fn:toUpperCase(fn:substring(sessionScope.staff.staffName, 0, 1))}
                        </div>
                        <span class="text-white-50 d-none d-lg-inline" style="font-size:13px;">${sessionScope.staff.staffName}</span>
                    </div>
                    <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-outline-light" style="font-size:12px;">Đăng xuất</a>
                </c:if>
            </div>
        </div>
    </div>
</nav>

<%-- WebSocket + fallback polling --%>
<script>
    (function () {
        var badge = document.getElementById('pendingBadge');
        if (!badge)
            return;

        // Load pending count ban đầu
        fetch('${pageContext.request.contextPath}/api/pending-count')
                .then(function (r) {
                    return r.ok ? r.json() : null;
                })
                .then(function (data) {
                    if (data && data.pendingCount > 0) {
                        badge.textContent = data.pendingCount;
                        badge.style.display = '';
                    }
                }).catch(function () {});

        // WebSocket
        var protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
        var wsUrl = protocol + '//' + location.host + '${pageContext.request.contextPath}/ws/notify/admin';
        var ws = null;
        var reconnectDelay = 2000;
        var wsConnected = false;

        function connectWs() {
            try {
                ws = new WebSocket(wsUrl);
            } catch (e) {
                startPolling();
                return;
            }

            ws.onopen = function () {
                wsConnected = true;
                reconnectDelay = 2000;
            };

            ws.onmessage = function (event) {
                try {
                    var data = JSON.parse(event.data);
                    if (data.type === 'NEW_BORROW') {
                        var current = parseInt(badge.textContent) || 0;
                        badge.textContent = current + 1;
                        badge.style.display = '';
                        showToast(data.message || 'Có phiếu mượn mới!');
                    }
                } catch (e) {
                }
            };

            ws.onclose = function () {
                wsConnected = false;
                setTimeout(connectWs, reconnectDelay);
                reconnectDelay = Math.min(reconnectDelay * 1.5, 30000);
            };

            ws.onerror = function () {
                ws.close();
            };
        }

        // Fallback polling nếu WS không kết nối được
        function startPolling() {
            setInterval(function () {
                if (wsConnected)
                    return;
                fetch('${pageContext.request.contextPath}/api/pending-count')
                        .then(function (r) {
                            return r.ok ? r.json() : null;
                        })
                        .then(function (data) {
                            if (data && data.pendingCount > 0) {
                                badge.textContent = data.pendingCount;
                                badge.style.display = '';
                            } else if (badge.style.display !== 'none') {
                                badge.style.display = 'none';
                            }
                        }).catch(function () {});
            }, 15000);
        }

        connectWs();
        // Nếu sau 5s vẫn chưa kết nối WS → bật polling backup
        setTimeout(function () {
            if (!wsConnected)
                startPolling();
        }, 5000);

        // Toast notification
        function showToast(message) {
            var container = document.getElementById('wsToastContainer');
            if (!container) {
                container = document.createElement('div');
                container.id = 'wsToastContainer';
                container.className = 'position-fixed top-0 end-0 p-3';
                container.style.zIndex = '9999';
                document.body.appendChild(container);
            }
            var tid = 'toast-' + Date.now();
            container.insertAdjacentHTML('beforeend',
                    '<div id="' + tid + '" class="toast align-items-center text-bg-primary border-0 show mb-2" role="alert" style="min-width:300px;">'
                    + '<div class="d-flex"><div class="toast-body">' + message + '</div>'
                    + '<button type="button" class="btn-close btn-close-white me-2 m-auto" onclick="this.closest(\'.toast\').remove()"></button>'
                    + '</div></div>');
            setTimeout(function () {
                var t = document.getElementById(tid);
                if (t)
                    t.remove();
            }, 6000);
        }
    })();
</script>