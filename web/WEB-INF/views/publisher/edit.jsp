<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Sửa nhà xuất bản</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="publishers" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container"><div class="card">
        <h2>Sửa nhà xuất bản</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/publishers?action=edit">
            <input type="hidden" name="publisherID" value="${publisher.publisherID}">
            <label>Tên nhà xuất bản</label><input type="text" name="publisherName" value="${publisher.publisherName}" required>
            <button class="btn btn-primary" type="submit">Cập nhật</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/publishers">Hủy</a>
        </form>
    </div></div>
</body>
</html>





