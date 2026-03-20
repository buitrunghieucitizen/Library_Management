<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên - Mượn và mua sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body class="student-body">
    <%@ include file="../_header.jsp" %>

    <c:url var="borrowListUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>
    <c:url var="buyPageUrl" value="/buy" />
    <c:url var="homeUrl" value="/home" />
    <c:url var="checkoutUrl" value="/borrows">
        <c:param name="action" value="checkout" />
    </c:url>

    <c:set var="safeBookTotalItems" value="${empty bookTotalItems ? 0 : bookTotalItems}" />
    <c:set var="safePurchaseTotalItems" value="${empty purchaseTotalItems ? 0 : purchaseTotalItems}" />
    <c:set var="buyListItemCount" value="${fn:length(buyListItems)}" />
    <c:set var="readyBuyCount" value="0" />
    <c:set var="blockedBuyCount" value="0" />
    <c:forEach var="item" items="${buyListItems}">
        <c:choose>
            <c:when test="${item.canOrder}">
                <c:set var="readyBuyCount" value="${readyBuyCount + 1}" />
            </c:when>
            <c:otherwise>
                <c:set var="blockedBuyCount" value="${blockedBuyCount + 1}" />
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <c:set var="pricedBookCount" value="0" />
    <c:set var="unpricedBookCount" value="0" />
    <c:forEach var="price" items="${bookPrices}">
        <c:choose>
            <c:when test="${price.amount > 0}">
                <c:set var="pricedBookCount" value="${pricedBookCount + 1}" />
            </c:when>
            <c:otherwise>
                <c:set var="unpricedBookCount" value="${unpricedBookCount + 1}" />
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <c:set var="borrowingCount" value="0" />
    <c:set var="overdueCount" value="0" />
    <c:set var="returnedCount" value="0" />
    <c:forEach var="borrow" items="${borrows}">
        <c:choose>
            <c:when test="${borrow.status eq 'Borrowing'}">
                <c:set var="borrowingCount" value="${borrowingCount + 1}" />
            </c:when>
            <c:when test="${borrow.status eq 'Overdue'}">
                <c:set var="overdueCount" value="${overdueCount + 1}" />
            </c:when>
            <c:when test="${borrow.status eq 'Returned'}">
                <c:set var="returnedCount" value="${returnedCount + 1}" />
            </c:when>
        </c:choose>
    </c:forEach>
    <c:set var="activeBorrowCount" value="${borrowingCount + overdueCount}" />

    <c:url var="resetBookFilterUrl" value="/borrows">
        <c:param name="action" value="list" />
        <c:param name="purchaseSearch" value="${purchaseSearch}" />
        <c:param name="purchasePage" value="${purchaseCurrentPage}" />
    </c:url>
    <c:url var="resetPurchaseFilterUrl" value="/borrows">
        <c:param name="action" value="list" />
        <c:param name="bookSearch" value="${bookSearch}" />
        <c:param name="bookPage" value="${bookCurrentPage}" />
    </c:url>

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <section class="page-hero student-hero">
                <div class="student-hero-copy">
                    <span class="page-hero-kicker">Borrow And Buy Center</span>
                    <h1>Trung tâm mượn và mua sách</h1>
                    <p>Tra cứu sách, so sánh giá, gom danh sách cần mua, theo dõi đơn đã duyệt và quản lý phiếu mượn ngay trên cùng một màn hình làm việc.</p>
                    <div class="student-hero-badges">
                        <span class="student-chip">Mượn nhanh từ kho sách đang có</span>
                        <span class="student-chip">Checkout gọn các mục cần mua</span>
                        <span class="student-chip">Theo dõi trả sách theo từng phiếu</span>
                    </div>
                </div>
                <div class="page-hero-actions">
                    <a class="hero-action primary" href="${buyPageUrl}">Mở trang mua riêng</a>
                    <a class="hero-action secondary" href="${borrowListUrl}">Làm mới dữ liệu</a>
                </div>
            </section>

            <section class="student-kpi-grid">
                <article class="student-kpi-card">
                    <span>Mã sinh viên</span>
                    <strong><c:out value="${studentId}" default="-" /></strong>
                    <p>Tài khoản hiện tại đang thao tác trên trung tâm giao dịch.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Sách khả dụng</span>
                    <strong>${safeBookTotalItems}</strong>
                    <p>Các đầu sách đang khớp với bộ lọc kho sách bên dưới.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Mục sẵn checkout</span>
                    <strong>${readyBuyCount}</strong>
                    <p>${buyListItemCount} mục hiện nằm trong danh sách cần mua.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Phiếu đang theo dõi</span>
                    <strong>${activeBorrowCount}</strong>
                    <p>${overdueCount} quá hạn, ${returnedCount} phiếu đã hoàn tất.</p>
                </article>
            </section>

            <nav class="student-quick-nav card-soft" aria-label="Điều hướng nhanh">
                <a class="student-quick-link" href="#price-panel">Bảng giá</a>
                <a class="student-quick-link" href="#catalog-panel">Kho sách</a>
                <a class="student-quick-link" href="#buy-list-panel">Cần mua</a>
                <a class="student-quick-link" href="#purchase-panel">Đã mua</a>
                <a class="student-quick-link" href="#borrow-panel">Phiếu mượn</a>
                <a class="student-quick-link" href="${homeUrl}">Thư viện</a>
            </nav>

            <c:if test="${not empty mappingError}"><div class="error">${mappingError}</div></c:if>
            <c:if test="${not empty param.msg}"><div class="msg">${param.msg}</div></c:if>
            <c:if test="${not empty param.error}"><div class="error">${param.error}</div></c:if>

            <section class="student-highlight-grid">
                <article class="student-highlight-card good">
                    <span>Sẵn gửi duyệt</span>
                    <strong>${readyBuyCount}</strong>
                    <p><c:choose><c:when test="${readyBuyCount gt 0}">Các mục này đã đủ giá và tồn kho để đưa vào checkout ngay.</c:when><c:otherwise>Hiện chưa có mục nào trong danh sách cần mua đủ điều kiện để checkout.</c:otherwise></c:choose></p>
                </article>
                <article class="student-highlight-card neutral">
                    <span>Đơn đã duyệt</span>
                    <strong>${safePurchaseTotalItems}</strong>
                    <p><c:choose><c:when test="${safePurchaseTotalItems gt 0}">Có thể tra cứu lại lịch sử đơn đã hoàn tất ở khu vực đã mua.</c:when><c:otherwise>Bạn chưa có đơn mua nào được duyệt trong hệ thống.</c:otherwise></c:choose></p>
                </article>
                <article class="student-highlight-card ${overdueCount gt 0 ? 'warn' : 'neutral'}">
                    <span>Phiếu cần chú ý</span>
                    <strong>${overdueCount}</strong>
                    <p><c:choose><c:when test="${overdueCount gt 0}">Có phiếu quá hạn. Nên gửi yêu cầu trả để thư viện xử lý sớm.</c:when><c:otherwise>Chưa có phiếu nào quá hạn. Hiện bạn đang theo dõi ${activeBorrowCount} phiếu mở.</c:otherwise></c:choose></p>
                </article>
            </section>

            <section class="card table-card" id="price-panel">
                <div class="section-header-inline student-section-head">
                    <div>
                        <h2>Bảng giá sách hiện hành</h2>
                        <div class="note">So sánh nhanh mức giá, trạng thái mua và số lượng còn lại trước khi tạo đơn.</div>
                    </div>
                    <div class="student-head-badges">
                        <span class="student-chip soft">${pricedBookCount} đầu sách đã có giá</span>
                        <span class="student-chip warning">${unpricedBookCount} đầu sách chờ cập nhật</span>
                    </div>
                </div>
                <div class="table-scroll">
                    <table class="compact-table">
                        <thead><tr><th>Mã</th><th>Tên sách</th><th>Giá</th><th>Tiền tệ</th><th>Trạng thái</th><th>Ghi chú</th><th>Còn lại</th></tr></thead>
                        <tbody>
                            <c:forEach var="price" items="${bookPrices}">
                                <tr>
                                    <td>${price.bookID}</td><td>${price.bookName}</td>
                                    <td><c:choose><c:when test="${price.amount > 0}">${price.amount}</c:when><c:otherwise>Chưa cập nhật</c:otherwise></c:choose></td>
                                    <td><c:out value="${price.currency}" default="-" /></td>
                                    <td><c:choose><c:when test="${price.amount > 0}"><span class="status-ok">Có thể mua</span></c:when><c:otherwise><span class="status-bad">Chờ giá</span></c:otherwise></c:choose></td>
                                    <td><c:out value="${price.note}" default="-" /></td>
                                    <td>${price.available}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty bookPrices}"><tr><td colspan="7" class="empty">Chưa có bảng giá sách.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="card table-card" id="catalog-panel">
                <div class="section-header-inline student-section-head">
                    <div>
                        <h2>Kho sách có thể thao tác</h2>
                        <div class="note">Từ cùng một hàng, bạn có thể mượn ngay, mua nhanh hoặc đưa sách vào danh sách cần mua.</div>
                    </div>
                    <div class="student-head-badges">
                        <span class="student-chip neutral">Tổng: ${safeBookTotalItems}</span>
                        <span class="student-chip neutral">Trang ${bookCurrentPage}/${bookTotalPages}</span>
                    </div>
                </div>
                <div class="student-action-hint">
                    <span><strong>Mượn</strong> tạo phiếu mượn ngay cho tài khoản học sinh hiện tại.</span>
                    <span><strong>Mua nhanh</strong> gửi ngay một yêu cầu mua cho đúng đầu sách đó.</span>
                    <span><strong>Thêm vào cần mua</strong> giúp gom nhiều đầu sách trước khi checkout.</span>
                </div>
                <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                    <input type="hidden" name="action" value="list">
                    <input type="hidden" name="purchaseSearch" value="${purchaseSearch}">
                    <input type="hidden" name="purchasePage" value="${purchaseCurrentPage}">
                    <div class="search-row student-search-row-single">
                        <input type="text" name="bookSearch" value="${bookSearch}" placeholder="Tìm theo mã hoặc tên sách để lọc kho sách">
                        <div class="search-actions">
                            <button class="btn-apply" type="submit">Lọc kho sách</button>
                            <a class="btn-reset" href="${pageContext.request.contextPath}${resetBookFilterUrl}">Xóa lọc</a>
                        </div>
                    </div>
                </form>
                <div class="table-scroll">
                    <table>
                        <thead><tr><th>Mã</th><th>Tên sách</th><th>Còn lại</th><th>Hành động</th></tr></thead>
                        <tbody>
                            <c:forEach var="book" items="${availableBooks}">
                                <tr>
                                    <td>${book.bookID}</td><td>${book.bookName}</td><td>${book.available}</td>
                                    <td>
                                        <div class="actions">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="borrow"><input type="hidden" name="bookID" value="${book.bookID}"><button class="btn btn-borrow" type="submit">Mượn</button></form>
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="buy"><input type="hidden" name="bookID" value="${book.bookID}"><button class="btn btn-buy" type="submit">Mua nhanh</button></form>
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="addBuyList"><input type="hidden" name="bookID" value="${book.bookID}"><button class="btn btn-secondary" type="submit">Thêm vào cần mua</button></form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty availableBooks}"><tr><td colspan="4" class="empty">Không có sách phù hợp với bộ lọc hiện tại.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
                <c:if test="${bookTotalPages > 1}">
                    <div class="pagination">
                        <c:if test="${bookCurrentPage > 1}">
                            <c:url var="prevBookUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${bookCurrentPage - 1}"/><c:param name="purchasePage" value="${purchaseCurrentPage}"/></c:url>
                            <a class="page-link" href="${pageContext.request.contextPath}${prevBookUrl}">Trang trước</a>
                        </c:if>
                        <c:forEach begin="1" end="${bookTotalPages}" var="p">
                            <c:url var="bookPageUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${p}"/><c:param name="purchasePage" value="${purchaseCurrentPage}"/></c:url>
                            <a class="page-link ${p eq bookCurrentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${bookPageUrl}">${p}</a>
                        </c:forEach>
                        <c:if test="${bookCurrentPage < bookTotalPages}">
                            <c:url var="nextBookUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${bookCurrentPage + 1}"/><c:param name="purchasePage" value="${purchaseCurrentPage}"/></c:url>
                            <a class="page-link" href="${pageContext.request.contextPath}${nextBookUrl}">Trang sau</a>
                        </c:if>
                    </div>
                </c:if>
            </section>

            <section class="student-section-grid">
                <section class="card table-card" id="buy-list-panel">
                    <div class="section-header-inline student-section-head">
                        <div>
                            <h2>Danh sách cần mua</h2>
                            <div class="note">Quản lý số lượng, theo dõi điều kiện gửi duyệt và checkout gọn một lần.</div>
                        </div>
                        <div class="student-head-actions">
                            <span class="student-chip success">${readyBuyCount} sẵn gửi duyệt</span>
                            <c:if test="${blockedBuyCount gt 0}"><span class="student-chip warning">${blockedBuyCount} cần kiểm tra</span></c:if>
                            <c:if test="${not empty buyListItems}"><a class="btn btn-approve" href="${checkoutUrl}">Checkout đơn mua</a></c:if>
                        </div>
                    </div>
                    <c:if test="${blockedBuyCount gt 0}"><div class="student-inline-alert warn">Một số mục chưa thể gửi duyệt do hết hàng hoặc chưa có giá. Kiểm tra cột trạng thái trước khi checkout.</div></c:if>
                    <div class="table-scroll">
                        <table>
                            <thead><tr><th>Mã sách</th><th>Tên sách</th><th>Số lượng</th><th>Còn lại</th><th>Đơn giá</th><th>Thành tiền</th><th>Trạng thái</th><th>Hành động</th></tr></thead>
                            <tbody>
                                <c:forEach var="item" items="${buyListItems}">
                                    <tr>
                                        <td>${item.bookID}</td><td>${item.bookName}</td>
                                        <td>
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form inline-edit-form">
                                                <input type="hidden" name="action" value="updateBuyQty"><input type="hidden" name="bookID" value="${item.bookID}">
                                                <input class="qty-input" type="number" min="1" value="${item.quantity}" name="quantity">
                                                <button class="btn btn-secondary" type="submit">Cập nhật</button>
                                            </form>
                                        </td>
                                        <td>${item.available}</td>
                                        <td>${item.unitPrice} <c:out value="${item.currency}" default="" /></td>
                                        <td>${item.lineTotal}</td>
                                        <td><c:choose><c:when test="${item.canOrder}"><span class="status-ok">Sẵn gửi duyệt</span></c:when><c:otherwise><span class="status-bad">Cần kiểm tra</span></c:otherwise></c:choose></td>
                                        <td>
                                            <div class="actions">
                                                <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="orderBuyItem"><input type="hidden" name="bookID" value="${item.bookID}"><button class="btn btn-approve" type="submit" ${item.canOrder ? '' : 'disabled'}>Gửi duyệt sách này</button></form>
                                                <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="removeBuyItem"><input type="hidden" name="bookID" value="${item.bookID}"><button class="btn btn-reject" type="submit">Xóa</button></form>
                                            </div>
                                            <c:if test="${not item.canOrder}"><div class="note">Không thể gửi vì sách hết hàng hoặc chưa có giá.</div></c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty buyListItems}"><tr><td colspan="8" class="empty">Danh sách cần mua đang trống.</td></tr></c:if>
                            </tbody>
                        </table>
                    </div>
                    <c:if test="${not empty buyListItems}"><div class="summary-note">Tổng tạm tính hiện tại là <strong>${buyListTotal}</strong>. Checkout sẽ gửi toàn bộ mục hợp lệ để thư viện xét duyệt.</div></c:if>
                </section>

                <div class="panel-stack">
                    <section class="card table-card" id="purchase-panel">
                        <div class="section-header-inline student-section-head">
                            <div>
                                <h2>Danh sách đã mua</h2>
                                <div class="note">Chỉ hiển thị các đơn đã được duyệt để bạn tra cứu lại lịch sử nhận sách.</div>
                            </div>
                            <div class="student-head-badges">
                                <span class="student-chip soft">Tổng: ${safePurchaseTotalItems}</span>
                                <span class="student-chip neutral">Trang ${purchaseCurrentPage}/${purchaseTotalPages}</span>
                            </div>
                        </div>
                        <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                            <input type="hidden" name="action" value="list"><input type="hidden" name="bookSearch" value="${bookSearch}"><input type="hidden" name="bookPage" value="${bookCurrentPage}">
                            <div class="search-row student-search-row-single">
                                <input type="text" name="purchaseSearch" value="${purchaseSearch}" placeholder="Tìm theo mã đơn hoặc tên sách đã mua">
                                <div class="search-actions"><button class="btn-apply" type="submit">Tìm đơn đã duyệt</button><a class="btn-reset" href="${pageContext.request.contextPath}${resetPurchaseFilterUrl}">Xóa lọc</a></div>
                            </div>
                        </form>
                        <div class="table-scroll">
                            <table class="compact-table">
                                <thead><tr><th>Mã đơn</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Chi tiết</th></tr></thead>
                                <tbody>
                                    <c:forEach var="order" items="${purchasedOrders}">
                                        <tr><td>${order.orderID}</td><td>${order.orderDate}</td><td>${order.totalAmount}</td><td>${empty order.items ? 'Không có chi tiết' : order.items}</td></tr>
                                    </c:forEach>
                                    <c:if test="${empty purchasedOrders}"><tr><td colspan="4" class="empty">Bạn chưa có đơn mua nào đã duyệt.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                        <c:if test="${purchaseTotalPages > 1}">
                            <div class="pagination">
                                <c:if test="${purchaseCurrentPage > 1}">
                                    <c:url var="prevPurchaseUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${bookCurrentPage}"/><c:param name="purchasePage" value="${purchaseCurrentPage - 1}"/></c:url>
                                    <a class="page-link" href="${pageContext.request.contextPath}${prevPurchaseUrl}">Trang trước</a>
                                </c:if>
                                <c:forEach begin="1" end="${purchaseTotalPages}" var="p">
                                    <c:url var="purchasePageUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${bookCurrentPage}"/><c:param name="purchasePage" value="${p}"/></c:url>
                                    <a class="page-link ${p eq purchaseCurrentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${purchasePageUrl}">${p}</a>
                                </c:forEach>
                                <c:if test="${purchaseCurrentPage < purchaseTotalPages}">
                                    <c:url var="nextPurchaseUrl" value="/borrows"><c:param name="action" value="list"/><c:param name="bookSearch" value="${bookSearch}"/><c:param name="purchaseSearch" value="${purchaseSearch}"/><c:param name="bookPage" value="${bookCurrentPage}"/><c:param name="purchasePage" value="${purchaseCurrentPage + 1}"/></c:url>
                                    <a class="page-link" href="${pageContext.request.contextPath}${nextPurchaseUrl}">Trang sau</a>
                                </c:if>
                            </div>
                        </c:if>
                    </section>

                    <section class="card table-card" id="borrow-panel">
                        <div class="section-header-inline student-section-head">
                            <div>
                                <h2>Sách đang mượn của bạn</h2>
                                <div class="note">Theo dõi trạng thái trả sách theo từng phiếu và gửi yêu cầu trả trực tiếp từ bảng.</div>
                            </div>
                            <div class="student-head-badges">
                                <span class="student-chip soft">${borrowingCount} đang mượn</span>
                                <span class="student-chip warning">${overdueCount} quá hạn</span>
                                <span class="student-chip neutral">${renewableBorrowCount} có thể gia hạn</span>
                                <span class="student-chip success">${returnedCount} đã trả</span>
                            </div>
                        </div>
                        <c:if test="${overdueCount gt 0}"><div class="student-inline-alert warn">Bạn có phiếu quá hạn. Nên gửi yêu cầu trả sớm để thư viện đối soát.</div></c:if>
                        <c:if test="${renewableBorrowCount gt 0}"><div class="student-inline-alert success">Có ${renewableBorrowCount} phiếu đang đủ điều kiện gia hạn online. Chỉ gia hạn trong ${studentRenewalWindowDays} ngày cuối và mỗi phiếu thêm tối đa ${studentRenewalDays} ngày.</div></c:if>
                        <c:if test="${activeBorrowCount gt 0 and overdueCount eq 0 and renewableBorrowCount eq 0}"><div class="student-inline-alert">Các phiếu hiện tại vẫn đang trong hạn. Bạn có thể gửi yêu cầu trả ngay khi không còn nhu cầu sử dụng hoặc chờ tới ${studentRenewalWindowDays} ngày cuối để gia hạn online.</div></c:if>
                        <div class="table-scroll">
                            <table class="compact-table">
                                <thead><tr><th>Mã phiếu mượn</th><th>Ngày mượn</th><th>Hạn trả</th><th>Ngày trả</th><th>Trạng thái</th><th>Sách</th><th>Hành động</th></tr></thead>
                                <tbody>
                                    <c:forEach var="b" items="${borrows}">
                                        <c:set var="renewalDecision" value="${renewalDecisionByBorrowId[b.borrowID]}" />
                                        <tr>
                                            <td>${b.borrowID}</td><td>${b.borrowDate}</td><td>${b.dueDate}</td>
                                            <td><c:out value="${b.returnDate}" default="-" /></td>
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
                                                <div class="borrow-action-stack">
                                                    <c:if test="${renewalDecision.eligible}">
                                                        <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="renew"><input type="hidden" name="borrowID" value="${b.borrowID}"><button class="btn btn-renew" type="submit">Gia hạn</button></form>
                                                    </c:if>
                                                    <c:if test="${b.status ne 'Returned'}">
                                                        <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form"><input type="hidden" name="action" value="requestReturn"><input type="hidden" name="borrowID" value="${b.borrowID}"><button class="btn btn-return" type="submit">Gửi yêu cầu trả</button></form>
                                                    </c:if>
                                                    <c:if test="${b.status ne 'Returned' and not empty renewalDecision.message}">
                                                        <div class="borrow-action-note ${renewalDecision.eligible ? 'success' : 'muted'}"><c:out value="${renewalDecision.message}" /></div>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty borrows}"><tr><td colspan="7" class="empty">Bạn chưa có phiếu mượn nào.</td></tr></c:if>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
