<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng thư viện | Đăng ký</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">+</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Đăng ký tài khoản</h1>
                <p class="text-muted mb-0">Tạo tài khoản sinh viên để truy cập cổng sinh viên.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/register" method="post">
                <div class="field">
                    <label for="name">Họ và tên</label>
                    <input id="name" type="text" name="name" value="${name}" autocomplete="name" required>
                </div>
                <div class="field">
                    <label for="email">Email</label>
                    <input id="email" type="email" name="email" value="${email}" autocomplete="email" inputmode="email" spellcheck="false" required>
                    <p class="auth-help">Email này được dùng để nhận OTP khi quên mật khẩu.</p>
                </div>
                <div class="field">
                    <label for="username">Tên đăng nhập</label>
                    <input id="username" type="text" name="username" value="${username}" autocomplete="username" spellcheck="false" required>
                    <p class="auth-help">Bạn vẫn đăng nhập bằng tên đăng nhập, email được lưu riêng để khôi phục mật khẩu.</p>
                </div>
                <div class="field">
                    <label for="password">Mật khẩu</label>
                    <input id="password" type="password" name="password" autocomplete="new-password" minlength="6" required>
                </div>
                <div class="field">
                    <label for="confirm">Xác nhận mật khẩu</label>
                    <input id="confirm" type="password" name="confirm" autocomplete="new-password" minlength="6" required>
                </div>
                <button class="btn-submit" type="submit">Tạo tài khoản</button>
            </form>

            <div class="text-center auth-foot">
                <span class="text-muted">Đã có tài khoản?</span>
                <a href="${pageContext.request.contextPath}/LoginURL" class="fw-bold text-decoration-none ms-1">Đăng nhập</a>
            </div>
        </div>
    </main>
</body>
</html>
