<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh s&#225;ch th&#7875; lo&#7841;i</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Qu&#7843;n l&#253; th&#432; vi&#7879;n</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang ch&#7911;</a>
        <a href="${pageContext.request.contextPath}/admin/books">S&#225;ch</a>
        <a href="${pageContext.request.contextPath}/admin/students">Sinh vi&#234;n</a>
        <a href="${pageContext.request.contextPath}/admin/authors">T&#225;c gi&#7843;</a>
        <a href="${pageContext.request.contextPath}/admin/categories">Th&#7875; lo&#7841;i</a>
        <a href="${pageContext.request.contextPath}/admin/publishers">Nh&#224; xu&#7845;t b&#7843;n</a>
    </div>
    <div class="container">
        <h2>Danh s&#225;ch th&#7875; lo&#7841;i</h2>
        <c:if test="${not empty param.error}">
            <div class="error"><c:out value="${param.error}" /></div>
        </c:if>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/categories?action=create">+ Th&#234;m th&#7875; lo&#7841;i</a>
        <table>
            <thead>
                <tr><th>M&#227;</th><th>T&#234;n th&#7875; lo&#7841;i</th><th>H&#224;nh &#273;&#7897;ng</th></tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${categories}">
                    <tr>
                        <td>${c.categoryID}</td>
                        <td>${c.categoryName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${c.categoryID}">S&#7917;a</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/categories?action=delete&id=${c.categoryID}" onclick="return confirm('X&#243;a?')">X&#243;a</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty categories}">
                    <tr><td colspan="3" class="empty-row-lg">Ch&#432;a c&#243; th&#7875; lo&#7841;i.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>
