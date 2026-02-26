<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><title>Sửa Thể loại</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 30px; }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; padding: 8px 16px; border-radius: 6px; }
        .container { max-width: 600px; margin: 30px auto; padding: 0 20px; }
        .card { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
        h2 { color: #1e3c72; margin-bottom: 20px; }
        label { display: block; font-weight: 600; color: #333; margin: 14px 0 6px; }
        input { width: 100%; padding: 10px 14px; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 14px; }
        input:focus { outline: none; border-color: #2a5298; }
        .btn { padding: 10px 24px; border-radius: 8px; font-size: 14px; font-weight: 600; border: none; cursor: pointer; margin-top: 20px; text-decoration: none; display: inline-block; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-secondary { background: #6c757d; color: #fff; margin-left: 8px; }
    </style>
</head>
<body>
    <div class="navbar"><h1>📚 Library Manager</h1><a href="${pageContext.request.contextPath}/">Trang chủ</a><a href="${pageContext.request.contextPath}/categories">Categories</a></div>
    <div class="container"><div class="card">
        <h2>✏️ Sửa Thể loại</h2>
        <form method="POST" action="${pageContext.request.contextPath}/categories?action=edit">
            <input type="hidden" name="categoryID" value="${category.categoryID}">
            <label>Tên thể loại</label><input type="text" name="categoryName" value="${category.categoryName}" required>
            <button class="btn btn-primary" type="submit">Cập nhật</button>
            <a class="btn btn-secondary" href="${pageContext.request.contextPath}/categories">Hủy</a>
        </form>
    </div></div>
</body>
</html>
