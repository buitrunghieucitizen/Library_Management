<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - Library Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/books">Sách</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
        <div class="nav-right">
            <span>Xin chào, ${sessionScope.staff.staffName} (Sinh viên)</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <div class="section-header">
                <h2 class="mb-0">Checkout đơn mua sách</h2>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/borrows?action=list">Quay lại danh sách cần mua</a>
            </div>
            <p class="note">
                Sinh viên: <strong>#${studentId}</strong>.
                Kiểm tra thông tin đơn hàng trước khi gửi duyệt cho staff/admin.
            </p>
            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
        </div>

        <div class="checkout-layout">
            <div class="card">
                <h3>Chi tiết đơn hàng</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Mã sách</th>
                            <th>Tên sách</th>
                            <th>Số lượng</th>
                            <th>Đơn giá</th>
                            <th>Thành tiền</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${checkoutItems}">
                            <tr>
                                <td>${item.bookID}</td>
                                <td>${item.bookName}</td>
                                <td>${item.quantity}</td>
                                <td class="text-right">${item.unitPrice} <c:out value="${item.currency}" default=""/></td>
                                <td class="text-right">${item.lineTotal}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${item.canOrder}">
                                            <span class="status-ok">Hợp lệ</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-bad">Không hợp lệ</span>
                                            <div class="note">Hết hàng hoặc chưa có giá bán.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty checkoutItems}">
                            <tr><td colspan="6" class="empty">Danh sách cần mua đang trống.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="card checkout-summary">
                <h3>Tổng quan checkout</h3>
                <div class="summary-list">
                    <div class="summary-row">
                        <span>Số đầu sách</span>
                        <strong>${checkoutItemCount}</strong>
                    </div>
                    <div class="summary-row">
                        <span>Tổng số lượng</span>
                        <strong>${checkoutQuantity}</strong>
                    </div>
                    <div class="summary-row">
                        <span>Item không hợp lệ</span>
                        <strong>${checkoutInvalidCount}</strong>
                    </div>
                    <div class="summary-row total">
                        <span>Tổng tạm tính</span>
                        <strong>${checkoutTotal}</strong>
                    </div>
                </div>

                <c:if test="${checkoutInvalidCount > 0}">
                    <div class="error mt-4">
                        Có ${checkoutInvalidCount} item chưa hợp lệ. Vui lòng quay lại danh sách cần mua để cập nhật.
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/borrows" class="mt-4">
                    <input type="hidden" name="action" value="orderBuyAll">
                    <button class="btn btn-approve btn-block" type="submit" <c:if test="${checkoutInvalidCount > 0}">disabled</c:if>>
                        Xác nhận checkout
                    </button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
