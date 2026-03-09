<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Sửa Thể loại</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="categories" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container"><div class="card">
        <h2>Sửa Thể loại</h2>
        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>
        <form method="POST" action="${pageContext.request.contextPath}/admin/categories?action=edit">
            <input type="hidden" name="categoryID" value="${category.categoryID}">
            <label>Tên thể loại</label><input type="text" name="categoryName" value="${category.categoryName}" required>
            <button class="btn btn-primary" type="submit">Cập nhật</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/categories">Hủy</a>
        </form>
    </div></div>
</body>
</html>





