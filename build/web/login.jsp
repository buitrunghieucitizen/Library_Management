<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Utils.GoogleOAuthService" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cong thu vien | Dang nhap</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="card-shell">
        <div class="text-center mb-4">
            <div class="brand">LM</div>
            <h1 class="h3 fw-bold mb-2">Dang nhap</h1>
            <p class="text-muted mb-0">Dang nhap vao he thong thu vien va cong sinh vien.</p>
        </div>

        <% if ("1".equals(request.getParameter("registered"))) { %>
            <div class="success-box">Dang ky thanh cong. Ban co the dang nhap ngay.</div>
        <% } %>
        <% if ("1".equals(request.getParameter("reset"))) { %>
            <div class="success-box">Mat khau da duoc cap nhat. Vui long dang nhap lai.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="<%=request.getContextPath()%>/LoginURL" method="post">
            <div class="field">
                <label>Ten dang nhap</label>
                <input type="text" name="username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required>
            </div>
            <div class="field">
                <label>Mat khau</label>
                <input type="password" name="password" required>
            </div>
            <div class="text-right mb-3">
                <a href="<%=request.getContextPath()%>/forgot-password" class="text-decoration-none">Quen mat khau?</a>
            </div>
            <button class="btn-submit" type="submit">Dang nhap he thong</button>
        </form>

        <div class="login-divider"><span>hoac</span></div>

        <a class="btn-google" href="<%= GoogleOAuthService.buildAuthorizationUrl() %>">
            <span class="google-mark">G</span>
            Dang nhap bang Google
        </a>

        <div class="text-center mt-4">
            <span class="text-muted">Chua co tai khoan sinh vien?</span>
            <a href="<%=request.getContextPath()%>/register" class="fw-bold text-decoration-none ms-1">Dang ky</a>
        </div>
    </div>
</body>
</html>
