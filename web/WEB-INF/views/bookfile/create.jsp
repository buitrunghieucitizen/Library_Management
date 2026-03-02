<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Them BookFile</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f4fa; }
        .container { max-width: 720px; margin: 36px auto; padding: 0 16px; }
        .card { background: #fff; border-radius: 14px; box-shadow: 0 3px 16px rgba(30, 60, 114, 0.1); padding: 24px; }
        h2 { color: #1e3c72; margin-bottom: 18px; }
        label { display: block; font-weight: 600; margin-bottom: 6px; margin-top: 14px; }
        input, select { width: 100%; padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; }
        .actions { display: flex; gap: 8px; margin-top: 20px; }
        .btn { text-decoration: none; border: none; cursor: pointer; border-radius: 8px; padding: 10px 16px; font-weight: 600; }
        .btn-primary { background: #2563eb; color: #fff; }
        .btn-secondary { background: #6b7280; color: #fff; }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <h2>Them bookfile</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/bookfiles?action=create">
                <label>Sach</label>
                <select name="bookID" required>
                    <c:forEach var="book" items="${books}">
                        <option value="${book.bookID}">${book.bookID} - ${book.bookName}</option>
                    </c:forEach>
                </select>

                <label>Ten file</label>
                <input type="text" name="fileName" required>

                <label>File URL</label>
                <input type="text" name="fileUrl" required>

                <label>File type</label>
                <input type="text" name="fileType">

                <label>File size</label>
                <input type="number" name="fileSize" min="0" value="0">

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Luu</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/bookfiles">Huy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
