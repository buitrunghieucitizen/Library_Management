<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cong sinh vien - Muon va mua sach</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Quan ly thu vien</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/books">Sach</a>
        <a href="${pageContext.request.contextPath}/borrows?action=list">Muon va mua sach</a>
        <div class="nav-right">
            <span>Xin chao, ${sessionScope.staff.staffName} (Sinh vien)</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="card">
            <h2>Trung tam sinh vien</h2>
            <c:if test="${not empty mappingError}">
                <div class="error">${mappingError}</div>
            </c:if>
            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
            <p>Ma sinh vien: <strong><c:out value="${studentId}" default="-"/></strong></p>
        </div>

        <div class="card">
            <h3>Bang gia sach hien hanh</h3>
            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Ten sach</th>
                        <th>Gia</th>
                        <th>Tien te</th>
                        <th>Ghi chu</th>
                        <th>Con lai</th>
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
                                    <c:otherwise>Chua cap nhat</c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${price.currency}" default="-"/></td>
                            <td><c:out value="${price.note}" default="-"/></td>
                            <td>${price.available}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookPrices}">
                        <tr><td colspan="6" class="empty">Chua co bang gia sach.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>

        <div class="card">
            <div class="section-header">
                <h3 class="mb-0">Danh sach sach co san</h3>
                <span class="note">Tong: ${bookTotalItems}</span>
            </div>
            <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                <input type="hidden" name="action" value="list">
                <div class="search-row search-row-student">
                    <input type="text" name="bookSearch" value="${bookSearch}" placeholder="Tim theo ma hoac ten sach">
                    <input type="text" name="purchaseSearch" value="${purchaseSearch}" placeholder="Tim trong danh sach da mua">
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Loc</button>
                        <a class="btn-reset" href="${pageContext.request.contextPath}/borrows?action=list">Dat lai</a>
                    </div>
                </div>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Ten sach</th>
                        <th>Con lai</th>
                        <th>Hanh dong</th>
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
                                        <button class="btn btn-borrow" type="submit">Muon</button>
                                    </form>
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="buy">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-buy" type="submit">Mua nhanh</button>
                                    </form>
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="addBuyList">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button class="btn btn-secondary" type="submit">Them vao can mua</button>
                                    </form>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty availableBooks}">
                        <tr><td colspan="4" class="empty">Khong co sach phu hop bo loc.</td></tr>
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
                        <a class="page-link" href="${pageContext.request.contextPath}${prevBookUrl}">Trang truoc</a>
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
                <h3 class="mb-0">Danh sach book can mua</h3>
                <span class="note">Tong tam tinh: ${buyListTotal}</span>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Ma sach</th>
                        <th>Ten sach</th>
                        <th>So luong</th>
                        <th>Con lai</th>
                        <th>Don gia</th>
                        <th>Thanh tien</th>
                        <th>Hanh dong</th>
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
                                    <button class="btn btn-secondary" type="submit">Cap nhat</button>
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
                                        <button class="btn btn-approve" type="submit" ${item.canOrder ? '' : 'disabled'}>Gui duyet sach nay</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="removeBuyItem">
                                        <input type="hidden" name="bookID" value="${item.bookID}">
                                        <button class="btn btn-reject" type="submit">Xoa</button>
                                    </form>
                                </div>
                                <c:if test="${not item.canOrder}">
                                    <div class="note">Khong the gui: het hang hoac chua co gia.</div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty buyListItems}">
                        <tr><td colspan="7" class="empty">Danh sach can mua dang trong.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${not empty buyListItems}">
                <form method="post" action="${pageContext.request.contextPath}/borrows" class="mt-4">
                    <input type="hidden" name="action" value="orderBuyAll">
                    <button class="btn btn-approve" type="submit">Gui duyet tat ca</button>
                </form>
            </c:if>
        </div>

        <div class="card">
            <div class="section-header">
                <h3 class="mb-0">Danh sach da mua (da duyet)</h3>
                <span class="note">Tong: ${purchaseTotalItems}</span>
            </div>

            <form method="get" action="${pageContext.request.contextPath}/borrows" class="search-form">
                <input type="hidden" name="action" value="list">
                <input type="hidden" name="bookSearch" value="${bookSearch}">
                <div class="search-row search-row-student">
                    <input type="text" name="purchaseSearch" value="${purchaseSearch}" placeholder="Tim theo ma don hoac ten sach">
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Tim</button>
                    </div>
                </div>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Ma don</th>
                        <th>Ngay dat</th>
                        <th>Tong tien</th>
                        <th>Chi tiet</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${purchasedOrders}">
                        <tr>
                            <td>${order.orderID}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>${empty order.items ? 'Khong co chi tiet' : order.items}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty purchasedOrders}">
                        <tr><td colspan="4" class="empty">Ban chua co don mua da duyet.</td></tr>
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
                        <a class="page-link" href="${pageContext.request.contextPath}${prevPurchaseUrl}">Trang truoc</a>
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
            <h3>Sach dang muon cua ban</h3>
            <table>
                <thead>
                    <tr>
                        <th>Ma phieu muon</th>
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
                            <td>${b.borrowDate}</td>
                            <td>${b.dueDate}</td>
                            <td><c:out value="${b.returnDate}" default="-"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status-borrowing">Dang muon</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status-returned">Da tra</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status-overdue">Qua han</span></c:when>
                                    <c:otherwise>${b.status}</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="requestReturn">
                                        <input type="hidden" name="borrowID" value="${b.borrowID}">
                                        <button class="btn btn-return" type="submit">Gui yeu cau tra</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty borrows}">
                        <tr><td colspan="7" class="empty">Ban chua co phieu muon nao.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>
