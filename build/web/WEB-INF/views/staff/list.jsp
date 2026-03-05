<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
        <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quản lý nhân viên</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/staffs?action=create">Thêm nhân viên</a>

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
                        <th>Tên</th>
                        <th>Tên đăng nhập</th>
                        <th>Mật khẩu</th>
                        <th>Vai trò</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${staffRows}">
                        <tr>
                            <td>${row.staff.staffID}</td>
                            <td>${row.staff.staffName}</td>
                            <td>${row.staff.username}</td>
                            <td>${row.staff.password}</td>
                            <td>${row.roleNames}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/staffs?action=edit&id=${row.staff.staffID}">Sửa</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/staffs?action=delete&id=${row.staff.staffID}" onclick="return confirm('Xóa nhân viên này?')">Xóa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty staffRows}">
                        <tr>
                            <td colspan="6" class="empty-row">Chưa có tài khoản nhân viên nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>


