<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Thêm Tác giả</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar"><h1>Quản lý thư viện</h1><a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></div>
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





