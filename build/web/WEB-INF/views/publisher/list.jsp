<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Danh sách nhà xuất bản</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar"><h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
        <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
        <a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a>
        <a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a>
        <a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a>
    </div>
    <div class="container">
        <h2>Danh sách nhà xuất bản</h2>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/publishers?action=create">+ Thêm nhà xuất bản</a>
        <table>
            <thead><tr><th>Mã</th><th>Tên nhà xuất bản</th><th>Hành động</th></tr></thead>
            <tbody>
                <c:forEach var="p" items="${publishers}">
                    <tr>
                        <td>${p.publisherID}</td><td>${p.publisherName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/publishers?action=edit&id=${p.publisherID}">Sửa</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${p.publisherID}" onclick="return confirm('Xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty publishers}"><tr><td colspan="3" class="empty-row-lg">Chưa có nhà xuất bản.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</body>
</html>





