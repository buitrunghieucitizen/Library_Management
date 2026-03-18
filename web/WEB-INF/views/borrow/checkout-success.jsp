<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout Thanh Cong - Library Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/books">Sách</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
        <div class="nav-right">
            <span>Xin chào, ${sessionScope.staff.staffName} (Sinh vien)</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Checkout thành công</h2>
            <div class="msg">
                Đơn mua của bạn đã được tạo thành công và đang chờ staff/admin duyệt.
            </div>

            <div class="summary-list mt-4">
                <div class="summary-row">
                    <span>Mã đơn</span>
                    <strong>#${successOrder.orderID}</strong>
                </div>
                <div class="summary-row">
                    <span>Ngày đặt</span>
                    <strong>${successOrder.orderDate}</strong>
                </div>
                <div class="summary-row">
                    <span>Trạng thái</span>
                    <strong>
                        <c:choose>
                            <c:when test="${successOrder.status eq 'Pending'}"><span class="status pending">Đang chờ duyệt</span></c:when>
                            <c:when test="${successOrder.status eq 'Approved'}"><span class="status approved">Đã duyệt</span></c:when>
                            <c:when test="${successOrder.status eq 'Rejected'}"><span class="status rejected">Đã từ chối</span></c:when>
                            <c:otherwise>${successOrder.status}</c:otherwise>
                        </c:choose>
                    </strong>
                </div>
                <div class="summary-row">
                    <span>Số đầu sách</span>
                    <strong>${successItemCount}</strong>
                </div>
                <div class="summary-row">
                    <span>Tổng số lượng</span>
                    <strong>${successTotalQuantity}</strong>
                </div>
                <div class="summary-row total">
                    <span>Tổng giá trị đơn</span>
                    <strong>${successOrder.totalAmount}</strong>
                </div>
            </div>
        </div>

        <div class="card">
            <h3>Chi tiết danh sách trong đơn</h3>
            <table>
                <thead>
                    <tr>
                        <th>Mã sách</th>
                        <th>Tên sách</th>
                        <th>Số lượng</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${successItems}">
                        <tr>
                            <td>${item.bookID}</td>
                            <td>${item.bookName}</td>
                            <td>${item.quantity}</td>
                            <td class="text-right">${item.unitPrice}</td>
                            <td class="text-right">${item.quantity * item.unitPrice}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty successItems}">
                        <tr><td colspan="5" class="empty">Không có chi tiết đơn hàng.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <div class="actions mt-4">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/borrows?action=list">Về trung tâm mượn và mua sách</a>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/borrows?action=checkout">Mở checkout mới</a>
            </div>
        </div>
    </div>
</body>
</html>
