<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="pageTitle" value="Quản lý mượn trả" />
<c:set var="activeTab" value="borrows" />
<%@ include file="../admin/layout/_admin_header.jsp" %>

<c:if test="${not empty param.msg}"><div class="msg">${param.msg}</div></c:if>
<c:if test="${not empty param.error}"><div class="error">${param.error}</div></c:if>

    <div style="display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;">
        <div>
            <div class="note" style="margin:0;">${totalItems} bản ghi tổng cộng</div>
    </div>
    <a class="btn btn-primary btn-inline-sm" href="${pageContext.request.contextPath}/admin/borrows?action=create">+ Tạo phiếu mượn</a>
</div>

<%-- === TABS === --%>
<style>
    .borrow-tabs{
        display:flex;
        gap:4px;
        border-bottom:2px solid #e2e8f0;
        margin-bottom:16px;
    }
    .borrow-tab{
        padding:8px 16px;
        font-size:13px;
        font-weight:600;
        cursor:pointer;
        border:none;
        background:none;
        color:#64748b;
        border-bottom:2px solid transparent;
        margin-bottom:-2px;
        transition:all .15s;
    }
    .borrow-tab:hover{
        color:#1e293b;
    }
    .borrow-tab.active{
        color:#1a2744;
        border-bottom-color:#1a2744;
    }
    .borrow-tab .tab-badge{
        display:inline-block;
        padding:1px 7px;
        border-radius:10px;
        font-size:10px;
        font-weight:700;
        margin-left:6px;
        vertical-align:middle;
    }
    .tab-badge.danger{
        background:#ef4444;
        color:#fff;
    }
    .tab-badge.warning{
        background:#f59e0b;
        color:#fff;
    }
    .tab-pane{
        display:none;
    }
    .tab-pane.active{
        display:block;
    }
    .pending-card{
        border:1px solid #e2e8f0;
        border-radius:10px;
        padding:16px;
        background:#fff;
        transition:box-shadow .15s;
    }
    .pending-card:hover{
        box-shadow:0 2px 8px rgba(0,0,0,.06);
    }
    .pending-grid{
        display:grid;
        grid-template-columns:repeat(auto-fill,minmax(320px,1fr));
        gap:12px;
    }
    .status{
        display:inline-block;
        padding:2px 10px;
        border-radius:6px;
        font-size:11px;
        font-weight:600;
    }
    .status.pending{
        background:#fef3c7;
        color:#92400e;
    }
    .status.borrowing{
        background:#dbeafe;
        color:#1e40af;
    }
    .status.returned{
        background:#d1fae5;
        color:#065f46;
    }
    .status.overdue{
        background:#fee2e2;
        color:#991b1b;
    }
    .status.rejected{
        background:#e2e8f0;
        color:#475569;
    }
    .status.waiting{
        background:#fef3c7;
        color:#92400e;
    }
    .status.notified{
        background:#d1fae5;
        color:#065f46;
    }
    .action-btn{
        padding:4px 12px;
        border-radius:6px;
        font-size:12px;
        font-weight:600;
        border:1px solid;
        cursor:pointer;
        transition:all .15s;
    }
    .action-btn.approve{
        background:#059669;
        color:#fff;
        border-color:#059669;
    }
    .action-btn.approve:hover{
        background:#047857;
    }
    .action-btn.reject{
        background:#fff;
        color:#dc2626;
        border-color:#dc2626;
    }
    .action-btn.reject:hover{
        background:#fef2f2;
    }
    .action-btn.return-btn{
        background:#2563eb;
        color:#fff;
        border-color:#2563eb;
    }
    .action-btn.return-btn:hover{
        background:#1d4ed8;
    }
    .empty-state{
        text-align:center;
        padding:48px 24px;
        color:#94a3b8;
        font-size:14px;
    }
    .hold-available{
        display:inline-block;
        padding:2px 8px;
        border-radius:6px;
        font-size:11px;
        font-weight:600;
    }
    .hold-available.yes{
        background:#d1fae5;
        color:#065f46;
    }
    .hold-available.no{
        background:#fee2e2;
        color:#991b1b;
    }

    /* Toast */
    .ws-toast-container{
        position:fixed;
        top:16px;
        right:16px;
        z-index:99999;
        display:flex;
        flex-direction:column;
        gap:8px;
    }
    .ws-toast{
        padding:12px 16px;
        border-radius:10px;
        color:#fff;
        font-size:13px;
        font-weight:500;
        box-shadow:0 4px 12px rgba(0,0,0,.15);
        animation:toastIn .3s ease;
    }
    .ws-toast.info{
        background:#2563eb;
    }
    .ws-toast .toast-close{
        background:none;
        border:none;
        color:rgba(255,255,255,.7);
        cursor:pointer;
        font-size:16px;
        margin-left:12px;
    }
    @keyframes toastIn{
        from{
            opacity:0;
            transform:translateY(-8px);
        }
        to{
            opacity:1;
            transform:translateY(0);
        }
    }
</style>

