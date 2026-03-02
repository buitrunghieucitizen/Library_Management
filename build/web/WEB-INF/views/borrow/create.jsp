<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Tao phieu muon sach</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.2); }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; font-size: 15px; padding: 8px 16px; border-radius: 6px; transition: all 0.2s; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .container { max-width: 700px; margin: 30px auto; background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); padding: 25px; }
        h2 { color: #1e3c72; margin-bottom: 18px; }
        label { display: block; margin-bottom: 6px; font-weight: 600; color: #444; }
        select, input[type="number"], input[type="date"] { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; margin-bottom: 15px; font-size: 14px; }
        .btn { display: inline-block; padding: 10px 20px; border-radius: 6px; text-decoration: none; font-size: 14px; font-weight: 500; border: none; cursor: pointer; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-primary:hover { background: #1e3c72; }
        .btn-secondary { background: #6c757d; color: #fff; }
        .btn-secondary:hover { background: #5a6268; }
        .actions { margin-top: 10px; display: flex; gap: 8px; }
        .error { background: #f8d7da; color: #721c24; padding: 12px 16px; border-radius: 8px; margin-bottom: 15px; }
        .note { color: #666; font-size: 13px; margin-top: -8px; margin-bottom: 14px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>📚 Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/borrows">Borrow</a>
    </div>

    <div class="container">
        <h2>Tao phieu muon sach</h2>

        <c:if test="${not empty error}">
            <div class="error">${error}</div>
        </c:if>

        <form method="POST" action="${pageContext.request.contextPath}/borrows">
            <input type="hidden" name="action" value="create">

            <label>Sinh vien</label>
            <select name="studentID" required>
                <option value="">-- Chon sinh vien --</option>
                <c:forEach var="s" items="${students}">
                    <option value="${s.studentID}" ${selectedStudentId == s.studentID ? 'selected' : ''}>
                        ${s.studentID} - ${s.studentName}
                    </option>
                </c:forEach>
            </select>

            <label>Sach</label>
            <select name="bookID" required>
                <option value="">-- Chon sach --</option>
                <c:forEach var="b" items="${books}">
                    <option value="${b.bookID}" ${selectedBookId == b.bookID ? 'selected' : ''}>
                        ${b.bookID} - ${b.bookName} (con ${b.available})
                    </option>
                </c:forEach>
            </select>
            <p class="note">Chi hien thi sach con trong kho (Available > 0).</p>

            <label>So luong muon</label>
            <input type="number" name="quantity" min="1" value="${empty quantity ? 1 : quantity}" required>

            <label>Han tra</label>
            <input type="date" name="dueDate" value="${dueDate}" required>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Tao phieu</button>
                <a class="btn btn-secondary" href="${pageContext.request.contextPath}/borrows?action=list">Huy</a>
            </div>
        </form>
    </div>
</body>
</html>
