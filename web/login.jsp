<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thư viện | Đăng nhập</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="card-shell">
        <div class="text-center mb-4">
            <div class="brand">LM</div>
            <h1 class="h3 fw-bold mb-2">Đăng nhập</h1>
            <p class="text-muted mb-0">Đăng nhập vào hệ thống thư viện và cổng sinh viên.</p>
        </div>

        <% if ("1".equals(request.getParameter("registered"))) { %>
            <div class="success-box">Đăng ký thành công. Bạn có thể đăng nhập ngay bây giờ.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="<%=request.getContextPath()%>/LoginURL" method="post">
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
            <button class="btn-submit" type="submit">Đăng nhập hệ thống</button>
        </form>

        <div class="text-center mt-4">
            <span class="text-muted">Chưa có tài khoản sinh viên?</span>
            <a href="<%=request.getContextPath()%>/register" class="fw-bold text-decoration-none ms-1">Đăng ký</a>
        </div>
    </div>
</body>
</html>


