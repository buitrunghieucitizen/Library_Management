<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Th&#234;m Th&#7875; lo&#7841;i</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Qu&#7843;n l&#253; th&#432; vi&#7879;n</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang ch&#7911;</a>
        <a href="${pageContext.request.contextPath}/admin/categories">Th&#7875; lo&#7841;i</a>
    </div>
    <div class="container">
        <div class="card">
            <h2>Th&#234;m Th&#7875; lo&#7841;i</h2>
            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>
            <form method="POST" action="${pageContext.request.contextPath}/admin/categories?action=create">
                <label>T&#234;n th&#7875; lo&#7841;i</label>
                <input type="text" name="categoryName" value="${categoryName}" required>
                <button class="btn btn-primary" type="submit">L&#432;u</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/categories">H&#7911;y</a>
            </form>
        </div>
    </div>
</body>
</html>
