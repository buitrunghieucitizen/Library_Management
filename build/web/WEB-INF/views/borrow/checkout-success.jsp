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
        <h1>Quan ly thu vien</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/books">Sach</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Muon va mua sach</a>
        <div class="nav-right">
            <span>Xin chao, ${sessionScope.staff.staffName} (Sinh vien)</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Checkout thanh cong</h2>
            <div class="msg">
                Don mua cua ban da duoc tao thanh cong va dang cho staff/admin duyet.
            </div>

            <div class="summary-list mt-4">
                <div class="summary-row">
                    <span>Ma don</span>
                    <strong>#${successOrder.orderID}</strong>
                </div>
                <div class="summary-row">
                    <span>Ngay dat</span>
                    <strong>${successOrder.orderDate}</strong>
                </div>
                <div class="summary-row">
                    <span>Trang thai</span>
                    <strong>
                        <c:choose>
                            <c:when test="${successOrder.status eq 'Pending'}"><span class="status pending">Dang cho duyet</span></c:when>
                            <c:when test="${successOrder.status eq 'Approved'}"><span class="status approved">Da duyet</span></c:when>
                            <c:when test="${successOrder.status eq 'Rejected'}"><span class="status rejected">Da tu choi</span></c:when>
                            <c:otherwise>${successOrder.status}</c:otherwise>
                        </c:choose>
                    </strong>
                </div>
                <div class="summary-row">
                    <span>So dau sach</span>
                    <strong>${successItemCount}</strong>
                </div>
                <div class="summary-row">
                    <span>Tong so luong</span>
                    <strong>${successTotalQuantity}</strong>
                </div>
                <div class="summary-row total">
                    <span>Tong gia tri don</span>
                    <strong>${successOrder.totalAmount}</strong>
                </div>
            </div>
        </div>

        <div class="card">
            <h3>Chi tiet sach trong don</h3>
            <table>
                <thead>
                    <tr>
                        <th>Ma sach</th>
                        <th>Ten sach</th>
                        <th>So luong</th>
                        <th>Don gia</th>
                        <th>Thanh tien</th>
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
                        <tr><td colspan="5" class="empty">Khong co chi tiet don hang.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <div class="actions mt-4">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/borrows?action=list">Ve trung tam muon va mua sach</a>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/borrows?action=checkout">Mo checkout moi</a>
            </div>
        </div>
    </div>
</body>
</html>
