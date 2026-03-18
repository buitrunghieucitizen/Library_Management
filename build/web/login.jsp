<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Utils.GoogleOAuthService"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thư viện | Đăng nhập</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Đăng nhập</h1>
                <p class="text-muted mb-0">Đăng nhập vào hệ thống thư viện và cổng sinh viên.</p>
            </div>

            <c:if test="${param.registered eq '1'}">
                <div class="success-box auth-message" role="status" aria-live="polite">Đăng ký thành công. Bạn có thể đăng nhập ngay.</div>
            </c:if>
            <c:if test="${param.reset eq '1'}">
                <div class="success-box auth-message" role="status" aria-live="polite">Mật khẩu đã được cập nhật. Vui lòng đăng nhập lại.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/LoginURL" method="post">
                <div class="field">
                    <label for="username">Tên đăng nhập hoặc email</label>
                    <input id="username" type="text" name="username" value="${username}" autocomplete="username" spellcheck="false" autofocus required>
                    <p class="auth-help">Bạn có thể dùng tên đăng nhập hoặc email đã đăng ký.</p>
                </div>
                <div class="field">
                    <label for="password">Mật khẩu</label>
                    <input id="password" type="password" name="password" autocomplete="current-password" required>
                </div>
                <div class="auth-link-row mb-3">
                    <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none">Quên mật khẩu?</a>
                </div>
                <button class="btn-submit" type="submit">Đăng nhập hệ thống</button>
            </form>

            <div class="login-divider auth-divider"><span>hoặc</span></div>

            <a class="btn-google" href="<%= GoogleOAuthService.buildAuthorizationUrl() %>">
                <span class="google-mark">G</span>
                Đăng nhập bằng Google
            </a>

            <div class="text-center auth-foot">
                <span class="text-muted">Chưa có tài khoản sinh viên?</span>
                <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none ms-1">Đăng ký</a>
            </div>
        </div>
    </main>
</body>
</html>
