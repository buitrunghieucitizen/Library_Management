<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cổng sinh viên - Mượn trả sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/books">Sách</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
        <div class="nav-right">
            <span>Xin chào, ${sessionScope.staff.staffName} (Sinh viên)</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Màn hình sinh viên</h2>
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
            <h3>Bảng giá sách hiện hành</h3>
            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Tên sách</th>
                        <th>Giá</th>
                        <th>Tiền tệ</th>
                        <th>Ghi chú</th>
                        <th>Còn lại</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="price" items="${bookPrices}">
                        <tr>
                            <td>${price.bookID}</td>
                            <td>${price.bookName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${price.amount > 0}">${price.amount}</c:when>
                                    <c:otherwise>Chưa cập nhật</c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${price.currency}" default="-"/></td>
                            <td><c:out value="${price.note}" default="-"/></td>
                            <td>${price.available}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookPrices}">
                        <tr><td colspan="6" class="empty">Chưa có bảng giá sách.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h3>Danh sách sách có sẵn</h3>
            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
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
                                <div class="actions">
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="borrow">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-borrow" type="submit">Mượn</button>
                                    </form>
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="buy">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-buy" type="submit">Mua</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableBooks}">
                        <tr><td colspan="4" class="empty">Không có sách sẵn sàng để mượn hoặc mua.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <h3>Sách đang mượn của bạn</h3>
            <table>
                <thead>
                    <tr>
                        <th>Mã phiếu mượn</th>
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
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status-borrowing">Đang mượn</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status-returned">Đã trả</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status-overdue">Quá hạn</span></c:when>
                                    <c:otherwise>${b.status}</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
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


