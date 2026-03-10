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
            <div class="section-header">
                <h2 class="mb-0">Checkout don mua sach</h2>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/borrows?action=list">Quay lai danh sach can mua</a>
            </div>
            <p class="note">
                Sinh vien: <strong>#${studentId}</strong>.
                Kiem tra thong tin don hang truoc khi gui duyet cho staff/admin.
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
                <h3>Chi tiet don hang</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Ma sach</th>
                            <th>Ten sach</th>
                            <th>So luong</th>
                            <th>Don gia</th>
                            <th>Thanh tien</th>
                            <th>Trang thai</th>
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
                                            <span class="status-ok">Hop le</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-bad">Khong hop le</span>
                                            <div class="note">Het hang hoac chua co gia ban.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty checkoutItems}">
                            <tr><td colspan="6" class="empty">Danh sach can mua dang trong.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <div class="card checkout-summary">
                <h3>Tong quan checkout</h3>
                <div class="summary-list">
                    <div class="summary-row">
                        <span>So dau sach</span>
                        <strong>${checkoutItemCount}</strong>
                    </div>
                    <div class="summary-row">
                        <span>Tong so luong</span>
                        <strong>${checkoutQuantity}</strong>
                    </div>
                    <div class="summary-row">
                        <span>Item khong hop le</span>
                        <strong>${checkoutInvalidCount}</strong>
                    </div>
                    <div class="summary-row total">
                        <span>Tong tam tinh</span>
                        <strong>${checkoutTotal}</strong>
                    </div>
                </div>

                <c:if test="${checkoutInvalidCount > 0}">
                    <div class="error mt-4">
                        Co ${checkoutInvalidCount} item chua hop le. Vui long quay lai danh sach can mua de cap nhat.
                    </div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/borrows" class="mt-4">
                    <input type="hidden" name="action" value="orderBuyAll">
                    <button class="btn btn-approve btn-block" type="submit" <c:if test="${checkoutInvalidCount > 0}">disabled</c:if>>
                        Xac nhan checkout
                    </button>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
