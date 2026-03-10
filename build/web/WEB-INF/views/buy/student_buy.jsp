<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Cổng sinh viên - Mua sách</title>
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

            <div class="card">
                <h3>Danh sách sách chọn tạm</h3>

                <c:forEach var="item" items="${waitlistItems}">
                    <form id="formDelete_${item.bookId}" method="POST" action="${pageContext.request.contextPath}/buy">
                        <input type="hidden" name="action" value="removeFromWaitlist">
                        <input type="hidden" name="bookID" value="${item.bookId}">
                    </form>
                </c:forEach>

                <form method="POST" action="${pageContext.request.contextPath}/buy">
                    <input type="hidden" name="action" value="checkout">
                    
                    <table>
                        <thead>
                            <tr>
                                <th style="width: 50px; text-align: center;">
                                    <input type="checkbox" id="selectAll" onclick="toggleAll(this)" title="Chọn tất cả">
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
                                    <td style="text-align: center;">
                                        <input type="checkbox" name="selectedBooks" value="${item.bookId}">
                                    </td>
                                    <td>${item.bookName}</td>
                                    <td>${item.quantity}</td>
                                    <td>${item.totalPrice}</td>
                                    <td>
                                        <button type="submit" form="formDelete_${item.bookId}" style="color:red; cursor:pointer; background:none; border:none; text-decoration:underline;">Xóa</button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty waitlistItems}">
                                <tr><td colspan="5" style="text-align: center;">Chưa có sách nào trong danh sách chờ.</td></tr>
                            </c:if>
                        </tbody>
                    </table>

                    <c:if test="${not empty waitlistItems}">
                        <div style="margin-top: 15px; text-align: right;">
                            <button class="btn btn-buy" type="submit">Xác nhận Đặt các sách đã chọn</button>
                        </div>
                    </c:if>
                </form>

                <script>
                    function toggleAll(source) {
                        var checkboxes = document.getElementsByName('selectedBooks');
                        for (var i = 0, n = checkboxes.length; i < n; i++) {
                            checkboxes[i].checked = source.checked;
                        }
                    }
                </script>
            </div>

            <div class="card">
                <h3>Thông tin đơn hàng của bạn</h3>
                <table>
                    <thead>
                        <tr><th>Mã đơn</th><th>Ngày đặt</th><th>Chi tiết sách</th><th>Trạng thái</th></tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orderHistory}">
                            <tr>
                                <td>#${order.orderID}</td>
                                <td>${order.orderDate}</td>
                                <td>${order.items}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${order.status eq 'Sẵn sàng'}"><span style="color: green; font-weight: bold;">Sẵn sàng</span></c:when>
                                        <c:when test="${order.status eq 'Hàng chờ'}"><span style="color: orange; font-weight: bold;">Hàng chờ</span></c:when>
                                        <c:otherwise>${order.status}</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty orderHistory}">
                            <tr><td colspan="4">Bạn chưa có đơn đặt hàng nào.</td></tr>
                        </c:if>
                    </tbody>
                </table>
                <p style="font-size: 0.9em; color: gray; margin-top: 10px;">
                    * Cầm <strong>Mã đơn</strong> tới quầy thu ngân thư viện để thanh toán và nhận sách.
                </p>
            </div>
        </div>
    </body>
</html>