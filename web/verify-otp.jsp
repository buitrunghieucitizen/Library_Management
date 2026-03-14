<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Manager | Xac thuc OTP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css">
</head>
<body class="auth-page">
    <main class="auth-wrap" aria-labelledby="auth-title">
        <div class="card-shell auth-card">
            <div class="text-center mb-4">
                <div class="brand">LM</div>
                <h1 id="auth-title" class="h3 fw-bold mb-2">Xac thuc OTP</h1>
                <p class="text-muted mb-0">Nhap ma OTP da gui den email tai khoan.</p>
            </div>

            <c:if test="${not empty message}">
                <div class="success-box auth-message" role="status" aria-live="polite">${message}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="error-box auth-message" role="alert" aria-live="assertive">${error}</div>
            </c:if>

            <form class="auth-form" action="${pageContext.request.contextPath}/verify-otp" method="post">
                <div class="field">
                    <label for="otp">Ma OTP</label>
                    <input id="otp" type="text" name="otp" maxlength="6" minlength="6" inputmode="numeric" autocomplete="one-time-code" pattern="[0-9]{6}" title="OTP gom 6 chu so" required>
                </div>
                <button class="btn-submit" type="submit">Xac nhan OTP</button>
            </form>

            <div class="text-center auth-foot">
                <a href="${pageContext.request.contextPath}/forgot-password" class="fw-bold text-decoration-none">Gui lai OTP</a>
            </div>
        </div>
    </main>
</body>
</html>
