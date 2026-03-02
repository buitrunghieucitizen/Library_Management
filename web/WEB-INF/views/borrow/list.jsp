<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quan ly Muon Tra</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 15px 30px; display: flex; align-items: center; gap: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.2); }
        .navbar h1 { color: #fff; font-size: 22px; }
        .navbar a { color: #dce6f5; text-decoration: none; font-size: 15px; padding: 8px 14px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .nav-right { margin-left: auto; display: flex; align-items: center; gap: 16px; color: #dce6f5; font-size: 14px; }
        .container { max-width: 1240px; margin: 30px auto; padding: 0 20px; }
        .panel { background: #fff; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.08); padding: 18px; }
        h2 { color: #1e3c72; margin-bottom: 15px; }
        .btn { display: inline-block; padding: 8px 14px; border-radius: 6px; text-decoration: none; font-size: 13px; font-weight: 600; border: none; cursor: pointer; }
        .btn-primary { background: #2a5298; color: #fff; }
        .btn-success { background: #16a34a; color: #fff; }
        .msg { background: #d4edda; color: #155724; padding: 12px 16px; border-radius: 8px; margin-bottom: 12px; }
        .error { background: #f8d7da; color: #721c24; padding: 12px 16px; border-radius: 8px; margin-bottom: 12px; }
        table { width: 100%; border-collapse: collapse; background: #fff; border-radius: 10px; overflow: hidden; }
        th { background: #1f4a8a; color: #fff; padding: 12px 14px; text-align: left; font-size: 13px; }
        td { padding: 11px 14px; border-bottom: 1px solid #edf2f7; font-size: 14px; color: #1f2937; vertical-align: top; }
        tr:hover td { background: #f8fbff; }
        .status { font-weight: 600; }
        .status.borrowing { color: #1d4ed8; }
        .status.returned { color: #15803d; }
        .status.overdue { color: #dc2626; }
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
        <a href="${pageContext.request.contextPath}/admin/books">Books</a>
        <a href="${pageContext.request.contextPath}/admin/students">Students</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Borrow</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">BookFiles</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/authors">Authors</a>
            <a href="${pageContext.request.contextPath}/admin/categories">Categories</a>
            <a href="${pageContext.request.contextPath}/admin/publishers">Publishers</a>
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Staffs</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quan ly muon tra sach</h2>

            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/borrows?action=create" style="margin-bottom:12px;">Tao phieu muon</a>

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
                            <td><c:out value="${b.returnDate}" default="-"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status borrowing">${b.status}</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status returned">${b.status}</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status overdue">${b.status}</span></c:when>
                                    <c:otherwise><span class="status">${b.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" style="margin:0;" onsubmit="return confirm('Xac nhan tra sach cho phieu nay?');">
                                        <input type="hidden" name="action" value="return">
                                        <input type="hidden" name="borrowID" value="${b.borrowID}">
                                        <button class="btn btn-success" type="submit">Xac nhan tra</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty borrows}">
                        <tr>
                            <td colspan="9" style="text-align:center;color:#64748b;padding:26px;">Chua co phieu muon nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
