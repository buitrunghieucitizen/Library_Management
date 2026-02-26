<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"><title>Danh sách Tác giả</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 30px; }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; padding: 8px 16px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .container { max-width: 900px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1e3c72; margin-bottom: 15px; }
        .btn { display: inline-block; padding: 8px 18px; border-radius: 6px; text-decoration: none; font-size: 14px; font-weight: 500; border: none; cursor: pointer; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-warning { background: #f0ad4e; color: #fff; }
        .btn-danger { background: #d9534f; color: #fff; }
        table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
        th { background: linear-gradient(135deg, #1e3c72, #2a5298); color: #fff; padding: 14px 16px; text-align: left; }
        td { padding: 12px 16px; border-bottom: 1px solid #eee; }
        tr:hover td { background: #f8f9ff; }
        .actions { display: flex; gap: 6px; }
    </style>
</head>
<body>
    <div class="navbar"><h1>📚 Library Manager</h1>
        <a href="${pageContext.request.contextPath}/">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/students">Students</a>
        <a href="${pageContext.request.contextPath}/authors">Authors</a>
        <a href="${pageContext.request.contextPath}/categories">Categories</a>
        <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
    </div>
    <div class="container">
        <h2>✍️ Danh sách Tác giả</h2>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/authors?action=create" style="margin-bottom:15px;display:inline-block;">+ Thêm tác giả</a>
        <table>
            <thead><tr><th>ID</th><th>Tên tác giả</th><th>Hành động</th></tr></thead>
            <tbody>
                <c:forEach var="a" items="${authors}">
                    <tr>
                        <td>${a.authorID}</td><td>${a.authorName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/authors?action=edit&id=${a.authorID}">Sửa</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/authors?action=delete&id=${a.authorID}" onclick="return confirm('Xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty authors}"><tr><td colspan="3" style="text-align:center;color:#999;padding:30px;">Chưa có tác giả.</td></tr></c:if>
            </tbody>
        </table>
    </div>
</body>
</html>
