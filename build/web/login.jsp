<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="Utils.GoogleOAuthService"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cổng thư viện | Đăng nhập</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260321-login">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/auth.css?v=20260321-login">
    </head>
    <body class="auth-page">
        <main class="auth-wrap login-shell" aria-labelledby="auth-title">
            <section class="login-stage">
                <aside class="login-showcase" aria-label="Giới thiệu cổng thư viện">
                    <div class="login-showcase-top">
                        <div class="login-brand-lockup">
                            <span class="login-brand-mark">LM</span>
                            <div class="login-brand-copy">
                                <span class="login-kicker">Library Manager</span>
                                <strong>Cổng thư viện số</strong>
                            </div>
                        </div>

                        <h1>Đăng nhập vào không gian học tập của bạn</h1>
                        <p class="login-showcase-intro">
                            Truy cập kho sách, mở file tài liệu số, theo dõi phiếu mượn và thao tác mua sách
                            trong một trải nghiệm thống nhất cho học sinh.
                        </p>

                        <div class="login-pill-row">
                            <span class="login-pill">Sinh viên</span>
                            <span class="login-pill">Mượn và mua sách</span>
                            <span class="login-pill">Tài liệu số</span>
                        </div>
                    </div>

                    <div class="login-feature-list">
                        <article class="login-feature">
                            <strong>Tra cứu và mở file sách</strong>
                            <p>Xem nhanh tài liệu số đã được thư viện kích hoạt ngay từ trang chi tiết sách.</p>
                        </article>
                        <article class="login-feature">
                            <strong>Theo dõi mượn trả rõ ràng</strong>
                            <p>Kiểm tra hạn trả, yêu cầu trả sách và gia hạn online khi phiếu đủ điều kiện.</p>
                        </article>
                        <article class="login-feature">
                            <strong>Một nơi cho mọi thao tác</strong>
                            <p>Danh mục sách, hồ sơ sinh viên và trung tâm giao dịch đều dùng chung một tài khoản.</p>
                        </article>
                    </div>

                    <div class="login-stat-row" aria-label="Tóm tắt lợi ích">
                        <div class="login-stat">
                            <strong>1 cổng</strong>
                            <span>mượn, mua và lấy file sách</span>
                        </div>
                        <div class="login-stat">
                            <strong>Google</strong>
                            <span>có thể đăng nhập nhanh nếu đã liên kết</span>
                        </div>
                        <div class="login-stat">
                            <strong>Bảo mật</strong>
                            <span>có luồng quên mật khẩu và đặt lại bằng OTP</span>
                        </div>
                    </div>
                </aside>

                <div class="card-shell auth-card login-panel">
                    <div class="login-panel-copy">
                        <span class="login-panel-kicker">Sign In</span>
                        <h2 id="auth-title" class="fw-bold">Đăng nhập</h2>
                        <p class="text-muted mb-0">Dùng tên đăng nhập hoặc email đã đăng ký để tiếp tục vào hệ thống.</p>
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

                    <form class="auth-form login-form" action="${pageContext.request.contextPath}/LoginURL" method="post">
                        <div class="field">
                            <div class="field-head">
                                <label for="username">Tên đăng nhập hoặc email</label>
                                <span class="field-meta">Bắt buộc</span>
                            </div>
                            <input id="username" type="text" name="username" value="${username}" autocomplete="username" spellcheck="false" autofocus required>
                            <p class="auth-help">Bạn có thể dùng tên đăng nhập hoặc email đã liên kết với tài khoản thư viện.</p>
                        </div>

                        <div class="field">
                            <div class="field-head">
                                <label for="password">Mật khẩu</label>
                                <a href="${pageContext.request.contextPath}/forgot-password" class="login-inline-link">Quên mật khẩu?</a>
                            </div>
                            <div class="password-field">
                                <input id="password" type="password" name="password" autocomplete="current-password" required>
                                <button class="password-toggle" type="button" data-password-toggle data-target="password" aria-controls="password" aria-label="Hiện mật khẩu">Hiện</button>
                            </div>
                        </div>

                        <button class="btn-submit login-submit" type="submit">Đăng nhập hệ thống</button>
                    </form>

                    <c:if test="${googleEnabled}">
                        <div class="login-divider auth-divider"><span>hoặc</span></div>
                        <a class="btn-google login-google" href="<%= GoogleOAuthService.buildAuthorizationUrl()%>">
                            <span class="google-mark">G</span>
                            Đăng nhập bằng Google
                        </a>
                    </c:if>

                    <div class="text-center auth-foot login-foot">
                        <span class="text-muted">Chưa có tài khoản sinh viên?</span>
                        <a href="${pageContext.request.contextPath}/register" class="fw-bold text-decoration-none ms-1">Đăng ký</a>
                    </div>
                </div>
            </section>
        </main>

        <script>
            (function () {
                var toggles = document.querySelectorAll('[data-password-toggle]');
                toggles.forEach(function (button) {
                    button.addEventListener('click', function () {
                        var targetId = button.getAttribute('data-target');
                        var input = document.getElementById(targetId);
                        if (!input) {
                            return;
                        }
                        var shouldShow = input.type === 'password';
                        input.type = shouldShow ? 'text' : 'password';
                        button.textContent = shouldShow ? 'Ẩn' : 'Hiện';
                        button.setAttribute('aria-label', shouldShow ? 'Ẩn mật khẩu' : 'Hiện mật khẩu');
                    });
                });
            })();
        </script>
    </body>
</html>
