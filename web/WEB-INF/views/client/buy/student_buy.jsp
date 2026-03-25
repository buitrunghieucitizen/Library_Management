<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên - Mua sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260321-student-ui">
</head>
<body class="student-body">
    <%@ include file="../_header.jsp" %>

    <c:url var="borrowListUrl" value="/borrows"><c:param name="action" value="list" /></c:url>
    <c:url var="buyUrl" value="/buy" />

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Student Purchase Flow</span>
                    <h1>Mua sách</h1>
                    <p>Tạo danh sách chờ, chọn các sách cần checkout và theo dõi lịch sử đơn mua trong cùng giao diện.</p>
                </div>
                <div class="page-hero-actions">
                    <a class="hero-action primary" href="${borrowListUrl}">Về trung tâm mượn trả</a>
                    <a class="hero-action secondary" href="${buyUrl}">Làm mới trang mua</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card"><span>Sinh viên</span><strong><c:out value="${studentName}" default="-" /></strong><p>Mã sinh viên: <strong>#${studentId}</strong></p></article>
                <article class="student-kpi-card"><span>Sách có thể mua</span><strong>${fn:length(bookPrices)}</strong><p>Các đầu sách có giá bán trong bảng giá hiện tại.</p></article>
                <article class="student-kpi-card"><span>Danh sách chờ</span><strong>${fn:length(waitlistItems)}</strong><p>Các sách bạn đang chuẩn bị checkout.</p></article>
                <article class="student-kpi-card"><span>Lịch sử đơn</span><strong>${fn:length(orderHistory)}</strong><p>Các đơn đã gửi trước đó.</p></article>
            </section>

            <c:if test="${not empty param.msg}"><div class="msg">${param.msg}</div></c:if>
            <c:if test="${not empty param.error}"><div class="error">${param.error}</div></c:if>

            <section class="card table-card">
                <div class="section-header-inline">
                    <div><h2>Thông tin sinh viên mua hàng</h2><div class="note">Theo dõi nhanh dữ liệu tài khoản trước khi tạo đơn.</div></div>
                </div>
                <div class="summary-list">
                    <div class="summary-row"><span>Họ và tên</span><strong>${studentName}</strong></div>
                    <div class="summary-row"><span>Mã sinh viên</span><strong>${studentId}</strong></div>
                </div>
            </section>

            <section class="card table-card">
                <div class="section-header-inline">
                    <div><h3>Danh sách đầu sách có thể mua</h3><div class="note">Chỉ hiển thị các sách đã có giá bán.</div></div>
                </div>
                <div class="table-scroll">
                    <table>
                        <thead><tr><th>Tên sách</th><th>Số lượng còn</th><th>Giá bán</th><th>Số lượng mua</th><th>Hành động</th></tr></thead>
                        <tbody>
                            <c:forEach var="price" items="${bookPrices}">
                                <c:if test="${price.amount > 0}">
                                    <tr>
                                        <td>${price.bookName}</td><td>${price.available}</td><td>${price.amount}</td>
                                        <td>
                                            <form id="waitlistAdd_${price.bookID}" method="POST" action="${pageContext.request.contextPath}/buy" class="inline-form inline-edit-form">
                                                <input type="hidden" name="action" value="addToWaitlist">
                                                <input type="hidden" name="bookID" value="${price.bookID}">
                                                <input type="hidden" name="bookName" value="${price.bookName}">
                                                <input type="hidden" name="price" value="${price.amount}">
                                                <input class="qty-input qty-input-sm" type="number" name="quantity" value="1" min="1" max="${price.available > 0 ? price.available : 99}">
                                            </form>
                                        </td>
                                        <td><button class="btn btn-buy" type="submit" form="waitlistAdd_${price.bookID}">Thêm vào chờ</button></td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            <c:if test="${empty bookPrices}"><tr><td colspan="5" class="empty">Chưa có sách nào sẵn sàng để mua.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="student-buy-grid">
                <section class="card table-card" data-waitlist-panel>
                    <div class="section-header-inline">
                        <div><h3>Danh sách sách chọn tạm</h3><div class="note">Chọn các sách cần checkout hoặc xóa từng mục khỏi danh sách chờ.</div></div>
                    </div>
                    <c:set var="waitlistTotal" value="${0}" />
                    <c:forEach var="item" items="${waitlistItems}">
                        <c:set var="waitlistTotal" value="${waitlistTotal + item.totalPrice}" />
                        <form id="updateWaitlist_${item.bookId}" method="POST" action="${pageContext.request.contextPath}/buy">
                            <input type="hidden" name="action" value="updateWaitlistQty">
                            <input type="hidden" name="bookID" value="${item.bookId}">
                        </form>
                        <form id="deleteWaitlist_${item.bookId}" method="POST" action="${pageContext.request.contextPath}/buy">
                            <input type="hidden" name="action" value="removeFromWaitlist">
                            <input type="hidden" name="bookID" value="${item.bookId}">
                        </form>
                    </c:forEach>
                    <form method="POST" action="${pageContext.request.contextPath}/buy">
                        <input type="hidden" name="action" value="checkout">
                        <div class="table-scroll">
                            <table class="compact-table">
                                <thead><tr><th class="checkbox-cell"><input type="checkbox" data-select-all="buy-selection" title="Chọn tất cả"></th><th>Tên sách</th><th>SL</th><th>Thành tiền</th><th>Xóa</th></tr></thead>
                                <tbody>
                                    <c:forEach var="item" items="${waitlistItems}">
                                        <tr data-waitlist-row data-unit-price="${item.unitPrice}" data-update-form-id="updateWaitlist_${item.bookId}">
                                            <td class="checkbox-cell"><input type="checkbox" name="selectedBooks" value="${item.bookId}" data-select-item="buy-selection"></td>
                                            <td>${item.bookName}</td>
                                            <td>
                                                <div class="actions">
                                                    <input class="qty-input qty-input-sm" type="number" name="quantity" value="${item.quantity}" min="1" form="updateWaitlist_${item.bookId}" data-waitlist-qty-input>
                                                    <button type="submit" form="updateWaitlist_${item.bookId}" class="btn btn-secondary" data-waitlist-save-button>C&#7853;p nh&#7853;t</button>
                                                </div>
                                            </td>
                                            <td data-waitlist-line-total>${item.totalPrice}</td>
                                            <td><button type="submit" form="deleteWaitlist_${item.bookId}" class="link-button link-danger">Xóa</button></td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty waitlistItems}"><tr><td colspan="5" class="empty">Chưa có sách nào trong danh sách chờ.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${not empty waitlistItems}">
                            <p class="summary-note">T&#7893;ng t&#7841;m t&#237;nh hi&#7879;n t&#7841;i: <strong data-waitlist-total>${waitlistTotal}</strong>. Thay &#273;&#7893;i s&#7889; l&#432;&#7907;ng s&#7869; t&#7921; l&#432;u.</p>
                            <div class="actions mt-4"><button class="btn btn-buy" type="submit">Xác nhận đặt các sách đã chọn</button></div>
                        </c:if>
                    </form>
                </section>

                <section class="card table-card">
                    <div class="section-header-inline">
                        <div><h3>Thông tin đơn hàng của bạn</h3><div class="note">Mang mã đơn tới quầy thư viện để thanh toán và nhận sách khi trạng thái sẵn sàng.</div></div>
                    </div>
                    <div class="table-scroll">
                        <table class="compact-table">
                            <thead><tr><th>Mã đơn</th><th>Ngày đặt</th><th>Chi tiết sách</th><th>Trạng thái</th></tr></thead>
                            <tbody>
                                <c:forEach var="order" items="${orderHistory}">
                                    <tr>
                                        <td>#${order.orderID}</td><td>${order.orderDate}</td><td>${order.items}</td>
                                        <td><c:choose><c:when test="${order.status eq 'Sẵn sàng'}"><span class="status-ready">Sẵn sàng</span></c:when><c:when test="${order.status eq 'Hàng chờ'}"><span class="status-waiting">Hàng chờ</span></c:when><c:otherwise>${order.status}</c:otherwise></c:choose></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty orderHistory}"><tr><td colspan="4" class="empty">Bạn chưa có đơn đặt hàng nào.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                    <p class="summary-note">Cầm <strong>mã đơn</strong> tới quầy thư viện để thanh toán và nhận sách khi đơn đã sẵn sàng.</p>
                </section>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
