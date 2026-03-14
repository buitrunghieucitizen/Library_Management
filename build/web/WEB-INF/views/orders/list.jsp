<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly don hang</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="orders" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <h2>Quay thu ngan va quan ly don hang</h2>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>

            <div class="search-form order-search">
                <form action="${pageContext.request.contextPath}/admin/orders" method="GET">
                    <div class="search-row search-row-orders mb-0">
                        <div class="field mb-0">
                            <label for="txtOrderID">Nhap ma don hang sinh vien cung cap</label>
                            <input type="number" id="txtOrderID" name="orderID" required placeholder="Vi du: 15">
                        </div>
                        <button type="submit" class="btn btn-approve">Tra cuu / Kiem tra</button>
                        <a class="btn btn-secondary text-decoration-none" href="${pageContext.request.contextPath}/admin/orders?action=list">Lam moi</a>
                    </div>
                </form>
            </div>

            <c:if test="${not empty searchResult}">
                <div class="details-box">
                    <h3 class="details-title">Chi tiet don hang #${searchResult.orderID}</h3>
                    <p class="details-meta">Ma sinh vien dat: <strong>${searchResult.studentID}</strong></p>
                    <p class="details-meta">Trang thai hien tai: <strong class="order-status">${searchResult.status}</strong></p>
                    <p class="details-meta">Tong tien phai thu: <strong class="order-total">${searchResult.totalAmount} VND</strong></p>

                    <h4>Danh sach sach can lay tren ke:</h4>
                    <ul class="details-list">
                        <c:forEach var="item" items="${orderItems}">
                            <li>Sach: <strong>${item.bookName}</strong> | So luong: <strong>${item.quantity}</strong> (Don gia: ${item.unitPrice})</li>
                        </c:forEach>
                    </ul>
                    <c:if test="${empty orderItems}">
                        <p class="note">Don hang nay chua co chi tiet sach.</p>
                    </c:if>

                    <c:if test="${searchResult.status eq 'Sẵn sàng' or searchResult.status eq 'Hàng chờ' or searchResult.status eq 'Pending' or searchResult.status eq 'Approved'}">
                        <div class="order-actions">
                            <form method="POST" class="inline-form" action="${pageContext.request.contextPath}/admin/orders" onsubmit="return confirm('Xac nhan da thu tien va giao sach cho sinh vien?');">
                                <input type="hidden" name="action" value="complete">
                                <input type="hidden" name="orderID" value="${searchResult.orderID}">
                                <button type="submit" class="btn btn-approve">Da thu tien va hoan thanh giao sach</button>
                            </form>

                            <form method="POST" class="inline-form" action="${pageContext.request.contextPath}/admin/orders" onsubmit="return confirm('Ban co chac chan muon huy don nay?');">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="orderID" value="${searchResult.orderID}">
                                <button type="submit" class="btn btn-reject">Huy bo don</button>
                            </form>
                        </div>
                    </c:if>
                </div>
            </c:if>

            <div class="divider"></div>

            <h3>Danh sach toan bo don hang</h3>
            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Sinh vien</th>
                        <th>Xu ly boi</th>
                        <th>Ngay dat</th>
                        <th>Tong tien</th>
                        <th>Trang thai</th>
                        <th>Chi tiet</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${allOrders}">
                        <tr>
                            <td><strong>#${order.orderID}</strong></td>
                            <td>${order.studentName}</td>
                            <td>${empty order.staffName ? '-' : order.staffName}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status eq 'Đã giao' or order.status eq 'Approved'}">
                                        <span class="status status-delivered">${order.status}</span>
                                    </c:when>
                                    <c:when test="${order.status eq 'Đã hủy' or order.status eq 'Rejected'}">
                                        <span class="status status-canceled">${order.status}</span>
                                    </c:when>
                                    <c:when test="${order.status eq 'Sẵn sàng'}">
                                        <span class="status status-ready">${order.status}</span>
                                    </c:when>
                                    <c:when test="${order.status eq 'Hàng chờ' or order.status eq 'Pending'}">
                                        <span class="status status-waiting">${order.status}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status">${order.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${empty order.items ? 'Khong co chi tiet' : order.items}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${order.status eq 'Pending'}">
                                        <div class="order-action-stack">
                                            <form method="POST" class="inline-form" action="${pageContext.request.contextPath}/admin/orders" onsubmit="return confirm('Xac nhan duyet don hang nay?');">
                                                <input type="hidden" name="action" value="approve">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <button type="submit" class="btn btn-approve">Duyet</button>
                                            </form>
                                            <form method="POST" class="inline-form" action="${pageContext.request.contextPath}/admin/orders" onsubmit="return confirm('Xac nhan tu choi don hang nay?');">
                                                <input type="hidden" name="action" value="reject">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <button type="submit" class="btn btn-reject">Tu choi</button>
                                            </form>
                                        </div>
                                    </c:when>
                                    <c:when test="${order.status ne 'Pending' and order.status ne 'Đã giao' and order.status ne 'Đã hủy' and order.status ne 'Approved' and order.status ne 'Rejected'}">
                                        <a href="${pageContext.request.contextPath}/admin/orders?orderID=${order.orderID}" class="btn btn-approve text-decoration-none">Xu ly</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-subtle">Da xu ly</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty allOrders}">
                        <tr>
                            <td colspan="8" class="empty-row">Chua co don hang nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
