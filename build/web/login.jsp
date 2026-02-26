<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập - Library Manager</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; display: flex; align-items: center; justify-content: center; height: 100vh; }
        .login-card { background: #fff; padding: 40px; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); width: 100%; max-width: 400px; text-align: center; }
        h1 { color: #1e3c72; margin-bottom: 30px; font-size: 24px; }
        .input-group { margin-bottom: 20px; text-align: left; }
        label { display: block; font-weight: 600; color: #555; margin-bottom: 8px; font-size: 14px; }
        input[type="text"], input[type="password"] { width: 100%; padding: 12px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; transition: border 0.2s; }
        input:focus { outline: none; border-color: #2a5298; }
        .btn { width: 100%; padding: 12px; border-radius: 8px; font-size: 16px; font-weight: 600; cursor: pointer; border: none; background: linear-gradient(135deg, #1e3c72, #2a5298); color: #fff; transition: transform 0.1s; margin-top: 10px; }
        .btn:hover { background: linear-gradient(135deg, #162b54, #1e3c72); }
        .btn:active { transform: scale(0.98); }
        .error { color: #d9534f; background: #fdf0f0; padding: 10px; border-radius: 6px; margin-bottom: 20px; font-size: 14px; }
    </style>
</head>
<body>
    <div class="login-card">
        <h1>📚 Library Login</h1>
        
        <% if(request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <form action="<%=request.getContextPath()%>/LoginURL" method="POST">
            <div class="input-group">
                <label>Tên đăng nhập</label>
                <input type="text" name="username" value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>" required>
            </div>
            <div class="input-group">
                <label>Mật khẩu</label>
                <input type="password" name="password" required>
            </div>
            <button class="btn" type="submit">Đăng nhập</button>
        </form>
    </div>
</body>
</html>
