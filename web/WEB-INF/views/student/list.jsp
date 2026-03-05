<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
        <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a>
            <a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a>
            <a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a>
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
        </c:if>
    </div>

    <div class="container">
        <h2>Danh sách sinh viên</h2>
        <c:if test="${isAdmin}">
            <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/students?action=create">+ Thêm sinh viên</a>
        </c:if>
        <table>
            <thead><tr><th>Mã</th><th>Tên</th><th>Thư điện tử</th><th>Số điện thoại</th><th>Hành động</th></tr></thead>
            <tbody>
                <c:forEach var="s" items="${students}">
                    <tr>
                        <td>${s.studentID}</td>
                        <td>${s.studentName}</td>
                        <td>${s.email}</td>
                        <td>${s.phone}</td>
                        <td class="actions">
                            <c:if test="${isAdmin}">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/students?action=edit&id=${s.studentID}">Sửa</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/students?action=delete&id=${s.studentID}" onclick="return confirm('Xóa?')">Xóa</a>
                            </c:if>
                            <c:if test="${not isAdmin}">
                                <span class="text-subtle">Chỉ xem</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty students}">
                    <tr><td colspan="5" class="empty-row-lg">Chưa có sinh viên.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>



