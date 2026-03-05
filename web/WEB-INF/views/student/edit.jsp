<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8"><title>Sửa sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar"><h1>Quản lý thư viện</h1><a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a><a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a></div>
    <div class="container"><div class="card">
        <h2>Sửa sinh viên</h2>
        <form method="POST" action="${pageContext.request.contextPath}/admin/students?action=edit">
            <input type="hidden" name="studentID" value="${student.studentID}">
            <label>Tên sinh viên</label><input type="text" name="studentName" value="${student.studentName}" required>
            <label>Thư điện tử</label><input type="text" name="email" value="${student.email}">
            <label>Số điện thoại</label><input type="text" name="phone" value="${student.phone}">
            <button class="btn btn-primary" type="submit">Cập nhật</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/students">Hủy</a>
        </form>
    </div></div>
</body>
</html>




