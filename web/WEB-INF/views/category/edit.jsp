<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sua the loai</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="categories" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="card">
            <h2>Sua the loai</h2>

            <c:if test="${not empty error}">
                <div class="error"><c:out value="${error}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <form method="POST" action="${pageContext.request.contextPath}/admin/categories?action=edit">
                <input type="hidden" name="categoryID" value="${category.categoryID}">

                <div class="field">
                    <label for="categoryName">Ten the loai</label>
                    <input id="categoryName" type="text" name="categoryName" value="${category.categoryName}" required>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cap nhat</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/categories?action=list">Huy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
