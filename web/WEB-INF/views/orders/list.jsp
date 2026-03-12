<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <title>Quản lý đơn hàng</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
            <style>
                .search-box {
                    padding: 20px;
                    background: #f9f9f9;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                    margin-bottom: 20px;
                }

                .search-box input[type="number"] {
                    padding: 10px;
                    width: 250px;
                    font-size: 1em;
                }

                .details-box {
                    background: #eef7e9;
                    padding: 15px;
                    border-left: 5px solid #4CAF50;
                    margin-bottom: 20px;
                }

                .details-box ul {
                    margin-top: 10px;
                    margin-bottom: 15px;
                }

                .details-box li {
                    margin-bottom: 5px;
                    font-size: 1.1em;
                }
            </style>
        </head>

        <body>
            <div class="navbar">
                <h1>Quản lý thư viện</h1>
                <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
                <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
                <a href="${pageContext.request.contextPath}/admin/orders" style="font-weight: bold;">Đơn hàng</a>
                <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
                <c:if test="${isAdmin}">
                    <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
                    <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
                    <a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a>
                    <a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a>
                    <a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a>
                    <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
                </c:if>
                <div class="nav-right">
                    <span>${sessionScope.staff.staffName}</span>
                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                </div>
            </div>

            <div class="container">
                <div class="panel">
                    <h2>Quầy thu ngân & Quản lý đơn hàng</h2>

                    <c:if test="${not empty param.msg}">
                        <div class="msg" style="color: green; margin-bottom: 15px;">${param.msg}</div>
                    </c:if>
                    <c:if test="${not empty param.error}">
                        <div class="error" style="color: red; margin-bottom: 15px;">${param.error}</div>
                    </c:if>
                    <c:if test="${not empty error}">
                        <div class="error" style="color: red; margin-bottom: 15px;">${error}</div>
                    </c:if>

                    <div class="search-box">
                        <form action="${pageContext.request.contextPath}/admin/orders" method="GET">
                            <label for="txtOrderID" style="font-weight: bold; font-size: 1.1em;">Nhập mã đơn hàng sinh
                                viên cung cấp: </label>

                            <input type="number" id="txtOrderID" name="orderID" required placeholder="Ví dụ: 15">

                            <button type="submit" class="btn btn-approve" style="padding: 10px 20px;">Tra cứu / Kiểm
                                tra</button>
                        </form>
                    </div>

                    <c:if test="${not empty searchResult}">
                        <div class="details-box">
                            <h3>Chi tiết đơn hàng #${searchResult.orderID}</h3>
                            <p>Mã sinh viên đặt: <strong>${searchResult.studentID}</strong></p>
                            <p>Trạng thái hiện tại: <strong>${searchResult.status}</strong></p>
                            <p>Tổng tiền phải thu: <strong
                                    style="color:red; font-size: 1.2em;">${searchResult.totalAmount} VNĐ</strong></p>

                            <h4>Danh sách sách cần lấy trên kệ:</h4>
                            <ul>
                                <c:forEach var="item" items="${orderItems}">
                                    <li>👉 Sách: <strong>${item.bookName}</strong> | Số lượng:
                                        <strong>${item.quantity}</strong> (Đơn giá: ${item.unitPrice})
                                    </li>
                                </c:forEach>
                            </ul>

                            <c:if
                                test="${searchResult.status eq 'Sẵn sàng' or searchResult.status eq 'Hàng chờ' or searchResult.status eq 'Pending' or searchResult.status eq 'Approved'}">
                                <div style="margin-top: 15px; display: flex; gap: 10px;">
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/orders"
                                        onsubmit="return confirm('Xác nhận ĐÃ THU TIỀN và giao sách cho sinh viên? (Hành động này sẽ trừ số lượng sách trong kho)');">
                                        <input type="hidden" name="action" value="complete">
                                        <input type="hidden" name="orderID" value="${searchResult.orderID}">
                                        <button type="submit" class="btn btn-approve" style="padding: 10px 20px;">Đã thu
                                            tiền & Hoàn thành giao sách</button>
                                    </form>

                                    <form method="POST" action="${pageContext.request.contextPath}/admin/orders"
                                        onsubmit="return confirm('Bạn có chắc chắn muốn HỦY đơn này?');">
                                        <input type="hidden" name="action" value="cancel">
                                        <input type="hidden" name="orderID" value="${searchResult.orderID}">
                                        <button type="submit" class="btn btn-reject" style="padding: 10px 20px;">Hủy bỏ
                                            đơn</button>
                                    </form>
                                </div>
                            </c:if>
                        </div>
                    </c:if>

                    <hr style="margin: 30px 0; border: 0; border-top: 1px solid #ddd;">

                    <h3>Danh sách toàn bộ đơn hàng</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Sinh viên</th>
                                <th>Xử lý bởi</th>
                                <th>Ngày đặt</th>
                                <th>Tổng tiền</th>
                                <th>Trạng thái</th>
                                <th>Chi tiết</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="order" items="${allOrders}">
                                <tr>
                                    <td><strong>#${order.orderID}</strong></td>
                                    <td>${order.studentName}</td>
                                    <td>${empty order.staffName ? '-' : order.staffName}</td>
                                    <td>${order.orderDate}</td>
                                    <td>${order.totalAmount}</td>
                                    <td>
                                        <span style="font-weight: bold; color:
                                          ${order.status eq 'Đã giao' or order.status eq 'Approved' ? 'green' : 
                                            (order.status eq 'Đã hủy' or order.status eq 'Rejected' ? 'red' : 
                                            (order.status eq 'Sẵn sàng' ? '#007bff' : 'orange'))};">
                                            ${order.status}
                                        </span>
                                    </td>
                                    <td>${empty order.items ? 'Không có chi tiết' : order.items}</td>
                                    <td>
                                        <c:if test="${order.status eq 'Pending'}">
                                            <form method="POST" action="${pageContext.request.contextPath}/admin/orders"
                                                style="display:inline-block; margin-bottom: 5px;">
                                                <input type="hidden" name="action" value="approve">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <button type="submit" class="btn btn-approve"
                                                    style="padding: 5px 10px; width: 80px;"
                                                    onclick="return confirm('Xác nhận duyệt đơn hàng này (Trừ kho)?');">Duyệt</button>
                                            </form>
                                            <br />
                                            <form method="POST" action="${pageContext.request.contextPath}/admin/orders"
                                                style="display:inline-block;">
                                                <input type="hidden" name="action" value="reject">
                                                <input type="hidden" name="orderID" value="${order.orderID}">
                                                <button type="submit" class="btn btn-reject"
                                                    style="padding: 5px 10px; width: 80px; background-color: #f44336; color: white; border: none; cursor: pointer;"
                                                    onclick="return confirm('Xác nhận từ chối đơn hàng này?');">Từ
                                                    chối</button>
                                            </form>
                                        </c:if>
                                        <c:if
                                            test="${order.status ne 'Pending' and order.status ne 'Đã giao' and order.status ne 'Đã hủy' and order.status ne 'Approved' and order.status ne 'Rejected'}">
                                            <a href="${pageContext.request.contextPath}/admin/orders?orderID=${order.orderID}"
                                                class="btn btn-approve"
                                                style="text-decoration: none; padding: 5px 10px; display: inline-block;">Xử
                                                lý</a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty allOrders}">
                                <tr>
                                    <td colspan="8" class="empty-row" style="text-align: center;">Chưa có đơn hàng nào.
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </body>

        </html>