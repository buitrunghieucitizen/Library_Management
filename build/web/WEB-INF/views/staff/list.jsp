<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quan ly Staff</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f3f6fb; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 14px 28px; display: flex; align-items: center; gap: 20px; }
        .navbar h1 { color: #fff; font-size: 21px; margin-right: 18px; }
        .navbar a { color: #dbeafe; text-decoration: none; font-size: 14px; padding: 8px 12px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .nav-right { margin-left: auto; display: flex; align-items: center; gap: 12px; color: #dbeafe; }
        .container { max-width: 1180px; margin: 28px auto; padding: 0 18px; }
        .panel { background: #fff; border-radius: 14px; box-shadow: 0 3px 16px rgba(30, 60, 114, 0.1); padding: 20px; }
        h2 { color: #1e3c72; margin-bottom: 14px; }
        .btn { display: inline-block; text-decoration: none; border: none; cursor: pointer; border-radius: 6px; padding: 8px 12px; font-size: 13px; font-weight: 600; }
        .btn-primary { background: #2563eb; color: #fff; }
        .btn-warning { background: #f59e0b; color: #fff; }
        .btn-danger { background: #dc2626; color: #fff; }
        .msg { background: #dcfce7; color: #166534; padding: 12px 14px; border-radius: 8px; margin-top: 12px; margin-bottom: 12px; }
        .error { background: #fee2e2; color: #991b1b; padding: 12px 14px; border-radius: 8px; margin-top: 12px; margin-bottom: 12px; }
        table { width: 100%; border-collapse: collapse; margin-top: 14px; }
        th { background: #1f4a8a; color: #fff; text-align: left; padding: 12px 14px; font-size: 13px; }
        td { padding: 12px 14px; border-bottom: 1px solid #e5e7eb; font-size: 14px; }
        tr:hover td { background: #f8fbff; }
        .actions { display: flex; gap: 6px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Muon tra</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/bookfiles">BookFiles</a>
        <a href="${pageContext.request.contextPath}/staffs?action=list">Staffs</a>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quan ly staff</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/staffs?action=create">Them staff</a>

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
                        <th>Ten</th>
                        <th>Username</th>
                        <th>Password</th>
                        <th>Roles</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${staffRows}">
                        <tr>
                            <td>${row.staff.staffID}</td>
                            <td>${row.staff.staffName}</td>
                            <td>${row.staff.username}</td>
                            <td>${row.staff.password}</td>
                            <td>${row.roleNames}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/staffs?action=edit&id=${row.staff.staffID}">Sua</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/staffs?action=delete&id=${row.staff.staffID}" onclick="return confirm('Xoa staff nay?')">Xoa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty staffRows}">
                        <tr>
                            <td colspan="6" style="text-align:center;color:#64748b;padding:22px;">Chua co tai khoan staff nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
