<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:url var="homeUrl" value="/home" />
<c:url var="profileUrl" value="/profile" />
<c:url var="logoutUrl" value="/logout" />
<c:url var="borrowsUrl" value="/borrows">
    <c:param name="action" value="list" />
</c:url>
<c:set var="displayName"
       value="${empty requestScope.studentDisplayName ? (empty sessionScope.staff.staffName ? 'Sinh viên thư viện' : sessionScope.staff.staffName) : requestScope.studentDisplayName}" />
<c:set var="displayInitial"
       value="${empty requestScope.studentDisplayInitial ? 'S' : requestScope.studentDisplayInitial}" />
<c:set var="safeBorrowCartSize" value="${empty borrowCartSize ? 0 : borrowCartSize}" />
<c:set var="safeMaxCartSize" value="${empty maxCartSize ? 3 : maxCartSize}" />

<header class="student-top-nav">
    <div class="top-nav-shell">
        <div class="top-nav-left">
            <button class="student-nav-toggle" type="button" id="studentNavToggle"
                    aria-controls="studentSidebar" aria-expanded="false"
                    aria-label="Mở menu sinh viên" title="Mở menu sinh viên">
                <span></span>
                <span></span>
                <span></span>
            </button>

            <a class="brand" href="${homeUrl}">
                <span class="brand-mark">LM</span>
                <span class="brand-copy">
                    <span class="brand-overline">Library Manager</span>
                    <strong>Cổng thư viện</strong>
                </span>
            </a>
        </div>

        <div class="nav-right student-header-actions">
            <c:if test="${not empty sessionScope.staff}">
                <details class="student-cart-menu">
                    <summary class="student-cart-trigger" title="Giỏ mượn sách">
                        <span class="student-cart-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24"
                                 fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                                 stroke-linejoin="round">
                                <circle cx="9" cy="21" r="1"></circle>
                                <circle cx="20" cy="21" r="1"></circle>
                                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                            </svg>
                        </span>
                        <span class="student-cart-label">Giỏ mượn</span>
                        <c:if test="${safeBorrowCartSize gt 0}">
                            <span class="student-cart-count">${safeBorrowCartSize}</span>
                        </c:if>
                    </summary>

                    <div class="student-cart-panel">
                        <div class="student-cart-head">
                            <div>
                                <strong>Giỏ mượn sách</strong>
                                <span>${safeBorrowCartSize} / ${safeMaxCartSize} mục</span>
                            </div>
                            <a class="student-cart-link" href="${borrowsUrl}">Mở trung tâm</a>
                        </div>

                        <c:choose>
                            <c:when test="${empty borrowCart}">
                                <div class="student-cart-empty">
                                    Giỏ mượn đang trống.
                                    <span>Thêm sách từ trang chủ hoặc trang chi tiết để bắt đầu.</span>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="student-cart-list">
                                    <c:forEach var="cartBook" items="${borrowCart}">
                                        <div class="student-cart-item">
                                            <div class="student-cart-bookmark">
                                                ${fn:toUpperCase(fn:substring(cartBook.bookName, 0, 1))}
                                            </div>
                                            <div class="student-cart-copy">
                                                <strong>${cartBook.bookName}</strong>
                                                <span>${cartBook.available} bản còn sẵn</span>
                                            </div>
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="student-cart-form">
                                                <input type="hidden" name="action" value="removeFromCart">
                                                <input type="hidden" name="bookID" value="${cartBook.bookID}">
                                                <button type="submit" class="student-cart-remove" title="Xóa khỏi giỏ">×</button>
                                            </form>
                                        </div>
                                    </c:forEach>
                                </div>

                                <div class="student-cart-foot">
                                    <c:choose>
                                        <c:when test="${eligibility != null && eligibility.eligible}">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="student-cart-submit">
                                                <input type="hidden" name="action" value="submitBorrow">
                                                <button type="submit" class="student-cart-primary">
                                                    Gửi yêu cầu mượn (${safeBorrowCartSize})
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="student-cart-primary secondary" href="${borrowsUrl}">
                                                Kiểm tra điều kiện mượn
                                            </a>
                                            <div class="student-cart-notes">
                                                <c:if test="${eligibility != null && eligibility.hasOverdue}">
                                                    <span>Có sách quá hạn chưa trả.</span>
                                                </c:if>
                                                <c:if test="${eligibility != null && eligibility.hasUnpaidFine}">
                                                    <span>Đang còn tiền phạt chưa thanh toán.</span>
                                                </c:if>
                                                <c:if test="${eligibility != null && eligibility.remainingSlots le 0}">
                                                    <span>Đã đạt giới hạn số phiếu mượn.</span>
                                                </c:if>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </details>
            </c:if>

            <a href="${profileUrl}" class="user-chip user-chip-link student-user-chip">
                <span class="user-avatar">${displayInitial}</span>
                <span class="student-user-copy">
                    <strong><c:out value="${displayName}" /></strong>
                    <span>Hồ sơ sinh viên</span>
                </span>
            </a>

            <a href="${logoutUrl}" class="nav-button btn-logout">Đăng xuất</a>
        </div>
    </div>
