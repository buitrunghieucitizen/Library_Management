<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa tệp sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="activeTab" value="bookfiles" />
    <%@ include file="../admin/_header.jsp" %>
    <div class="container">
        <div class="card">
            <h2>Sửa tệp sách</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/bookfiles?action=edit">
                <input type="hidden" name="bookFileID" value="${bookFile.bookFileID}">

                <label>Sách</label>
                <select name="bookID" required>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.bookID}" ${book.bookID == bookFile.bookID ? 'selected' : ''}>${book.bookID} - ${book.bookName}</option>
                    </c:forEach>
                </select>

                <label>Tên tệp</label>
                <input type="text" name="fileName" value="${bookFile.fileName}" required>

                <label>Liên kết tệp</label>
                <input type="text" name="fileUrl" value="${bookFile.fileUrl}" required>

                <label>Loại tệp</label>
                <input type="text" name="fileType" value="${bookFile.fileType}">

                <label>Kích thước tệp</label>
                <input type="number" name="fileSize" min="0" value="${bookFile.fileSize}">

                <label class="toggle-label">
                    <input class="checkbox" type="checkbox" name="isActive" ${bookFile.isActive ? 'checked' : ''}>
                    Đang kích hoạt
                </label>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cập nhật</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/bookfiles">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>


