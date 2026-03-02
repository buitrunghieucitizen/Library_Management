<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sach Sinh vien</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.2); }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; font-size: 15px; padding: 8px 14px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .container { max-width: 1000px; margin: 30px auto; padding: 0 20px; }
        h2 { color: #1e3c72; margin-bottom: 15px; }
        .btn { display: inline-block; padding: 8px 18px; border-radius: 6px; text-decoration: none; font-size: 14px; font-weight: 500; border: none; cursor: pointer; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-warning { background: #f0ad4e; color: #fff; }
        .btn-danger { background: #d9534f; color: #fff; }
        table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 12px rgba(0,0,0,0.08); }
        th { background: linear-gradient(135deg, #1e3c72, #2a5298); color: #fff; padding: 14px 16px; text-align: left; font-size: 14px; }
        td { padding: 12px 16px; border-bottom: 1px solid #eee; font-size: 14px; color: #333; }
        tr:hover td { background: #f8f9ff; }
        .actions { display: flex; gap: 6px; }
    </style>
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/books">Books</a>
        <a href="${pageContext.request.contextPath}/students">Students</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Borrow</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/bookfiles">BookFiles</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/authors">Authors</a>
            <a href="${pageContext.request.contextPath}/categories">Categories</a>
            <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
            <a href="${pageContext.request.contextPath}/staffs?action=list">Staffs</a>
        </c:if>
    </div>

    <div class="container">
        <h2>Danh sach Sinh vien</h2>
        <c:if test="${isAdmin}">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/students?action=create" style="margin-bottom:15px;display:inline-block;">+ Them sinh vien</a>
        </c:if>
        <table>
            <thead><tr><th>ID</th><th>Ten</th><th>Email</th><th>SDT</th><th>Hanh dong</th></tr></thead>
            <tbody>
                <c:forEach var="s" items="${students}">
                    <tr>
                        <td>${s.studentID}</td>
                        <td>${s.studentName}</td>
                        <td>${s.email}</td>
                        <td>${s.phone}</td>
                        <td class="actions">
                            <c:if test="${isAdmin}">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/students?action=edit&id=${s.studentID}">Sua</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/students?action=delete&id=${s.studentID}" onclick="return confirm('Xoa?')">Xoa</a>
                            </c:if>
                            <c:if test="${not isAdmin}">
                                <span style="color:#64748b;">Chi xem</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty students}">
                    <tr><td colspan="5" style="text-align:center;color:#999;padding:30px;">Chua co sinh vien.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</body>
</html>
