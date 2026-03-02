<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Portal | Secure Login</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap"
          rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <style>
        .container {
            font-family: 'Plus Jakarta Sans', sans-serif;
            position: relative;
            width: 100%;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            z-index: 1;
        }

        .custom-bg-layer {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(rgba(15, 23, 42, 0.5), rgba(15, 23, 42, 0.8)),
            url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?q=80&w=2000&auto=format&fit=crop');
            background-size: cover;
            background-position: center;
            z-index: -1;
        }

        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(12px);
            border-radius: 2rem;
            width: 100%;
            max-width: 640px;
            padding: 3.5rem 2.5rem;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.6);
            border: 1px solid rgba(255, 255, 255, 0.3);
        }

        .brand-header {
            text-align: center;
            margin-bottom: 2.5rem;
        }

        .brand-icon-wrapper {
            width: 64px;
            height: 64px;
            background: #0f172a;
            color: #fff;
            border-radius: 1rem;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            font-size: 1.75rem;
            margin-bottom: 1rem;
        }

        .input-group-modern {
            position: relative;
            margin-bottom: 1.25rem;
        }

        .input-group-modern i {
            position: absolute;
            left: 1.25rem;
            top: 50%;
            transform: translateY(-50%);
            color: #94a3b8;
            z-index: 5;
        }

        .form-control-modern {
            width: 100%;
            height: 56px;
            padding-left: 3.25rem;
            border-radius: 0.85rem;
            border: 1.5px solid #e2e8f0;
            background: #f8fafc;
            font-weight: 500;
            transition: all 0.2s ease;
        }

        .form-control-modern:focus {
            outline: none;
            border-color: #2563eb;
            background: #fff;
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
        }

        .btn-submit-premium {
            width: 100%;
            height: 56px;
            background: #0f172a;
            color: white;
            border: none;
            border-radius: 0.85rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            transition: 0.3s;
            margin-top: 1rem;
        }

        .btn-submit-premium:hover {
            background: #1e293b;
            transform: translateY(-2px);
        }

        .error-toast {
            background: #fff1f2;
            color: #e11d48;
            padding: 1rem;
            border-radius: 0.75rem;
            font-size: 0.9rem;
            font-weight: 600;
            margin-bottom: 2rem;
            border: 1px solid #ffe4e6;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .bottom-links {
            display: flex;
            justify-content: space-between;
            margin-top: 1.5rem;
            font-size: 14px;
        }

        .bottom-links a {
            color: #475569;
            text-decoration: none;
            font-weight: 600;
        }

        .bottom-links a:hover {
            color: #2563eb;
        }
    </style>
</head>
<body>

<div class="container">
    <div class="custom-bg-layer"></div>

    <div class="login-card">
        <div class="brand-header">
            <div class="brand-icon-wrapper">
                <i class="fa-solid fa-swatchbook"></i>
            </div>
            <h2 class="fw-bold">Đăng nhập</h2>
            <p class="text-muted small">Hệ thống thư viện thông minh</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
        <div class="error-toast">
            <i class="fa-solid fa-circle-exclamation"></i>
            <span><%= request.getAttribute("error") %></span>
        </div>
        <% } %>

        <form action="<%=request.getContextPath()%>/LoginURL" method="POST">
            <div class="mb-4">
                <label class="form-label fw-bold small text-secondary">TÀI KHOẢN</label>
                <div class="input-group-modern">
                    <input type="text" class="form-control-modern" name="username"
                           placeholder="Nhập tài khoản"
                           value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                           required>
                    <i class="fa-solid fa-user-shield"></i>
                </div>
            </div>

            <div class="mb-4">
                <label class="form-label fw-bold small text-secondary">MẬT KHẨU</label>
                <div class="input-group-modern">
                    <input type="password" class="form-control-modern" name="password"
                           placeholder="••••••••" required>
                    <i class="fa-solid fa-key"></i>
                </div>
            </div>

            <button type="submit" class="btn-submit-premium text-uppercase">
                Đăng nhập hệ thống <i class="fa-solid fa-arrow-right-long"></i>
            </button>
        </form>

        <div class="bottom-links">
            <a href="<%=request.getContextPath()%>/forgot">Quên mật khẩu?</a>
            <a href="<%=request.getContextPath()%>/home">Trang chủ</a>
        </div>

        <div class="text-center mt-5">
            <small class="text-secondary opacity-50 fw-bold">LIB-AUTH v3.0 &copy; 2026</small>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>