<div class="borrow-tabs" id="borrowTabs">
    <button class="borrow-tab active" data-tab="tab-pending" type="button">
        Chờ duyệt
        <c:if test="${pendingCount gt 0}"><span class="tab-badge danger" id="pendingTabBadge">${pendingCount}</span></c:if>
        </button>
        <button class="borrow-tab" data-tab="tab-all" type="button">Tất cả phiếu mượn</button>
        <button class="borrow-tab" data-tab="tab-holds" type="button">
            Giữ chỗ
        <c:if test="${not empty activeHolds}"><span class="tab-badge warning">${fn:length(activeHolds)}</span></c:if>
        </button>
    </div>

<%-- ==================== TAB 1: PENDING ==================== --%>
<div class="tab-pane active" id="tab-pending">
    <c:choose>
        <c:when test="${empty pendingBorrows}">
            <div class="empty-state">Không có yêu cầu mượn nào đang chờ duyệt.</div>
        </c:when>
        <c:otherwise>
            <div class="pending-grid">
                <c:forEach var="pb" items="${pendingBorrows}">
                    <div class="pending-card">
                        <div style="display:flex;justify-content:space-between;align-items:start;margin-bottom:8px;">
                            <div>
                                <span class="status pending">Chờ duyệt</span>
                                <span style="color:#94a3b8;font-size:12px;margin-left:4px;">#${pb.borrowID}</span>
                            </div>
                            <span style="color:#94a3b8;font-size:12px;">${pb.borrowDate}</span>
                        </div>
                        <div style="margin-bottom:8px;">
                            <div style="font-weight:600;font-size:14px;">${pb.studentName}</div>
                            <div style="color:#94a3b8;font-size:12px;">Nhân viên: ${pb.staffName}</div>
                        </div>
                        <div style="background:#f8fafc;border-radius:6px;padding:8px;margin-bottom:8px;font-size:12px;">
                            <div style="color:#94a3b8;margin-bottom:2px;">Sách mượn:</div>
                            <div style="font-weight:500;">${pb.items}</div>
                        </div>
                        <div style="font-size:12px;margin-bottom:10px;">
                            <span style="color:#94a3b8;">Hạn trả:</span>
                            <span style="font-weight:600;">${pb.dueDate}</span>
                        </div>
                        <div style="display:flex;gap:8px;">
                            <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" style="flex:1;margin:0;"
                                  onsubmit="return confirm('Duyệt phiếu mượn #${pb.borrowID}?');">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="borrowID" value="${pb.borrowID}">
                                <button class="action-btn approve" type="submit" style="width:100%;">Duyệt</button>
                            </form>
                            <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" style="flex:1;margin:0;"
                                  onsubmit="return confirm('Từ chối phiếu mượn #${pb.borrowID}?');">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="borrowID" value="${pb.borrowID}">
                                <button class="action-btn reject" type="submit" style="width:100%;">Từ chối</button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<%-- ==================== TAB 2: ALL BORROWS ==================== --%>
<div class="tab-pane" id="tab-all">
    <table>
        <thead>
            <tr><th>Mã</th><th>Sinh viên</th><th>Nhân viên</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th><th>Sách</th><th>Hành động</th></tr>
        </thead>
        <tbody>
            <c:forEach var="b" items="${borrows}">
                <tr>
                    <td>${b.borrowID}</td>
                    <td>${b.studentName}</td>
                    <td>${b.staffName}</td>
                    <td>${b.borrowDate}</td>
                    <td>${b.dueDate}</td>
                    <td><c:out value="${b.returnDate}" default="—"/></td>
                    <td>
                        <c:choose>
                            <c:when test="${b.status eq 'Pending'}"><span class="status pending">Chờ duyệt</span></c:when>
                            <c:when test="${b.status eq 'Borrowing'}"><span class="status borrowing">Đang mượn</span></c:when>
                            <c:when test="${b.status eq 'Returned'}"><span class="status returned">Đã trả</span></c:when>
                            <c:when test="${b.status eq 'Overdue'}"><span class="status overdue">Quá hạn</span></c:when>
                            <c:when test="${b.status eq 'Rejected'}"><span class="status rejected">Đã từ chối</span></c:when>
                            <c:otherwise><span class="status">${b.status}</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td>${b.items}</td>
                    <td>
                        <c:if test="${b.status eq 'Pending'}">
                            <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="inline-form"
                                  onsubmit="return confirm('Duyệt phiếu #${b.borrowID}?');">
                                <input type="hidden" name="action" value="approve">
                                <input type="hidden" name="borrowID" value="${b.borrowID}">
                                <button class="action-btn approve" type="submit">Duyệt</button>
                            </form>
                            <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="inline-form"
                                  onsubmit="return confirm('Từ chối phiếu #${b.borrowID}?');">
                                <input type="hidden" name="action" value="reject">
                                <input type="hidden" name="borrowID" value="${b.borrowID}">
                                <button class="action-btn reject" type="submit">Từ chối</button>
                            </form>
                        </c:if>
                        <c:if test="${b.status eq 'Borrowing' || b.status eq 'Overdue'}">
                            <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="inline-form"
                                  onsubmit="return confirm('Xác nhận trả sách phiếu #${b.borrowID}?');">
                                <input type="hidden" name="action" value="return">
                                <input type="hidden" name="borrowID" value="${b.borrowID}">
                                <button class="action-btn return-btn" type="submit">Xác nhận trả</button>
                            </form>
                        </c:if>
                        <c:if test="${b.status eq 'Returned' || b.status eq 'Rejected'}">
                            <span style="color:#94a3b8;font-size:12px;">—</span>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty borrows}"><tr><td colspan="9" class="empty-row">Chưa có phiếu mượn nào.</td></tr></c:if>
            </tbody>
        </table>

    <c:if test="${totalPages > 1}">
        <div class="pagination">
            <c:if test="${currentPage > 1}">
                <c:url var="prevUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${currentPage - 1}"/></c:url>
                <a class="page-link" href="${prevUrl}">Trang trước</a>
            </c:if>
            <c:forEach begin="1" end="${totalPages}" var="p">
                <c:url var="pageUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${p}"/></c:url>
                <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
            </c:forEach>
            <c:if test="${currentPage < totalPages}">
                <c:url var="nextUrl" value="/admin/borrows"><c:param name="action" value="list"/><c:param name="page" value="${currentPage + 1}"/></c:url>
                <a class="page-link" href="${nextUrl}">Trang sau</a>
            </c:if>
        </div>
    </c:if>
