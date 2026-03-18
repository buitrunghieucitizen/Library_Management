<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Manager | Đặt lại mật khẩu</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Đặt lại mật khẩu</h1>
                <p class="text-muted mb-0">Nhập mật khẩu mới cho tài khoản của bạn.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/reset-password" method="post">
                <div class="field">
                    <label for="password">Mật khẩu mới</label>
                    <input id="password" type="password" name="password" autocomplete="new-password" minlength="6" aria-describedby="password-rule" required>
                    <div id="password-rule" class="auth-help">Yêu cầu: tối thiểu 6 ký tự, có chữ hoa, chữ thường và số.</div>
                </div>
                <div class="field">
                    <label for="confirmPassword">Xác nhận mật khẩu mới</label>
                    <input id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" minlength="6" required>
                </div>
                <button class="btn-submit" type="submit">Cập nhật mật khẩu</button>
            </form>

            <div class="text-center auth-foot">
                <a href="${pageContext.request.contextPath}/LoginURL" class="fw-bold text-decoration-none">Quay lai dang nhap</a>
            </div>
        </div>
    </main>
</body>
</html>
