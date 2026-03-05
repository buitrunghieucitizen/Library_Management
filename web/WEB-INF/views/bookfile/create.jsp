<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm tệp sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="container">
        <div class="card">
            <h2>Thêm tệp sách</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/bookfiles?action=create">
                <label>Sách</label>
                <select name="bookID" required>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.bookID}">${book.bookID} - ${book.bookName}</option>
                    </c:forEach>
                </select>

                <label>Tên tệp</label>
                <input type="text" name="fileName" required>

                <label>Liên kết tệp</label>
                <input type="text" name="fileUrl" required>

                <label>Loại tệp</label>
                <input type="text" name="fileType">

                <label>Kích thước tệp</label>
                <input type="number" name="fileSize" min="0" value="0">

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Lưu</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/bookfiles">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>


