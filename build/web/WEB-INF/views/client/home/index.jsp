<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cổng sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260321-student-ui">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
</head>
<body class="student-body">
    <%@ include file="../_header.jsp" %>

    <c:url var="homeUrl" value="/home" />
    <c:url var="borrowCenterUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>
    <c:url var="profileUrl" value="/profile" />

    <div class="layout student-layout student-layout-home">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content">
            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error-box"><c:out value="${param.error}" /></div>
            </c:if>

            <section class="hero page-hero">
                <div class="student-hero-copy">
                    <span class="page-hero-kicker">Personal Dashboard</span>
                    <h1>Xin chào, <c:out value="${empty studentDisplayName ? 'sinh viên' : studentDisplayName}" /></h1>
                    <p>Theo dõi nhanh tình trạng mượn sách, hạn trả, đơn mua, giỏ mượn và đặt giữ chỗ trong cùng một màn hình làm việc.</p>
                    <div class="student-hero-badges">
                        <span class="student-chip">Đang mượn ${studentBorrowingCount}</span>
                        <span class="student-chip">Sắp đến hạn ${studentDueSoonCount}</span>
                        <span class="student-chip">Đơn mua ${studentOrderCount}</span>
                        <span class="student-chip">Giỏ mượn ${borrowCartSize}/${maxCartSize}</span>
                    </div>
                </div>
                <div class="page-hero-actions">
                    <a href="${borrowCenterUrl}" class="hero-action primary">Mở trung tâm mượn trả</a>
                    <a href="${profileUrl}" class="hero-action secondary">Mở hồ sơ sinh viên</a>
                </div>
            </section>

            <c:if test="${eligibility != null}">
                <section class="card-soft student-quick-nav">
                    <span class="student-chip soft">Phiếu mượn ${eligibility.activeBorrows}/${maxCartSize}</span>
                    <span class="student-chip ${eligibility.remainingSlots gt 0 ? 'success' : 'warning'}">Còn có thể mượn ${eligibility.remainingSlots}</span>
                    <span class="student-chip neutral">Tuần này ${eligibility.borrowedThisWeek}/${eligibility.maxPerWeek}</span>
                    <c:if test="${eligibility.hasOverdue}">
                        <span class="student-chip warning">Có sách quá hạn</span>
                    </c:if>
                    <c:if test="${eligibility.hasUnpaidFine}">
                        <span class="student-chip warning">Có nợ phạt chưa thanh toán</span>
                    </c:if>
                </section>
            </c:if>

            <section class="student-kpi-grid">
                <article class="student-kpi-card">
                    <span>Đang mượn</span>
                    <strong>${studentBorrowingCount}</strong>
                    <p>Số phiếu mượn hiện còn hiệu lực trên tài khoản của bạn.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Sắp đến hạn</span>
                    <strong>${studentDueSoonCount}</strong>
                    <p>Các phiếu sẽ đến hạn trong ${studentDueSoonWindowDays} ngày tới.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Đơn mua</span>
                    <strong>${studentOrderCount}</strong>
                    <p>Tổng số đơn mua đã tạo, không tính các đơn đã hủy hoặc bị từ chối.</p>
                </article>
                <article class="student-kpi-card">
                    <span>Vi phạm / quá hạn</span>
                    <strong><c:out value="${studentHasViolation ? studentOverdueCount : '0'}" /></strong>
                    <p><c:out value="${studentHasViolation ? 'Bạn đang có phiếu quá hạn cần xử lý.' : 'Hiện chưa có vi phạm hoặc phiếu quá hạn.'}" /></p>
                </article>
            </section>

            <section class="student-highlight-grid">
                <article class="student-highlight-card good">
                    <span>Kho sách hiện tại</span>
                    <strong>${totalBooks}</strong>
                    <p>Kết quả đang khớp với bộ lọc sách hiện tại.</p>
                </article>
                <article class="student-highlight-card ${studentPendingOrderCount gt 0 ? 'warn' : 'neutral'}">
                    <span>Đơn đang xử lý</span>
                    <strong>${studentPendingOrderCount}</strong>
                    <p>Các đơn ở trạng thái chờ duyệt, hàng chờ hoặc sẵn sàng.</p>
                </article>
                <article class="student-highlight-card neutral">
                    <span>Bộ lọc hiện tại</span>
                    <strong><c:out value="${letter eq 'ALL' ? 'ALL' : letter}" /></strong>
                    <p>Trang ${currentPage}/${totalPages} của bộ sưu tập sinh viên.</p>
                </article>
            </section>

            <c:if test="${studentHasViolation}">
                <div class="student-inline-alert warn">
                    Bạn đang có ${studentOverdueCount} phiếu quá hạn. Nên mở ngay trung tâm mượn trả để xử lý hoặc gửi yêu cầu trả sách.
                </div>
            </c:if>
            <c:if test="${studentDueSoonCount gt 0}">
                <div class="student-inline-alert">
                    Có ${studentDueSoonCount} phiếu sẽ đến hạn trong ${studentDueSoonWindowDays} ngày tới. Hãy theo dõi để tránh phát sinh quá hạn.
                </div>
            </c:if>
            <c:if test="${studentPendingOrderCount gt 0}">
                <div class="student-inline-alert success">
                    Bạn có ${studentPendingOrderCount} đơn mua đang được xử lý. Có thể vào trung tâm mượn trả hoặc trang mua sách để theo dõi tiếp.
                </div>
            </c:if>
            <c:if test="${eligibility != null and eligibility.hasUnpaidFine}">
                <div class="student-inline-alert warn">
                    Tài khoản hiện có nợ phạt chưa thanh toán. Hệ thống sẽ chặn gửi yêu cầu mượn mới hoặc đặt giữ chỗ cho tới khi xử lý xong.
                </div>
            </c:if>

            <form class="search-form" method="get" action="${homeUrl}">
                <div class="search-row">
                    <input type="text" name="search" placeholder="Tìm theo tên sách" value="${search}">
                    <input type="text" name="author" placeholder="Tác giả" value="${author}">
                    <select name="categoryId">
                        <option value="">Tất cả thể loại</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryID}" <c:if test="${selectedCategoryId eq category.categoryID}">selected</c:if>>
                                ${category.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                    <select name="publisherId">
                        <option value="">Tất cả nhà xuất bản</option>
                        <c:forEach var="publisher" items="${publishers}">
                            <option value="${publisher.publisherID}" <c:if test="${selectedPublisherId eq publisher.publisherID}">selected</c:if>>
                                ${publisher.publisherName}
                            </option>
                        </c:forEach>
                    </select>
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Lọc</button>
                        <a class="btn-reset" href="${homeUrl}">Đặt lại</a>
                    </div>
                </div>

                <div class="letter-strip">
                    <c:url var="allLetterUrl" value="/home">
                        <c:param name="letter" value="ALL" />
                        <c:param name="search" value="${search}" />
                        <c:param name="author" value="${author}" />
                        <c:param name="categoryId" value="${categoryId}" />
                        <c:param name="publisherId" value="${publisherId}" />
                        <c:param name="page" value="1" />
                    </c:url>
                    <a href="${allLetterUrl}" class="${letter eq 'ALL' ? 'active' : ''}">Tất cả</a>

                    <c:forEach var="letterItem" items="${letters}">
                        <c:url var="letterUrl" value="/home">
                            <c:param name="letter" value="${letterItem}" />
                            <c:param name="search" value="${search}" />
                            <c:param name="author" value="${author}" />
                            <c:param name="categoryId" value="${categoryId}" />
                            <c:param name="publisherId" value="${publisherId}" />
                            <c:param name="page" value="1" />
                        </c:url>
                        <a href="${letterUrl}" class="${letter eq letterItem ? 'active' : ''}">${letterItem}</a>
                    </c:forEach>
                </div>
            </form>

            <div class="section-header">
                <h2>
                    <c:choose>
                        <c:when test="${letter eq 'ALL'}">Bộ sưu tập sách</c:when>
                        <c:otherwise>Sách bắt đầu bằng ${letter}</c:otherwise>
                    </c:choose>
                </h2>
                <span class="note">Tổng kết quả: ${totalBooks}</span>
                <a href="${borrowCenterUrl}" class="section-link">Mở trung tâm mượn trả</a>
            </div>

            <c:choose>
                <c:when test="${empty books}">
                    <div class="empty-box">Không tìm thấy sách phù hợp với bộ lọc hiện tại.</div>
                </c:when>
                <c:otherwise>
                    <div class="book-grid">
                        <c:forEach var="book" items="${books}">
                            <c:url var="bookDetailUrl" value="/home/book">
                                <c:param name="id" value="${book.bookID}" />
                            </c:url>

                            <c:set var="inCart" value="false" />
                            <c:forEach var="cartId" items="${borrowCartIds}">
                                <c:if test="${cartId eq book.bookID}">
                                    <c:set var="inCart" value="true" />
                                </c:if>
                            </c:forEach>

                            <div class="book-card">
                                <a href="${bookDetailUrl}" style="text-decoration:none;color:inherit;display:block;">
                                    <div class="book-visual">
                                        <c:choose>
                                            <c:when test="${not empty book.imageUrl}">
                                                <img src="${book.imageUrl}" alt="${book.bookName}" class="book-image" loading="lazy" decoding="async">
                                            </c:when>
                                            <c:otherwise>
                                                <span>
                                                    <c:choose>
                                                        <c:when test="${not empty book.bookName}">${fn:toUpperCase(fn:substring(book.bookName, 0, 1))}</c:when>
                                                        <c:otherwise>?</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="book-meta">
                                        <h3 class="book-title">${book.bookName}</h3>
                                        <span class="book-subline">${book.quantity} tổng • ${book.available} sẵn</span>
                                        <c:choose>
                                            <c:when test="${book.available le 0}">
                                                <span class="pill out">Hết sách</span>
                                            </c:when>
                                            <c:when test="${book.available le 2 or (book.quantity gt 0 and book.available * 100 le book.quantity * 20)}">
                                                <span class="pill low">Sắp hết</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pill ok">Còn sách</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </a>

                                <div style="padding:0 14px 14px;display:grid;gap:8px;">
                                    <c:choose>
                                        <c:when test="${inCart}">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form" style="display:block;">
                                                <input type="hidden" name="action" value="removeFromCart">
                                                <input type="hidden" name="bookID" value="${book.bookID}">
                                                <button type="submit" class="btn btn-success btn-block">Đã thêm vào giỏ mượn</button>
                                            </form>
                                            <div class="borrow-action-note success">Sách này đang nằm trong giỏ mượn của bạn.</div>
                                        </c:when>
                                        <c:when test="${book.available gt 0 and borrowCartSize lt maxCartSize}">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form" style="display:block;">
                                                <input type="hidden" name="action" value="addToCart">
                                                <input type="hidden" name="bookID" value="${book.bookID}">
                                                <button type="submit" class="btn btn-primary btn-block">Thêm vào giỏ mượn</button>
                                            </form>
                                            <div class="borrow-action-note muted">Chọn nhiều sách rồi gửi yêu cầu mượn ở trung tâm mượn trả.</div>
                                        </c:when>
                                        <c:when test="${book.available le 0}">
                                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form" style="display:block;">
                                                <input type="hidden" name="action" value="placeHold">
                                                <input type="hidden" name="bookID" value="${book.bookID}">
                                                <button type="submit" class="btn btn-warning btn-block">Đặt giữ chỗ</button>
                                            </form>
                                            <div class="borrow-action-note muted">Hệ thống sẽ gửi email khi sách có sẵn và tới lượt bạn.</div>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="student-chip neutral" style="width:100%;">Giỏ mượn đã đầy</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>

            <c:if test="${totalPages gt 1}">
                <div class="pagination">
                    <c:if test="${currentPage gt 1}">
                        <c:url var="prevPageUrl" value="/home">
                            <c:param name="letter" value="${letter}" />
                            <c:param name="search" value="${search}" />
                            <c:param name="author" value="${author}" />
                            <c:param name="categoryId" value="${categoryId}" />
                            <c:param name="publisherId" value="${publisherId}" />
                            <c:param name="page" value="${currentPage - 1}" />
                        </c:url>
                        <a class="page-link" href="${prevPageUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach var="p" begin="1" end="${totalPages}">
                        <c:url var="pageUrl" value="/home">
                            <c:param name="letter" value="${letter}" />
                            <c:param name="search" value="${search}" />
                            <c:param name="author" value="${author}" />
                            <c:param name="categoryId" value="${categoryId}" />
                            <c:param name="publisherId" value="${publisherId}" />
                            <c:param name="page" value="${p}" />
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage lt totalPages}">
                        <c:url var="nextPageUrl" value="/home">
                            <c:param name="letter" value="${letter}" />
                            <c:param name="search" value="${search}" />
                            <c:param name="author" value="${author}" />
                            <c:param name="categoryId" value="${categoryId}" />
                            <c:param name="publisherId" value="${publisherId}" />
                            <c:param name="page" value="${currentPage + 1}" />
                        </c:url>
                        <a class="page-link" href="${nextPageUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </main>

        <aside class="sidebar-right">
            <div class="section-title">Theo dõi cá nhân</div>
            <div class="dashboard-side-summary">
                <span class="student-chip soft">Đang mượn ${studentBorrowingCount}</span>
                <span class="student-chip warning">Đến hạn ${studentDueSoonCount}</span>
                <span class="student-chip ${studentHasViolation ? 'warning' : 'success'}">
                    <c:out value="${studentHasViolation ? 'Quá hạn ' : 'Ổn định '}" />${studentOverdueCount}
                </span>
            </div>

            <c:choose>
                <c:when test="${empty holds}">
                    <div class="empty-box">Không có sách đang mượn.</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="hold" items="${holds}">
                        <div class="hold-card ${fn:toLowerCase(hold.status) eq 'overdue' ? 'is-overdue' : 'is-borrowing'}">
                            <h3>Phiếu mượn #${hold.borrowID}</h3>
                            <div class="hold-meta">
                                Ngày mượn: ${hold.borrowDate}<br>
                                Hạn trả: ${hold.dueDate}
                            </div>
                            <span class="hold-status ${fn:toLowerCase(hold.status) eq 'overdue' ? 'overdue' : 'borrowing'}">
                                <c:choose>
                                    <c:when test="${fn:toLowerCase(hold.status) eq 'overdue'}">Quá hạn</c:when>
                                    <c:when test="${fn:toLowerCase(hold.status) eq 'borrowing'}">Đang mượn</c:when>
                                    <c:otherwise>${hold.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty studentHolds}">
                <div class="section-title" style="margin-top:18px;">Đặt giữ chỗ</div>
                <c:forEach var="sh" items="${studentHolds}">
                    <div class="hold-card">
                        <h3><c:out value="${sh.bookName}" /></h3>
                        <div class="hold-meta">
                            Đặt lúc: ${sh.holdDate}
                            <c:if test="${not empty sh.notifiedDate}">
                                <br>Đã thông báo: ${sh.notifiedDate}
                            </c:if>
                            <c:if test="${not empty sh.expireDate}">
                                <br>Giữ đến: ${sh.expireDate}
                            </c:if>
                        </div>
                        <div style="margin-top:10px;display:flex;gap:8px;flex-wrap:wrap;">
                            <span class="student-chip ${fn:toLowerCase(sh.status) eq 'notified' ? 'success' : 'warning'}">
                                <c:choose>
                                    <c:when test="${fn:toLowerCase(sh.status) eq 'notified'}">Sách đã sẵn sàng</c:when>
                                    <c:otherwise>Đang chờ hàng đợi</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form" style="display:block;margin-top:10px;">
                            <input type="hidden" name="action" value="cancelHold">
                            <input type="hidden" name="holdID" value="${sh.holdID}">
                            <button type="submit" class="btn btn-danger btn-block">Hủy giữ chỗ</button>
                        </form>
                    </div>
                </c:forEach>
            </c:if>
        </aside>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
