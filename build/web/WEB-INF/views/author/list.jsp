<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Danh sách tác giả</title>
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
        <h2>Danh sách tác giả</h2>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/authors?action=create">+ Thêm tác giả</a>
        <table>
            <thead><tr><th>Mã</th><th>Tên tác giả</th><th>Hành động</th></tr></thead>
            <tbody>
                <c:forEach var="a" items="${authors}">
                    <tr>
                        <td>${a.authorID}</td><td>${a.authorName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/authors?action=edit&id=${a.authorID}">Sửa</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/authors?action=delete&id=${a.authorID}" onclick="return confirm('Xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty authors}"><tr><td colspan="3" class="empty-row-lg">Chưa có tác giả.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</body>
</html>





