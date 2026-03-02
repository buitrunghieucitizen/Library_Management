<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quan ly BookFile</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f3f6fb; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 14px 28px; display: flex; align-items: center; gap: 20px; }
        .navbar h1 { color: #fff; font-size: 21px; margin-right: 18px; }
        .navbar a { color: #dbeafe; text-decoration: none; font-size: 14px; padding: 8px 12px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .nav-right { margin-left: auto; display: flex; align-items: center; gap: 12px; color: #dbeafe; }
        .container { max-width: 1280px; margin: 28px auto; padding: 0 18px; }
        .panel { background: #fff; border-radius: 14px; box-shadow: 0 3px 16px rgba(30, 60, 114, 0.1); padding: 20px; }
        h2 { color: #1e3c72; margin-bottom: 14px; }
        .msg { background: #dcfce7; color: #166534; padding: 12px 14px; border-radius: 8px; margin-bottom: 12px; }
        .error { background: #fee2e2; color: #991b1b; padding: 12px 14px; border-radius: 8px; margin-bottom: 12px; }
        .btn { display: inline-block; text-decoration: none; border: none; cursor: pointer; border-radius: 6px; padding: 8px 12px; font-size: 13px; font-weight: 600; }
        .btn-primary { background: #2563eb; color: #fff; }
        .btn-warning { background: #f59e0b; color: #fff; }
        .btn-danger { background: #dc2626; color: #fff; }
        table { width: 100%; border-collapse: collapse; margin-top: 14px; }
        th { background: #1f4a8a; color: #fff; text-align: left; padding: 12px 14px; font-size: 13px; }
        td { padding: 12px 14px; border-bottom: 1px solid #e5e7eb; vertical-align: top; font-size: 14px; }
        tr:hover td { background: #f8fbff; }
        .actions { display: flex; gap: 6px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Muon tra</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">BookFiles</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Staffs</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quan ly bookfile</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/bookfiles?action=create">Them bookfile</a>

            <c:if test="${not empty param.msg}">
                <div class="msg" style="margin-top:12px;">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error" style="margin-top:12px;">${param.error}</div>
            </c:if>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Book</th>
                        <th>Staff</th>
                        <th>File</th>
                        <th>URL</th>
                        <th>Type</th>
                        <th>Size</th>
                        <th>Upload</th>
                        <th>Active</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${bookFiles}">
                        <tr>
                            <td>${row.bookFileID}</td>
                            <td>${row.bookName}</td>
                            <td>${row.staffName}</td>
                            <td>${row.fileName}</td>
                            <td><a href="${row.fileUrl}" target="_blank">${row.fileUrl}</a></td>
                            <td>${row.fileType}</td>
                            <td>${row.fileSize}</td>
                            <td>${row.uploadAt}</td>
                            <td>${row.active ? 'Yes' : 'No'}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/bookfiles?action=edit&id=${row.bookFileID}">Sua</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/bookfiles?action=delete&id=${row.bookFileID}" onclick="return confirm('Xoa bookfile nay?')">Xoa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookFiles}">
                        <tr>
                            <td colspan="10" style="text-align:center;color:#64748b;padding:22px;">Chua co bookfile nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
