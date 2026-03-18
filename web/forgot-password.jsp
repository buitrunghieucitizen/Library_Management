<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Manager | Quen mat khau</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Quên mật khẩu</h1>
                <p class="text-muted mb-0">Nhập email đã đăng ký để nhận OTP đặt lại mật khẩu.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="field">
                    <label for="email">Email đã đăng ký</label>
                    <input id="email" type="email" name="email" value="${email}" autocomplete="email" inputmode="email" spellcheck="false" required>
                    <p id="forgot-help" class="auth-help">Hệ thống sẽ tìm tài khoản theo email và gửi OTP đến hộp thư đã liên kết.</p>
                </div>
                <button class="btn-submit" type="submit">Gửi OTP</button>
            </form>

            <div class="text-center auth-foot">
                <a href="${pageContext.request.contextPath}/LoginURL" class="fw-bold text-decoration-none">Quay lại đăng nhập</a>
            </div>
        </div>
    </main>
</body>
</html>
