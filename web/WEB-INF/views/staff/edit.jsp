<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sửa nhân viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="container">
        <div class="card">
            <h2>Sửa nhân viên</h2>

            <c:if test="${not empty error}">
                <div class="error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/staffs?action=edit">
                <input type="hidden" name="staffID" value="${staff.staffID}">

                <label>Tên nhân viên</label>
                <input type="text" name="staffName" value="${staff.staffName}" required>

                <label>Tên đăng nhập</label>
                <input type="text" name="username" value="${staff.username}" required>

                <label>Mật khẩu</label>
                <input type="text" name="password" value="${staff.password}" required>

                <label>Vai trò</label>
                <div class="roles">
                    <c:forEach var="role" items="${roles}">
                        <label class="role-item">
                            <input type="checkbox" name="roleIDs" value="${role.roleID}" ${selectedRoleFlags[role.roleID] ? 'checked' : ''}>
                            <span>${role.roleName}</span>
                        </label>
                    </c:forEach>
                </div>

                <div class="actions">
                    <button class="btn btn-primary" type="submit">Cập nhật</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/admin/staffs?action=list">Hủy</a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>


