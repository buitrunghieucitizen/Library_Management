<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Thêm Tác giả</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="authors" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container"><div class="card">
        <h2>Thêm Tác giả</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/authors?action=create">
            <label>Tên tác giả</label><input type="text" name="authorName" required>
            <button class="btn btn-primary" type="submit">Lưu</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/authors">Hủy</a>
        </form>
    </div></div>
</body>
</html>





