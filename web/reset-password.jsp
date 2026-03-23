<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Manager | Dat lai mat khau</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Dat lai mat khau</h1>
                <p class="text-muted mb-0">Nhap mat khau moi cho tai khoan cua ban.</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/reset-password" method="post">
                <div class="field">
                    <label for="password">Mat khau moi</label>
                    <input id="password" type="password" name="password" autocomplete="new-password" minlength="6" aria-describedby="password-rule" required>
                    <div id="password-rule" class="auth-help">Yeu cau: toi thieu 6 ky tu, co chu hoa, chu thuong va so.</div>
                </div>
                <div class="field">
                    <label for="confirmPassword">Xac nhan mat khau moi</label>
                    <input id="confirmPassword" type="password" name="confirmPassword" autocomplete="new-password" minlength="6" required>
                </div>
                <button class="btn-submit" type="submit">Cap nhat mat khau</button>
            </form>

            <div class="text-center auth-foot">
                <a href="${pageContext.request.contextPath}/LoginURL" class="fw-bold text-decoration-none">Quay lai dang nhap</a>
            </div>
        </div>
    </main>
</body>
</html>
