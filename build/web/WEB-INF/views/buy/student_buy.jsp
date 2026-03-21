<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<<<<<<< HEAD
    <head>
        <meta charset="UTF-8">
        <title>Cổng sinh viên - Mua sách</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
        <style>
            .grid-container {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 20px;
            }
            .full-width {
                grid-column: 1 / -1;
            }
        </style>
    </head>
    <body>
        <div class="navbar">
            <h1>Quản lý thư viện</h1>
            <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
            <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn sách</a>
            <a href="${pageContext.request.contextPath}/buy" style="font-weight: bold;">Mua sách</a>
            <div class="nav-right">
                <span>Xin chào, ${studentName}</span>
                <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>

        <div class="container grid-container">
            <div class="full-width">
                <c:if test="${not empty param.msg}"><div class="msg">${param.msg}</div></c:if>
                <c:if test="${not empty param.error}"><div class="error">${param.error}</div></c:if>
                </div>

                <div class="card full-width">
                    <h2>Thông tin sinh viên mua hàng</h2>
                    <p>Họ và tên: <strong>${studentName}</strong></p>
                <p>Mã sinh viên: <strong>${studentId}</strong></p>
            </div>

            <div class="card full-width">
                <h3>Danh sách đầu sách</h3>
                <table>
                    <thead>
                        <tr>
                            <th>Tên sách</th>
                            <th>Số lượng còn</th>
                            <th>Giá bán</th>
                            <th>Số lượng mua</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="price" items="${bookPrices}">
                            <c:if test="${price.amount > 0}"> 
                                <tr>
                                    <td>${price.bookName}</td>
                                    <td>${price.available}</td>
                                    <td>${price.amount}</td>
                                    <td>
                                        <form method="POST" action="${pageContext.request.contextPath}/buy">
                                            <input type="hidden" name="action" value="addToWaitlist">

                                            <input type="hidden" name="bookID" value="${price.bookID}">
                                            <input type="hidden" name="bookName" value="${price.bookName}">
                                            <input type="hidden" name="price" value="${price.amount}">
                                            <input type="number" name="quantity" value="1" min="1" max="${price.available > 0 ? price.available : 99}" style="width: 60px;">
                                            </td>
                                            <td>
                                                <button class="btn btn-buy" type="submit">Thêm vào chờ</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
=======
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên - Mua sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body class="student-body">
    <%@ include file="../client/_header.jsp" %>

    <c:url var="borrowListUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>
    <c:url var="buyUrl" value="/buy" />

    <div class="layout student-layout layout-two-column">
        <%@ include file="../client/_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero">
                <div>
                    <span class="page-hero-kicker">Student Purchase Flow</span>
                    <h1>Mua sách</h1>
                    <p>Tạo danh sách chờ, chọn các sách cần checkout và theo dõi lịch sử đơn mua trong cùng giao diện.</p>
                </div>
                <div class="page-hero-actions">
                    <a class="hero-action primary" href="${borrowListUrl}">Về trung tâm mượn trả</a>
                    <a class="hero-action secondary" href="${buyUrl}">Làm mới trang mua</a>
                </div>
            </section>
>>>>>>> origin/master

            <section class="student-kpi-grid">
                <article class="student-kpi-card">
                    <span>Sinh viên</span>
                    <strong><c:out value="${studentName}" default="-" /></strong>
                    <p>Mã sinh viên: <strong>#${studentId}</strong></p>
                </article>
                <article class="student-kpi-card">
                    <span>Sách có thể mua</span>
                    <strong>${fn:length(bookPrices)}</strong>
                    <p>Các đầu sách có giá bán trong bảng giá hiện tại.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Danh sách chờ</span>
                    <strong>${fn:length(waitlistItems)}</strong>
                    <p>Các sách bạn đang chuẩn bị checkout.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Lịch sử đơn</span>
                    <strong>${fn:length(orderHistory)}</strong>
                    <p>Các đơn đã gửi trước đó.</p>
                </article>
            </section>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>

<<<<<<< HEAD
                <form method="POST" action="${pageContext.request.contextPath}/buy">
                    <input type="hidden" name="action" value="checkout">

=======
            <section class="card table-card">
                <div class="section-header-inline">
                    <div>
                        <h2>Thông tin sinh viên mua hàng</h2>
                        <div class="note">Theo dõi nhanh dữ liệu tài khoản trước khi tạo đơn.</div>
                    </div>
                </div>
                <div class="summary-list">
                    <div class="summary-row">
                        <span>Họ và tên</span>
                        <strong>${studentName}</strong>
                    </div>
                    <div class="summary-row">
                        <span>Mã sinh viên</span>
                        <strong>${studentId}</strong>
                    </div>
                </div>
            </section>

            <section class="card table-card">
                <div class="section-header-inline">
                    <div>
                        <h3>Danh sách đầu sách có thể mua</h3>
                        <div class="note">Chỉ hiển thị các sách đã có giá bán.</div>
                    </div>
                </div>

                <div class="table-scroll">