</header>

<div class="student-nav-overlay" id="studentNavOverlay"></div>

<c:if test="${not empty studentId}">
    <script>
        (function () {
            var protocol = location.protocol === "https:" ? "wss:" : "ws:";
            var wsUrl = protocol + "//" + location.host + "${pageContext.request.contextPath}/ws/notify/student/${studentId}";
            var ws = null;
            var reconnectDelay = 3000;

            function connect() {
                try {
                    ws = new WebSocket(wsUrl);
                } catch (e) {
                    return;
                }

                ws.onopen = function () {
                    reconnectDelay = 3000;
                };

                ws.onmessage = function (event) {
                    try {
                        var data = JSON.parse(event.data);
                        showStudentToast(data);

                        if (data.type === "BORROW_APPROVED" || data.type === "BORROW_REJECTED"
                                || data.type === "RETURN_CONFIRMED" || data.type === "BOOK_AVAILABLE"
                                || data.type === "BOOK_CHANGED") {
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

            function getToastMeta(type) {
                if (type === "BORROW_APPROVED") {
                    return {
                        tone: "success",
                        title: "Phiếu mượn đã duyệt",
                        icon: "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2.5'><polyline points='20 6 9 17 4 12'></polyline></svg>"
                    };
                }
                if (type === "BORROW_REJECTED") {
                    return {
                        tone: "danger",
                        title: "Phiếu mượn bị từ chối",
                        icon: "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><line x1='18' y1='6' x2='6' y2='18'></line><line x1='6' y1='6' x2='18' y2='18'></line></svg>"
                    };
                }
                if (type === "BOOK_AVAILABLE") {
                    return {
                        tone: "success",
                        title: "Sách đã có sẵn",
                        icon: "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><circle cx='12' cy='12' r='10'></circle><polyline points='12 6 12 12 16 14'></polyline></svg>"
                    };
                }
                if (type === "RETURN_CONFIRMED") {
                    return {
                        tone: "info",
                        title: "Đã xác nhận trả",
                        icon: "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M4 19.5A2.5 2.5 0 0 1 6.5 17H20'></path><path d='M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z'></path></svg>"
                    };
                }
                return {
                    tone: "info",
                    title: "Thông báo mới",
                    icon: "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2'><path d='M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9'></path><path d='M13.73 21a2 2 0 0 1-3.46 0'></path></svg>"
                };
            }

            function showStudentToast(data) {
                var container = document.getElementById("studentToastContainer");
                if (!container) {
                    container = document.createElement("div");
                    container.id = "studentToastContainer";
                    container.className = "student-toast-container";
                    document.body.appendChild(container);
                }

                var meta = getToastMeta(data.type);
                var toast = document.createElement("div");
                toast.className = "student-toast student-toast-" + meta.tone;

                var closeButton = document.createElement("button");
                closeButton.type = "button";
                closeButton.className = "student-toast-close";
                closeButton.setAttribute("aria-label", "Đóng thông báo");
                closeButton.textContent = "×";
                closeButton.addEventListener("click", function () {
                    toast.remove();
                });

                var body = document.createElement("div");
                body.className = "student-toast-body";

                var icon = document.createElement("span");
                icon.className = "student-toast-icon";
                icon.innerHTML = meta.icon;

                var copy = document.createElement("div");
                copy.className = "student-toast-copy";

                var title = document.createElement("strong");
                title.className = "student-toast-title";
                title.textContent = meta.title;

                var message = document.createElement("div");
                message.className = "student-toast-message";
                message.textContent = data.message || "Có thông báo mới.";

                copy.appendChild(title);
                copy.appendChild(message);
                body.appendChild(icon);
                body.appendChild(copy);
                toast.appendChild(closeButton);
                toast.appendChild(body);
                container.appendChild(toast);

                setTimeout(function () {
                    toast.remove();
                }, 7000);
            }

            connect();
        }());
    </script>
</c:if>
