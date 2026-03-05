<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo phiếu mượn sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
    </div>

    <div class="container">
        <h2>Tạo phiếu mượn sách</h2>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/admin/borrows">
            <input type="hidden" name="action" value="create">

            <label>Sinh viên</label>
            <select name="studentID" required>
                <option value="">-- Chọn sinh viên --</option>
                <c:forEach var="s" items="${students}">
                    <option value="${s.studentID}" ${selectedStudentId == s.studentID ? 'selected' : ''}>
                        ${s.studentID} - ${s.studentName}
                    </option>
                </c:forEach>
            </select>

            <label>Sách</label>
            <select name="bookID" required>
                <option value="">-- Chọn sách --</option>
                <c:forEach var="b" items="${books}">
                    <option value="${b.bookID}" ${selectedBookId == b.bookID ? 'selected' : ''}>
                        ${b.bookID} - ${b.bookName} (còn ${b.available})
                    </option>
                </c:forEach>
            </select>
            <p class="note">Chỉ hiển thị sách còn trong kho (Còn lại &gt; 0).</p>

            <label>Số lượng mượn</label>
            <input type="number" name="quantity" min="1" value="${empty quantity ? 1 : quantity}" required>

            <label>Hạn trả</label>
            <input type="date" name="dueDate" value="${dueDate}" required>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Tạo phiếu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/borrows?action=list">Hủy</a>
            </div>
        </form>
    </div>
</body>
</html>


