<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Library Portal | Dang ky</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            min-height: 100vh;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(rgba(15, 23, 42, 0.68), rgba(15, 23, 42, 0.88)),
                url('https://images.unsplash.com/photo-1507842217343-583bb7270b66?q=80&w=2000&auto=format&fit=crop') center/cover;
            padding: 24px;
        }
        .card-shell {
            width: 100%;
            max-width: 640px;
            background: rgba(255, 255, 255, 0.95);
            border-radius: 28px;
            padding: 40px 34px;
            box-shadow: 0 28px 60px rgba(15, 23, 42, 0.36);
        }
        .field {
            margin-bottom: 18px;
        }
        .field label {
            display: block;
            font-size: 12px;
            font-weight: 700;
            color: #475569;
            margin-bottom: 8px;
            text-transform: uppercase;
            letter-spacing: 0.08em;
        }
        .field input {
            width: 100%;
            height: 54px;
            border-radius: 14px;
            border: 1px solid #dbe4f0;
            background: #f8fafc;
            padding: 0 18px;
            font-size: 15px;
        }
        .field input:focus {
            outline: none;
            border-color: #2563eb;
            box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.12);
            background: #fff;
        }
        .brand {
            width: 64px;
            height: 64px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 18px;
            background: #0f172a;
            color: #fff;
            font-size: 28px;
            margin-bottom: 14px;
        }
        .btn-submit {
            width: 100%;
            height: 56px;
            border: none;
            border-radius: 14px;
            background: #0f172a;
            color: #fff;
            font-weight: 700;
            margin-top: 8px;
        }
        .btn-submit:hover {
            background: #1e293b;
        }
        .error-box {
            background: #fff1f2;
            border: 1px solid #fecdd3;
            color: #be123c;
            border-radius: 14px;
            padding: 12px 14px;
            margin-bottom: 18px;
            font-weight: 600;
        }
    </style>
</head>
<body>
    <div class="card-shell">
        <div class="text-center mb-4">
            <div class="brand">+</div>
            <h1 class="h3 fw-bold mb-2">Dang ky tai khoan</h1>
            <p class="text-muted mb-0">Tao tai khoan student de truy cap student portal.</p>
        </div>

        <% if (request.getAttribute("error") != null) { %>
            <div class="error-box"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="<%=request.getContextPath()%>/register" method="post">
            <div class="field">
                <label>Ho va ten</label>
                <input type="text" name="name"
                       value="<%= request.getAttribute("name") != null ? request.getAttribute("name") : "" %>"
                       required>
            </div>
            <div class="field">
                <label>Ten dang nhap</label>
                <input type="text" name="username"
                       value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                       required>
            </div>
            <div class="field">
                <label>Mat khau</label>
                <input type="password" name="password" required>
            </div>
            <div class="field">
                <label>Xac nhan mat khau</label>
                <input type="password" name="confirm" required>
            </div>
            <button class="btn-submit" type="submit">Tao tai khoan</button>
        </form>

        <div class="text-center mt-4">
            <span class="text-muted">Da co tai khoan?</span>
            <a href="<%=request.getContextPath()%>/LoginURL" class="fw-bold text-decoration-none ms-1">Dang nhap</a>
        </div>
    </div>
</body>
</html>
