<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên - Mượn và mua sách</title>
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
            <h2>Trung tâm sinh viên</h2>
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
            <div class="section-header">
                <h3 class="mb-0">Danh sách sách có sẵn</h3>
                <span class="note">Tổng: ${bookTotalItems}</span>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                <input type="hidden" name="action" value="list">
                <div class="search-row search-row-student">
                    <input type="text" name="bookSearch" value="${bookSearch}" placeholder="Tìm theo mã hoặc tên sách">
                    <input type="text" name="purchaseSearch" value="${purchaseSearch}" placeholder="Tìm trong danh sách đã mua">
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Lọc</button>
                        <a class="btn-reset" href="${pageContext.request.contextPath}/borrows?action=list">Đặt lại</a>
                    </div>
                </div>
            </form>

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
                                        <button class="btn btn-buy" type="submit">Mua nhanh</button>
                                    </form>
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="addBuyList">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-secondary" type="submit">Thêm vào cần mua</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableBooks}">
                        <tr><td colspan="4" class="empty">Không có sách phù hợp bộ lọc.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${bookTotalPages > 1}">
                <div class="pagination">
                    <c:if test="${bookCurrentPage > 1}">
                        <c:url var="prevBookUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${bookCurrentPage - 1}"/>
                            <c:param name="purchasePage" value="${purchaseCurrentPage}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${prevBookUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${bookTotalPages}" var="p">
                        <c:url var="bookPageUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${p}"/>
                            <c:param name="purchasePage" value="${purchaseCurrentPage}"/>
                        </c:url>
                        <a class="page-link ${p eq bookCurrentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${bookPageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${bookCurrentPage < bookTotalPages}">
                        <c:url var="nextBookUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${bookCurrentPage + 1}"/>
                            <c:param name="purchasePage" value="${purchaseCurrentPage}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${nextBookUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>

        <div class="card">
            <div class="section-header">
                <h3 class="mb-0">Danh sách sách cần mua</h3>
                <span class="note">Tổng tạm tính: ${buyListTotal}</span>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Mã sách</th>
                        <th>Tên sách</th>
                        <th>Số lượng</th>
                        <th>Còn lại</th>
                        <th>Đơn giá</th>
                        <th>Thành tiền</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${buyListItems}">
                        <tr>
                            <td>${item.bookID}</td>
                            <td>${item.bookName}</td>
                            <td>
                                <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form inline-edit-form">
                                    <input type="hidden" name="action" value="updateBuyQty">
                                    <input type="hidden" name="bookID" value="${item.bookID}">
                                    <input class="qty-input" type="number" min="1" value="${item.quantity}" name="quantity">
                                    <button class="btn btn-secondary" type="submit">Cập nhật</button>
                                </form>
                            </td>
                            <td>${item.available}</td>
                            <td>${item.unitPrice} <c:out value="${item.currency}" default=""/></td>
                            <td>${item.lineTotal}</td>
                            <td>
                                <div class="actions">
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="orderBuyItem">
                                        <input type="hidden" name="bookID" value="${item.bookID}">
                                        <button class="btn btn-approve" type="submit" ${item.canOrder ? '' : 'disabled'}>Gửi duyệt sách này</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="removeBuyItem">
                                        <input type="hidden" name="bookID" value="${item.bookID}">
                                        <button class="btn btn-reject" type="submit">Xóa</button>
                                    </form>
                                </div>
                                <c:if test="${not item.canOrder}">
                                    <div class="note">Không thể gửi: hết hàng hoặc chưa có giá.</div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty buyListItems}">
                        <tr><td colspan="7" class="empty">Danh sách cần mua đang trống.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${not empty buyListItems}">
                <form method="post" action="${pageContext.request.contextPath}/borrows" class="mt-4">
                    <input type="hidden" name="action" value="orderBuyAll">
                    <button class="btn btn-approve" type="submit">Gửi duyệt tất cả</button>
                </form>
            </c:if>
        </div>

        <div class="card">
            <div class="section-header">
                <h3 class="mb-0">Danh sách đã mua (đã duyệt)</h3>
                <span class="note">Tổng: ${purchaseTotalItems}</span>
            </div>

            <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                <input type="hidden" name="action" value="list">
                <input type="hidden" name="bookSearch" value="${bookSearch}">
                <div class="search-row search-row-student">
                    <input type="text" name="purchaseSearch" value="${purchaseSearch}" placeholder="Tìm theo mã đơn hoặc tên sách">
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Tìm</button>
                    </div>
                </div>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Mã đơn</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Chi tiết</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${purchasedOrders}">
                        <tr>
                            <td>${order.orderID}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>${empty order.items ? 'Không có chi tiết' : order.items}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty purchasedOrders}">
                        <tr><td colspan="4" class="empty">Bạn chưa có đơn mua đã duyệt.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${purchaseTotalPages > 1}">
                <div class="pagination">
                    <c:if test="${purchaseCurrentPage > 1}">
                        <c:url var="prevPurchaseUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${bookCurrentPage}"/>
                            <c:param name="purchasePage" value="${purchaseCurrentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${prevPurchaseUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${purchaseTotalPages}" var="p">
                        <c:url var="purchasePageUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${bookCurrentPage}"/>
                            <c:param name="purchasePage" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq purchaseCurrentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${purchasePageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${purchaseCurrentPage < purchaseTotalPages}">
                        <c:url var="nextPurchaseUrl" value="/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="bookSearch" value="${bookSearch}"/>
                            <c:param name="purchaseSearch" value="${purchaseSearch}"/>
                            <c:param name="bookPage" value="${bookCurrentPage}"/>
                            <c:param name="purchasePage" value="${purchaseCurrentPage + 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${nextPurchaseUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
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
                                        <button class="btn btn-return" type="submit">Gửi yêu cầu trả</button>
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
