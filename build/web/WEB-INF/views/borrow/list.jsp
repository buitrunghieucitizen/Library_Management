<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sach muon tra sach</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.2); }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; font-size: 15px; padding: 8px 16px; border-radius: 6px; transition: all 0.2s; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .container { max-width: 1200px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1e3c72; margin-bottom: 15px; }
        .btn { display: inline-block; padding: 8px 18px; border-radius: 6px; text-decoration: none; font-size: 14px; font-weight: 500; border: none; cursor: pointer; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-primary:hover { background: #1e3c72; }
        .btn-success { background: #28a745; color: #fff; }
        .btn-success:hover { background: #218838; }
        .msg { background: #d4edda; color: #155724; padding: 12px 20px; border-radius: 8px; margin-bottom: 15px; }
        .error { background: #f8d7da; color: #721c24; padding: 12px 20px; border-radius: 8px; margin-bottom: 15px; }
        table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
        th { background: linear-gradient(135deg, #1e3c72, #2a5298); color: #fff; padding: 14px 16px; text-align: left; font-size: 14px; }
        td { padding: 12px 16px; border-bottom: 1px solid #eee; font-size: 14px; color: #333; vertical-align: top; }
        tr:hover td { background: #f8f9ff; }
        .status { font-weight: 600; }
        .status.borrowing { color: #0c63e4; }
        .status.returned { color: #198754; }
        .status.overdue { color: #dc3545; }
        .actions { display: flex; gap: 8px; }
        .inline-form { margin: 0; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>📚 Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/students">Students</a>
        <a href="${pageContext.request.contextPath}/borrows">Borrow</a>
        <a href="${pageContext.request.contextPath}/authors">Authors</a>
        <a href="${pageContext.request.contextPath}/categories">Categories</a>
        <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
    </div>

    <div class="container">
        <h2>Danh sach muon/tra sach</h2>
        <a class="btn btn-primary" href="${pageContext.request.contextPath}/borrows?action=create" style="margin-bottom:15px;">+ Tao phieu muon</a>

        <c:if test="${not empty param.msg}">
            <div class="msg">${param.msg}</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="error">${param.error}</div>
        </c:if>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Sinh vien</th>
                    <th>Nhan vien</th>
                    <th>Ngay muon</th>
                    <th>Han tra</th>
                    <th>Ngay tra</th>
                    <th>Trang thai</th>
                    <th>Sach</th>
                    <th>Hanh dong</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="b" items="${borrows}">
                    <tr>
                        <td>${b.borrowID}</td>
                        <td>${b.studentName}</td>
                        <td>${b.staffName}</td>
                        <td>${b.borrowDate}</td>
                        <td>${b.dueDate}</td>
                        <td>
                            <c:choose>
                                <c:when test="${empty b.returnDate}">-</c:when>
                                <c:otherwise>${b.returnDate}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${b.status eq 'Borrowing'}"><span class="status borrowing">${b.status}</span></c:when>
                                <c:when test="${b.status eq 'Returned'}"><span class="status returned">${b.status}</span></c:when>
                                <c:when test="${b.status eq 'Overdue'}"><span class="status overdue">${b.status}</span></c:when>
                                <c:otherwise><span class="status">${b.status}</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>${b.items}</td>
                        <td class="actions">
                            <c:if test="${b.status ne 'Returned'}">
                                <form class="inline-form" method="POST" action="${pageContext.request.contextPath}/borrows" onsubmit="return confirm('Xac nhan tra sach cho phieu nay?');">
                                    <input type="hidden" name="action" value="return">
                                    <input type="hidden" name="borrowID" value="${b.borrowID}">
                                    <button class="btn btn-success" type="submit">Tra sach</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty borrows}">
                    <tr>
                        <td colspan="9" style="text-align:center;color:#999;padding:30px;">Chua co phieu muon nao.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>
