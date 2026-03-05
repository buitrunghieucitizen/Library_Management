<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
            <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
            <a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a>
            <a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a>
            <a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a>
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quản lý đơn hàng</h2>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>

            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Sinh viên</th>
                        <th>Xử lý bởi</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Chi tiết</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>${order.orderID}</td>
                            <td>${order.studentName}</td>
                            <td>${order.staffName}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>
                                <span class="status ${order.status eq 'Pending' ? 'pending' : (order.status eq 'Approved' ? 'approved' : 'rejected')}">
                                    ${order.status eq 'Pending' ? 'Đang chờ' : (order.status eq 'Approved' ? 'Đã duyệt' : (order.status eq 'Rejected' ? 'Đã từ chối' : order.status))}
                                </span>
                            </td>
                            <td>${empty order.items ? 'Không có chi tiết' : order.items}</td>
                            <td>
                                <c:if test="${order.status eq 'Pending'}">
                                    <div class="actions">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <button class="btn btn-approve" type="submit">Duyệt</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <button class="btn btn-reject" type="submit">Từ chối</button>
                                        </form>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}">
                        <tr>
                            <td colspan="8" class="empty-row">Chưa có đơn hàng nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>