>>>>>>> origin/master
                    <table>
                        <thead>
                            <tr>
                                <th>Tên sách</th>
                                <th>Số lượng còn</th>
                                <th>Giá bán</th>
                                <th>Số lượng mua</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="price" items="${bookPrices}">
                                <c:if test="${price.amount > 0}">
                                    <tr>
                                        <td>${price.bookName}</td>
                                        <td>${price.available}</td>
                                        <td>${price.amount}</td>
                                        <td>
                                            <form id="waitlistAdd_${price.bookID}" method="POST" action="${pageContext.request.contextPath}/buy" class="inline-form inline-edit-form">
                                                <input type="hidden" name="action" value="addToWaitlist">
                                                <input type="hidden" name="bookID" value="${price.bookID}">
                                                <input type="hidden" name="bookName" value="${price.bookName}">
                                                <input type="hidden" name="price" value="${price.amount}">
                                                <input class="qty-input qty-input-sm" type="number" name="quantity" value="1" min="1" max="${price.available > 0 ? price.available : 99}">
                                            </form>
                                        </td>
                                        <td>
                                            <button class="btn btn-buy" type="submit" form="waitlistAdd_${price.bookID}">Thêm vào chờ</button>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            <c:if test="${empty bookPrices}">
                                <tr>
                                    <td colspan="5" class="empty">Chưa có sách nào sẵn sàng để mua.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="student-buy-grid">
                <section class="card table-card">
                    <div class="section-header-inline">
                        <div>
                            <h3>Danh sách sách chọn tạm</h3>
                            <div class="note">Chọn các sách cần checkout hoặc xóa từng mục khỏi danh sách chờ.</div>
                        </div>
                    </div>

                    <c:forEach var="item" items="${waitlistItems}">
                        <form id="deleteWaitlist_${item.bookId}" method="POST" action="${pageContext.request.contextPath}/buy">
                            <input type="hidden" name="action" value="removeFromWaitlist">
                            <input type="hidden" name="bookID" value="${item.bookId}">
                        </form>
                    </c:forEach>

                    <form method="POST" action="${pageContext.request.contextPath}/buy">
                        <input type="hidden" name="action" value="checkout">

                        <div class="table-scroll">
                            <table class="compact-table">
                                <thead>
                                    <tr>
                                        <th class="checkbox-cell">
                                            <input type="checkbox" data-select-all="buy-selection" title="Chọn tất cả">
                                        </th>
                                        <th>Tên sách</th>
                                        <th>SL</th>
                                        <th>Thành tiền</th>
                                        <th>Xóa</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${waitlistItems}">
                                        <tr>
                                            <td class="checkbox-cell">
                                                <input type="checkbox" name="selectedBooks" value="${item.bookId}" data-select-item="buy-selection">
                                            </td>
                                            <td>${item.bookName}</td>
                                            <td>${item.quantity}</td>
                                            <td>${item.totalPrice}</td>
                                            <td>
                                                <button type="submit" form="deleteWaitlist_${item.bookId}" class="link-button link-danger">Xóa</button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty waitlistItems}">
                                        <tr>
                                            <td colspan="5" class="empty">Chưa có sách nào trong danh sách chờ.</td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>

                        <c:if test="${not empty waitlistItems}">
                            <div class="actions mt-4">
                                <button class="btn btn-buy" type="submit">Xác nhận đặt các sách đã chọn</button>
                            </div>
                        </c:if>
<<<<<<< HEAD
                    </tbody>
                </table>
                <p style="font-size: 0.9em; color: gray; margin-top: 10px;">
                    * Cầm <strong>Mã đơn</strong> tới quầy thu ngân thư viện để thanh toán và nhận sách.
                </p>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
=======
                    </form>
                </section>

                <section class="card table-card">
                    <div class="section-header-inline">
                        <div>
                            <h3>Thông tin đơn hàng của bạn</h3>
                            <div class="note">Mang mã đơn tới quầy thư viện để thanh toán và nhận sách khi trạng thái sẵn sàng.</div>
                        </div>
                    </div>

                    <div class="table-scroll">
                        <table class="compact-table">
                            <thead>
                                <tr>
                                    <th>Mã đơn</th>
                                    <th>Ngày đặt</th>
                                    <th>Chi tiết sách</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${orderHistory}">
                                    <tr>
                                        <td>#${order.orderID}</td>
                                        <td>${order.orderDate}</td>
                                        <td>${order.items}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.status eq 'Sẵn sàng'}"><span class="status-ready">Sẵn sàng</span></c:when>
                                                <c:when test="${order.status eq 'Hàng chờ'}"><span class="status-waiting">Hàng chờ</span></c:when>
                                                <c:otherwise>${order.status}</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty orderHistory}">
                                    <tr>
                                        <td colspan="4" class="empty">Bạn chưa có đơn đặt hàng nào.</td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>

                    <p class="summary-note">
                        Cầm <strong>mã đơn</strong> tới quầy thư viện để thanh toán và nhận sách khi đơn đã sẵn sàng.
                    </p>
                </section>
            </section>
        </main>
    </div>

    <%@ include file="../client/_footer.jsp" %>
</body>
</html>
>>>>>>> origin/master
