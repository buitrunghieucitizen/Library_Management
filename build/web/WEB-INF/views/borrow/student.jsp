<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Man hinh hoc sinh - Muon tra sach</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f3f6fb; color: #1e293b; }
        .navbar { background: linear-gradient(135deg, #0f4c81, #1f6aa5); padding: 14px 28px; display: flex; align-items: center; gap: 20px; }
        .navbar h1 { color: #fff; font-size: 20px; margin-right: 20px; }
        .navbar a { color: #dbeafe; text-decoration: none; font-size: 14px; padding: 8px 12px; border-radius: 6px; }
        .navbar a:hover { background: rgba(255,255,255,0.15); color: #fff; }
        .nav-right { margin-left: auto; color: #dbeafe; font-size: 14px; display: flex; align-items: center; gap: 12px; }
        .container { max-width: 1100px; margin: 28px auto; padding: 0 16px; }
        .card { background: #fff; border-radius: 12px; box-shadow: 0 3px 14px rgba(15, 76, 129, 0.12); padding: 18px; margin-bottom: 18px; }
        h2 { color: #0f4c81; margin-bottom: 12px; font-size: 20px; }
        h3 { color: #0f4c81; margin-bottom: 10px; font-size: 17px; }
        .msg { background: #d1fae5; color: #065f46; padding: 10px 14px; border-radius: 8px; margin-bottom: 12px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 8px; margin-bottom: 12px; }
        table { width: 100%; border-collapse: collapse; border-radius: 10px; overflow: hidden; border: 1px solid #e2e8f0; }
        th { background: #e9f2fa; color: #0f4c81; text-align: left; padding: 11px 12px; font-size: 13px; }
        td { padding: 10px 12px; border-top: 1px solid #eef2f7; font-size: 14px; vertical-align: top; }
        .btn { border: none; cursor: pointer; border-radius: 6px; padding: 7px 12px; font-size: 13px; font-weight: 600; }
        .btn-borrow { background: #2563eb; color: #fff; }
        .btn-borrow:hover { background: #1d4ed8; }
        .btn-return { background: #16a34a; color: #fff; }
        .btn-return:hover { background: #15803d; }
        .status-borrowing { color: #1d4ed8; font-weight: 600; }
        .status-returned { color: #15803d; font-weight: 600; }
        .status-overdue { color: #dc2626; font-weight: 600; }
        .empty { text-align: center; color: #64748b; padding: 18px 8px; }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Library Manager</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn & Mua sách</a>
        <div class="nav-right">
            <span>Xin chào, ${sessionScope.staff.staffName} (Học sinh)</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Màn hình Học sinh</h2>
            <c:if test="${not empty mappingError}">
                <div class="error">${mappingError}</div>
            </c:if>
            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
            <p>Mã sinh viên: <strong><c:out value="${studentId}" default="-"/></strong></p>
        </div>

        <div class="card">
            <h3>Danh sách sách có sẵn</h3>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên sách</th>
                        <th>Còn lại</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="book" items="${availableBooks}">
                        <tr>
                            <td>${book.bookID}</td>
                            <td>${book.bookName}</td>
                            <td>${book.available}</td>
                            <td>
                                <div style="display:flex; gap:5px;">
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" style="margin:0;">
                                        <input type="hidden" name="action" value="borrow">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-borrow" type="submit">Mượn</button>
                                    </form>
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" style="margin:0;">
                                        <input type="hidden" name="action" value="buy">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-return" style="background:#f59e0b;" type="submit">Mua</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableBooks}">
                        <tr><td colspan="4" class="empty">Không có sách sẵn sàng để mượn/mua.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h3>Sách đang mượn của bạn</h3>
            <table>
                <thead>
                    <tr>
                        <th>Borrow ID</th>
                        <th>Ngày mượn</th>
                        <th>Hạn trả</th>
                        <th>Ngày trả</th>
                        <th>Trạng thái</th>
                        <th>Sách</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${borrows}">
                        <tr>
                            <td>${b.borrowID}</td>
                            <td>${b.borrowDate}</td>
                            <td>${b.dueDate}</td>
                            <td><c:out value="${b.returnDate}" default="-"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status-borrowing">${b.status}</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status-returned">${b.status}</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status-overdue">${b.status}</span></c:when>
                                    <c:otherwise>${b.status}</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" style="margin:0;">
                                        <input type="hidden" name="action" value="requestReturn">
                                        <input type="hidden" name="borrowID" value="${b.borrowID}">
                                        <button class="btn btn-return" type="submit">Trả sách</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty borrows}">
                        <tr><td colspan="7" class="empty">Bạn chưa có phiếu mượn nào.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
