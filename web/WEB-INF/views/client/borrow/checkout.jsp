<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - Library Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body class="student-body">
    <%@ include file="../_header.jsp" %>

    <c:url var="borrowListUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Checkout Workspace</span>
                    <h1>Checkout đơn mua sách</h1>
                    <p>Kiểm tra lại đơn mua, số lượng hợp lệ và tổng tạm tính trước khi gửi duyệt cho staff hoặc admin.</p>
                </div>
                <div class="page-hero-actions">
                    <a class="hero-action primary" href="${borrowListUrl}">Quay lại danh sách cần mua</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card"><span>Sinh viên</span><strong>#${studentId}</strong><p>Tài khoản đang thao tác với checkout hiện tại.</p></article>
                <article class="student-kpi-card"><span>Số đầu sách</span><strong>${checkoutItemCount}</strong><p>Tổng số dòng sản phẩm trong đơn.</p></article>
                <article class="student-kpi-card"><span>Tổng số lượng</span><strong>${checkoutQuantity}</strong><p>Số cuốn đang chuẩn bị gửi duyệt.</p></article>
                <article class="student-kpi-card"><span>Item lỗi</span><strong>${checkoutInvalidCount}</strong><p>Cần xử lý trước khi có thể xác nhận checkout.</p></article>
            </section>

            <c:if test="${not empty param.msg}"><div class="msg">${param.msg}</div></c:if>
            <c:if test="${not empty param.error}"><div class="error">${param.error}</div></c:if>

            <div class="checkout-layout">
                <section class="card table-card">
                    <div class="section-header-inline">
                        <div><h2>Chi tiết đơn hàng</h2><div class="note">Rà soát từng dòng trước khi gửi duyệt.</div></div>
                    </div>
                    <div class="table-scroll">
                        <table>
                            <thead><tr><th>Mã sách</th><th>Tên sách</th><th>Số lượng</th><th>Đơn giá</th><th>Thành tiền</th><th>Trạng thái</th></tr></thead>
                            <tbody>
                                <c:forEach var="item" items="${checkoutItems}">
                                    <tr>
                                        <td>${item.bookID}</td><td>${item.bookName}</td><td>${item.quantity}</td>
                                        <td class="text-right">${item.unitPrice} <c:out value="${item.currency}" default="" /></td>
                                        <td class="text-right">${item.lineTotal}</td>
                                        <td><c:choose><c:when test="${item.canOrder}"><span class="status-ok">Hợp lệ</span></c:when><c:otherwise><span class="status-bad">Không hợp lệ</span><div class="note">Hết hàng hoặc chưa có giá bán.</div></c:otherwise></c:choose></td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty checkoutItems}"><tr><td colspan="6" class="empty">Danh sách cần mua đang trống.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                </section>

                <aside class="card table-card checkout-summary">
                    <div class="section-header-inline">
                        <div><h3>Tổng quan checkout</h3><div class="note">Khóa cuối trước khi gửi duyệt.</div></div>
                    </div>
                    <div class="summary-list">
                        <div class="summary-row"><span>Số đầu sách</span><strong>${checkoutItemCount}</strong></div>
                        <div class="summary-row"><span>Tổng số lượng</span><strong>${checkoutQuantity}</strong></div>
                        <div class="summary-row"><span>Item không hợp lệ</span><strong>${checkoutInvalidCount}</strong></div>
                        <div class="summary-row total"><span>Tổng tạm tính</span><strong>${checkoutTotal}</strong></div>
                    </div>
                    <c:if test="${checkoutInvalidCount > 0}">
                        <div class="error mt-4">Có ${checkoutInvalidCount} item chưa hợp lệ. Vui lòng quay lại danh sách cần mua để cập nhật.</div>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="mt-4">
                        <input type="hidden" name="action" value="orderBuyAll">
                        <button class="btn btn-approve btn-block" type="submit" <c:if test="${checkoutInvalidCount > 0}">disabled</c:if>>Xác nhận checkout</button>
                    </form>
                </aside>
            </div>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
