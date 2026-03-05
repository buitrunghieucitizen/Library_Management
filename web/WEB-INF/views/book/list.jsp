<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:set var="isStaff" value="false" />
    <c:set var="isStudent" value="false" />
    <c:set var="isAdminSection" value="${requestScope.adminSection}" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
            <c:if test="${roleId == 2 || roleId == 4}">
                <c:set var="isStaff" value="true" />
            </c:if>
            <c:if test="${roleId == 8 || roleId == 9}">
                <c:set var="isStudent" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <c:choose>
            <c:when test="${isAdminSection}">
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
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/books">Sách</a>
                <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
            </c:otherwise>
        </c:choose>
    </div>

    <div class="container">
        <h2>Danh sách sách</h2>
        <c:if test="${isAdminSection && isAdmin}">
            <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/books?action=create">+ Thêm sách mới</a>
        </c:if>

        <c:if test="${not empty msg}">
            <div class="msg">${msg}</div>
        </c:if>

        <table>
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Tên sách</th>
                    <th>Số lượng</th>
                    <th>Còn lại</th>
                    <th>Mã thể loại</th>
                    <th>Mã nhà xuất bản</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="b" items="${books}">
                    <tr>
                        <td>${b.bookID}</td>
                        <td>${b.bookName}</td>
                        <td>${b.quantity}</td>
                        <td>${b.available}</td>
                        <td>${b.categoryID}</td>
                        <td>${b.publisherID}</td>
                        <td class="actions">
                            <c:if test="${isAdminSection && isAdmin}">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/books?action=edit&id=${b.bookID}">Sửa</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/books?action=delete&id=${b.bookID}" onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</a>
                            </c:if>
                            <c:if test="${not isAdminSection || not isAdmin}">
                                <span class="text-subtle">Chỉ xem</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty books}">
                    <tr><td colspan="7" class="empty-row-lg">Chưa có sách nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>


