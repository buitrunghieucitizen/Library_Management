<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isAdminNav" value="false" />
<c:if test="${not empty sessionScope.roles}">
    <c:forEach var="roleId" items="${sessionScope.roles}">
        <c:if test="${roleId == 1}">
            <c:set var="isAdminNav" value="true" />
        </c:if>
    </c:forEach>
</c:if>

<div class="navbar admin-fixed">
    <h1>Quản lý thư viện</h1>
    <a class="${activeTab eq 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a>
    <a class="${activeTab eq 'books' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/books?action=list">Sách</a>
    <a class="${activeTab eq 'students' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/students?action=list">Sinh viên</a>
    <a class="${activeTab eq 'borrows' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
    <a class="${activeTab eq 'orders' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/orders?action=list">Đơn hàng</a>
    <a class="${activeTab eq 'bookfiles' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/bookfiles?action=list">Tệp sách</a>

    <c:if test="${isAdminNav}">
        <a class="${activeTab eq 'authors' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/authors?action=list">Tác giả</a>
        <a class="${activeTab eq 'categories' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/categories?action=list">Thể loại</a>
        <a class="${activeTab eq 'publishers' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/publishers?action=list">Nhà xuất bản</a>
        <a class="${activeTab eq 'staffs' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
    </c:if>

    <div class="nav-right">
        <span><c:out value="${sessionScope.staff.staffName}" default="" /></span>
        <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
    </div>
</div>

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
                    if (data.type === 'NEW_BORROW' || data.type === 'NEW_RETURN_REQUEST') {
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
