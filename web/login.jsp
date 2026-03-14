<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Utils.GoogleOAuthService"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cong thu vien | Dang nhap</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Dang nhap</h1>
                <p class="text-muted mb-0">Dang nhap vao he thong thu vien va cong sinh vien.</p>
            </div>

            <c:if test="${param.registered eq '1'}">
                <div class="success-box auth-message" role="status" aria-live="polite">Dang ky thanh cong. Ban co the dang nhap ngay.</div>
            </c:if>
            <c:if test="${param.reset eq '1'}">
                <div class="success-box auth-message" role="status" aria-live="polite">Mat khau da duoc cap nhat. Vui long dang nhap lai.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/LoginURL" method="post">
                <div class="field">
                    <label for="username">Ten dang nhap</label>
                    <input id="username" type="text" name="username" value="${username}" autocomplete="username" spellcheck="false" autofocus required>
                </div>
                <div class="field">
                    <label for="password">Mat khau</label>
                    <input id="password" type="password" name="password" autocomplete="current-password" required>
                </div>
                <div class="auth-link-row mb-3">
                    <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none">Quen mat khau?</a>
                </div>
                <button class="btn-submit" type="submit">Dang nhap he thong</button>
            </form>

            <div class="login-divider auth-divider"><span>hoac</span></div>

            <a class="btn-google" href="<%= GoogleOAuthService.buildAuthorizationUrl() %>">
                <span class="google-mark">G</span>
                Dang nhap bang Google
            </a>

            <div class="text-center auth-foot">
                <span class="text-muted">Chua co tai khoan sinh vien?</span>
                <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none ms-1">Dang ky</a>
            </div>
        </div>
    </main>
</body>
</html>
