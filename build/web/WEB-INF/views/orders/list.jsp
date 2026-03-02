<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quan ly Orders</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f3f6fb; color: #1f2937; }
        .navbar { background: linear-gradient(135deg, #1e3c72, #2a5298); padding: 14px 28px; display: flex; align-items: center; gap: 20px; }
        .navbar h1 { color: #fff; font-size: 21px; margin-right: 18px; }
        .navbar a { color: #dbeafe; text-decoration: none; font-size: 14px; padding: 8px 12px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .nav-right { margin-left: auto; display: flex; align-items: center; gap: 12px; color: #dbeafe; }
        .container { max-width: 1260px; margin: 28px auto; padding: 0 18px; }
        .panel { background: #fff; border-radius: 14px; box-shadow: 0 3px 16px rgba(30, 60, 114, 0.1); padding: 20px; }
        h2 { color: #1e3c72; margin-bottom: 14px; }
        .msg { background: #dcfce7; color: #166534; padding: 12px 14px; border-radius: 8px; margin-bottom: 12px; }
        .error { background: #fee2e2; color: #991b1b; padding: 12px 14px; border-radius: 8px; margin-bottom: 12px; }
        table { width: 100%; border-collapse: collapse; }
        th { background: #1f4a8a; color: #fff; text-align: left; padding: 12px 14px; font-size: 13px; }
        td { padding: 12px 14px; border-bottom: 1px solid #e5e7eb; vertical-align: top; font-size: 14px; }
        tr:hover td { background: #f8fbff; }
        .status { font-weight: 700; }
        .pending { color: #d97706; }
        .approved { color: #15803d; }
        .rejected { color: #dc2626; }
        .actions { display: flex; gap: 6px; }
        .btn { border: none; cursor: pointer; border-radius: 6px; padding: 8px 12px; font-size: 13px; font-weight: 600; }
        .btn-approve { background: #16a34a; color: #fff; }
        .btn-reject { background: #dc2626; color: #fff; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Muon tra</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/bookfiles">BookFiles</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/books">Books</a>
            <a href="${pageContext.request.contextPath}/students">Students</a>
            <a href="${pageContext.request.contextPath}/authors">Authors</a>
            <a href="${pageContext.request.contextPath}/categories">Categories</a>
            <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
            <a href="${pageContext.request.contextPath}/staffs?action=list">Staffs</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quan ly order</h2>

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
                        <th>Xu ly boi</th>
                        <th>Ngay dat</th>
                        <th>Tong tien</th>
                        <th>Trang thai</th>
                        <th>Chi tiet</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>${order.orderID}</td>
                            <td>${order.studentName}</td>
                            <td>${order.staffName}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>
                                <span class="status ${order.status eq 'Pending' ? 'pending' : (order.status eq 'Approved' ? 'approved' : 'rejected')}">
                                    ${order.status}
                                </span>
                            </td>
                            <td>${empty order.items ? 'Khong co chi tiet' : order.items}</td>
                            <td>
                                <c:if test="${order.status eq 'Pending'}">
                                    <div class="actions">
                                        <form method="post" action="${pageContext.request.contextPath}/orders" style="margin:0;">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <button class="btn btn-approve" type="submit">Duyet</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/orders" style="margin:0;">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <button class="btn btn-reject" type="submit">Tu choi</button>
                                        </form>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}">
                        <tr>
                            <td colspan="8" style="text-align:center;color:#64748b;padding:22px;">Chua co don hang nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