</div>

<%-- ==================== TAB 3: HOLDS ==================== --%>
<div class="tab-pane" id="tab-holds">
    <c:choose>
        <c:when test="${empty activeHolds}">
            <div class="empty-state">Không có yêu cầu giữ chỗ nào đang hoạt động.</div>
        </c:when>
        <c:otherwise>
            <table>
                <thead>
                    <tr><th>Mã</th><th>Sinh viên</th><th>Email</th><th>Sách</th><th>Ngày đặt</th><th>Trạng thái</th><th>Thông báo lúc</th><th>Hết hạn</th><th>Có sẵn</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="h" items="${activeHolds}">
                        <tr>
                            <td>${h.holdID}</td>
                            <td>${h.studentName}</td>
                            <td>${h.studentEmail}</td>
                            <td>${h.bookName}</td>
                            <td>${h.holdDate}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${h.status eq 'Waiting'}"><span class="status waiting">Đang chờ</span></c:when>
                                    <c:when test="${h.status eq 'Notified'}"><span class="status notified">Đã thông báo</span></c:when>
                                    <c:otherwise><span class="status">${h.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${h.notifiedDate}" default="—"/></td>
                            <td><c:out value="${h.expireDate}" default="—"/></td>
                            <td><span class="hold-available ${h.bookAvailable gt 0 ? 'yes' : 'no'}">${h.bookAvailable}</span></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<%-- === TAB SWITCHING JS === --%>
<script>
    (function () {
        var tabs = document.querySelectorAll('.borrow-tab');
        var panes = document.querySelectorAll('.tab-pane');
        tabs.forEach(function (tab) {
            tab.addEventListener('click', function () {
                tabs.forEach(function (t) {
                    t.classList.remove('active');
                });
                panes.forEach(function (p) {
                    p.classList.remove('active');
                });
                tab.classList.add('active');
                var target = document.getElementById(tab.getAttribute('data-tab'));
                if (target)
                    target.classList.add('active');
            });
        });
    })();
</script>

<%-- === WebSocket: admin notification === --%>
<script>
    (function () {
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
                    if (data.type === 'NEW_BORROW' || data.type === 'NEW_HOLD') {
                        showToast(data.message || 'Có thông báo mới!');
                        // Refresh page after 3s to show new data
                        setTimeout(function () {
                            location.reload();
                        }, 3000);
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

        // Fallback polling
        function startPolling() {
            setInterval(function () {
                if (wsConnected)
                    return;
                fetch('${pageContext.request.contextPath}/api/pending-count')
                        .then(function (r) {
                            return r.ok ? r.json() : null;
                        })
                        .then(function (data) {
                            if (!data)
                                return;
                            var badge = document.getElementById('pendingTabBadge');
                            if (badge && data.pendingCount > 0) {
                                badge.textContent = data.pendingCount;
                            }
                        }).catch(function () {});
            }, 15000);
        }

        connectWs();
        setTimeout(function () {
            if (!wsConnected)
                startPolling();
        }, 5000);

        // Toast
        function showToast(message) {
            var container = document.querySelector('.ws-toast-container');
            if (!container) {
                container = document.createElement('div');
                container.className = 'ws-toast-container';
                document.body.appendChild(container);
            }
            var toast = document.createElement('div');
            toast.className = 'ws-toast info';
            toast.innerHTML = message + '<button class="toast-close" onclick="this.parentElement.remove()">×</button>';
            container.appendChild(toast);
            setTimeout(function () {
                if (toast.parentElement)
                    toast.remove();
            }, 6000);
        }
    })();
</script>

<%@ include file="../admin/layout/_admin_footer.jsp" %>
