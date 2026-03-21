<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout Thành Công - Library Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260321-student-ui">
</head>
<body class="student-body">
    <%@ include file="../_header.jsp" %>

    <c:url var="borrowListUrl" value="/borrows"><c:param name="action" value="list" /></c:url>
    <c:url var="checkoutUrl" value="/borrows"><c:param name="action" value="checkout" /></c:url>

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Checkout Completed</span>
                    <h1>Checkout thành công</h1>
                    <p>Đơn mua của bạn đã được tạo và đang chờ staff hoặc admin xử lý trong hệ thống.</p>
                </div>
                <div class="page-hero-actions">
                    <a class="hero-action primary" href="${borrowListUrl}">Về trung tâm mượn trả</a>
                    <a class="hero-action secondary" href="${checkoutUrl}">Tạo checkout mới</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card"><span>Mã đơn</span><strong>#${successOrder.orderID}</strong><p>Đơn đang được theo dõi trong lịch sử mua.</p></article>
                <article class="student-kpi-card"><span>Số đầu sách</span><strong>${successItemCount}</strong><p>Tổng số loại sách trong đơn checkout.</p></article>
                <article class="student-kpi-card"><span>Tổng số lượng</span><strong>${successTotalQuantity}</strong><p>Số cuốn đã được gửi lên hệ thống.</p></article>
                <article class="student-kpi-card"><span>Tổng giá trị</span><strong>${successOrder.totalAmount}</strong><p>Giá trị đơn hiện tại theo bảng giá áp dụng.</p></article>
            </section>

            <section class="card table-card">
                <h2>Thông tin đơn vừa tạo</h2>
                <div class="msg">Đơn mua của bạn đã được tạo thành công và đang chờ staff hoặc admin duyệt.</div>
                <div class="summary-list mt-4">
                    <div class="summary-row"><span>Mã đơn</span><strong>#${successOrder.orderID}</strong></div>
                    <div class="summary-row"><span>Ngày đặt</span><strong>${successOrder.orderDate}</strong></div>
                    <div class="summary-row">
                        <span>Trạng thái</span>
                        <strong>
                            <c:choose>
                                <c:when test="${successOrder.status eq 'Pending'}"><span class="status waiting">Đang chờ duyệt</span></c:when>
                                <c:when test="${successOrder.status eq 'Approved'}"><span class="status approved">Đã duyệt</span></c:when>
                                <c:when test="${successOrder.status eq 'Rejected'}"><span class="status rejected">Đã từ chối</span></c:when>
                                <c:otherwise>${successOrder.status}</c:otherwise>
                            </c:choose>
                        </strong>
                    </div>
                    <div class="summary-row"><span>Số đầu sách</span><strong>${successItemCount}</strong></div>
                    <div class="summary-row"><span>Tổng số lượng</span><strong>${successTotalQuantity}</strong></div>
                    <div class="summary-row total"><span>Tổng giá trị đơn</span><strong>${successOrder.totalAmount}</strong></div>
                </div>
            </section>

            <section class="card table-card">
                <div class="section-header-inline">
                    <div>
                        <h3>Chi tiết danh sách trong đơn</h3>
                        <div class="note">Thông tin này cũng sẽ xuất hiện trong lịch sử đơn mua của bạn.</div>
                    </div>
                </div>
                <div class="table-scroll">
                    <table class="compact-table">
                        <thead><tr><th>Mã sách</th><th>Tên sách</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>
                        <tbody>
                            <c:forEach var="item" items="${successItems}">
                                <tr><td>${item.bookID}</td><td>${item.bookName}</td><td>${item.quantity}</td><td class="text-right">${item.unitPrice}</td><td class="text-right">${item.quantity * item.unitPrice}</td></tr>
                            </c:forEach>
                            <c:if test="${empty successItems}"><tr><td colspan="5" class="empty">Không có chi tiết đơn hàng.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
