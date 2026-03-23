<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cong thu vien | Dang ky</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">+</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Dang ky tai khoan</h1>
                <p class="text-muted mb-0">Tao tai khoan sinh vien de truy cap cong sinh vien.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/register" method="post">
                <div class="field">
                    <label for="name">Ho va ten</label>
                    <input id="name" type="text" name="name" value="${name}" autocomplete="name" required>
                </div>
                <div class="field">
                    <label for="username">Ten dang nhap</label>
                    <input id="username" type="text" name="username" value="${username}" autocomplete="username" spellcheck="false" required>
                    <p class="auth-help">Nen dung email de co the khoi phuc mat khau bang OTP.</p>
                </div>
                <div class="field">
                    <label for="password">Mat khau</label>
                    <input id="password" type="password" name="password" autocomplete="new-password" minlength="6" required>
                </div>
                <div class="field">
                    <label for="confirm">Xac nhan mat khau</label>
                    <input id="confirm" type="password" name="confirm" autocomplete="new-password" minlength="6" required>
                </div>
                <button class="btn-submit" type="submit">Tao tai khoan</button>
            </form>

            <div class="text-center auth-foot">
                <span class="text-muted">Da co tai khoan?</span>
                <a href="${pageContext.request.contextPath}/LoginURL" class="fw-bold text-decoration-none ms-1">Dang nhap</a>
            </div>
        </div>
    </main>
</body>
</html>
