<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Sua Staff</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f4fa; }
        .container { max-width: 760px; margin: 36px auto; padding: 0 16px; }
        .card { background: #fff; border-radius: 14px; box-shadow: 0 3px 16px rgba(30, 60, 114, 0.1); padding: 24px; }
        h2 { color: #1e3c72; margin-bottom: 18px; }
        .error { background: #fee2e2; color: #991b1b; padding: 12px 14px; border-radius: 8px; margin-bottom: 12px; }
        label { display: block; font-weight: 600; margin-bottom: 6px; margin-top: 14px; }
        input { width: 100%; padding: 10px 12px; border: 1px solid #d1d5db; border-radius: 8px; font-size: 14px; }
        .roles { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 10px; margin-top: 10px; }
        .role-item { display: flex; align-items: center; gap: 8px; background: #f8fafc; padding: 10px 12px; border-radius: 8px; }
        .role-item input { width: auto; }
        .actions { display: flex; gap: 8px; margin-top: 20px; }
        .btn { text-decoration: none; border: none; cursor: pointer; border-radius: 8px; padding: 10px 16px; font-weight: 600; }
        .btn-primary { background: #2563eb; color: #fff; }
        .btn-secondary { background: #6b7280; color: #fff; }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <h2>Sua staff</h2>

            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/staffs?action=edit">
                <input type="hidden" name="staffID" value="${staff.staffID}">

                <label>Ten staff</label>
                <input type="text" name="staffName" value="${staff.staffName}" required>

                <label>Username</label>
                <input type="text" name="username" value="${staff.username}" required>

                <label>Password</label>
                <input type="text" name="password" value="${staff.password}" required>

                <label>Roles</label>
                <div class="roles">
                    <c:forEach var="role" items="${roles}">
                        <label class="role-item">
                            <input type="checkbox" name="roleIDs" value="${role.roleID}" ${selectedRoleFlags[role.roleID] ? 'checked' : ''}>
                            <span>${role.roleName}</span>
                        </label>
                    </c:forEach>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cap nhat</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/staffs?action=list">Huy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
