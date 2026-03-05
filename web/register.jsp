<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thư viện | Đăng ký</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="card-shell">
        <div class="text-center mb-4">
            <div class="brand">+</div>
            <h1 class="h3 fw-bold mb-2">Đăng ký tài khoản</h1>
            <p class="text-muted mb-0">Tạo tài khoản sinh viên để truy cập cổng sinh viên.</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="<%=request.getContextPath()%>/register" method="post">
            <div class="field">
                <label>Họ và tên</label>
                <input type="text" name="name"
                       value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>"
                       required>
            </div>
            <div class="field">
                <label>Tên đăng nhập</label>
                <input type="text" name="username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required>
            </div>
            <div class="field">
                <label>Mật khẩu</label>
                <input type="password" name="password" required>
            </div>
            <div class="field">
                <label>Xác nhận mật khẩu</label>
                <input type="password" name="confirm" required>
            </div>
            <button class="btn-submit" type="submit">Tạo tài khoản</button>
        </form>

        <div class="text-center mt-4">
            <span class="text-muted">Đã có tài khoản?</span>
            <a href="<%=request.getContextPath()%>/LoginURL" class="fw-bold text-decoration-none ms-1">Đăng nhập</a>
        </div>
    </div>
</body>
</html>


