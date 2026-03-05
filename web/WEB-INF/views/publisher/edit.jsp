<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Sửa nhà xuất bản</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar"><h1>Quản lý thư viện</h1><a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a></div>
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





