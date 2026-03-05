<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Thêm Thể loại</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar"><h1>Quản lý thư viện</h1><a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a></div>
    <div class="container"><div class="card">
        <h2>Thêm Thể loại</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/categories?action=create">
            <label>Tên thể loại</label><input type="text" name="categoryName" required>
            <button class="btn btn-primary" type="submit">Lưu</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/categories">Hủy</a>
        </form>
    </div></div>
</body>
</html>





