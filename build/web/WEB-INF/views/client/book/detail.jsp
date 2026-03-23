<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.bookName} - Thư viện</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css?v=20260323-student-refresh">
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

    <c:set var="inCart" value="false" />
    <c:forEach var="cartId" items="${borrowCartIds}">
        <c:if test="${cartId eq book.bookID}">
            <c:set var="inCart" value="true" />
        </c:if>
    </c:forEach>

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <a href="${homeUrl}" class="back-link">Quay về trang sinh viên</a>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error-box"><c:out value="${param.error}" /></div>
            </c:if>

            <section class="page-hero detail-page-hero">
                <div class="student-hero-copy">
                    <span class="page-hero-kicker">Book Detail</span>
                    <h1><c:out value="${book.bookName}" /></h1>
                    <p><c:out value="${authorsText}" /></p>
                    <div class="student-hero-badges">
                        <span class="student-chip ${availabilityStatusKey eq 'out' ? 'warning' : 'success'}">
                            <c:out value="${availabilityStatusLabel}" />
                        </span>
                        <span class="student-chip soft">
                            <c:out value="${empty category ? 'Chưa phân loại' : category.categoryName}" />
                        </span>
                        <span class="student-chip neutral">
                            <c:out value="${empty publisher ? 'Chưa có NXB' : publisher.publisherName}" />
                        </span>
                        <span class="student-chip soft">${book.available}/${book.quantity} khả dụng</span>
                    </div>
                </div>
                <div class="page-hero-actions">
                    <a href="${borrowCenterUrl}" class="hero-action primary">Mở trung tâm mượn trả</a>
                    <a href="${homeUrl}" class="hero-action secondary">Tiếp tục khám phá</a>
                </div>
            </section>

            <c:if test="${not bookFieldSupport.descriptionSupported or not bookFieldSupport.shelfLocationSupported or not bookFieldSupport.imageUrlSupported}">
                <div class="student-inline-alert">
                    Một số trường mở rộng của sách chưa được bật đầy đủ trong cơ sở dữ liệu hiện tại. Giao diện vẫn hiển thị các dữ liệu đang có.
                </div>
            </c:if>

            <section class="student-section-grid detail-layout-grid">
                <article class="card-soft detail-card-extended">
                    <div class="detail-visual-column">
                        <div class="book-cover detail-book-cover">
                            <c:choose>
                                <c:when test="${not empty book.imageUrl}">
                                    <img src="${book.imageUrl}" alt="${book.bookName}" loading="lazy" decoding="async">
                                </c:when>
                                <c:otherwise>
                                    <span>
                                        <c:choose>
                                            <c:when test="${not empty book.bookName}">
                                                ${fn:toUpperCase(fn:substring(book.bookName, 0, 1))}
                                            </c:when>
                                            <c:otherwise>?</c:otherwise>
                                        </c:choose>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="detail-status-stack">
                            <span class="pill ${availabilityStatusKey}">
                                <c:out value="${availabilityStatusLabel}" />
                            </span>
                            <span class="student-chip soft">File số ${bookFileCount}</span>
                            <span class="student-chip neutral">Mã sách #${book.bookID}</span>
                        </div>
                    </div>

                    <div class="detail-main-copy">
                        <div class="section-header-inline">
                            <div>
                                <h2>Tổng quan đầu sách</h2>
                                <div class="note">Kiểm tra tồn kho, vị trí, file số và thao tác mượn ngay trong cùng một màn hình.</div>
                            </div>
                        </div>

                        <div class="detail-meta-grid meta-grid">
                            <div class="meta-card">
                                <span>Thể loại</span>
                                <strong><c:out value="${empty category ? 'Chưa cập nhật' : category.categoryName}" /></strong>
                            </div>
                            <div class="meta-card">
                                <span>Nhà xuất bản</span>
                                <strong><c:out value="${empty publisher ? 'Chưa cập nhật' : publisher.publisherName}" /></strong>
                            </div>
                            <div class="meta-card">
                                <span>Kho hiện tại</span>
                                <strong>${book.available} / ${book.quantity}</strong>
                            </div>
                            <div class="meta-card">
                                <span>Vị trí kệ</span>
                                <strong><c:out value="${bookLocationText}" /></strong>
                            </div>
                            <div class="meta-card">
                                <span>Dữ liệu mô tả</span>
                                <strong><c:out value="${bookHasManualDescription ? 'Mô tả gốc' : 'Tạo từ metadata'}" /></strong>
                            </div>
                            <div class="meta-card">
                                <span>Tài nguyên số</span>
                                <strong>${bookFileCount} tệp khả dụng</strong>
                            </div>
                        </div>

                        <div class="detail-callout ${availabilityStatusKey}">
                            <c:out value="${availabilityStatusNote}" />
                        </div>

                        <div class="detail-actions-row action-row">
                            <c:choose>
                                <c:when test="${inCart}">
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="removeFromCart">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button type="submit" class="btn btn-success">Đã có trong giỏ mượn</button>
                                    </form>
                                </c:when>
                                <c:when test="${book.available gt 0 and borrowCartSize lt maxCartSize}">
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="addToCart">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button type="submit" class="btn btn-primary">Thêm vào giỏ mượn</button>
                                    </form>
                                </c:when>
                                <c:when test="${book.available le 0}">
                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                        <input type="hidden" name="action" value="placeHold">
                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                        <button type="submit" class="btn btn-warning">Đặt giữ chỗ</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <span class="student-chip warning">Giỏ mượn đã đầy</span>
                                </c:otherwise>
                            </c:choose>

                            <form method="post" action="${pageContext.request.contextPath}/borrows" class="inline-form">
                                <input type="hidden" name="action" value="addBuyList">
                                <input type="hidden" name="bookID" value="${book.bookID}">
                                <button type="submit" class="btn btn-secondary">Thêm vào danh sách mua</button>
                            </form>

                            <a href="${borrowCenterUrl}" class="btn btn-ghost">Mở trung tâm mượn trả</a>
                        </div>

                        <article class="detail-description-card">
                            <div class="section-header-inline">
                                <div>
                                    <h3>Mô tả sách</h3>
                                    <div class="note">
                                        <c:out value="${bookHasManualDescription ? 'Nội dung do thư viện cập nhật cho đầu sách này.' : 'Nội dung đang được tổng hợp tự động từ metadata hiện có.'}" />
                                    </div>
                                </div>
                            </div>
                            <p class="detail-description"><c:out value="${bookDescriptionText}" /></p>
                        </article>
                    </div>
                </article>

                <div class="panel-stack detail-side-stack">
                    <article class="card-soft detail-info-card">
                        <div class="section-header-inline">
                            <div>
                                <h2>Thông tin nhanh</h2>
                                <div class="note">Các thông số quan trọng để bạn quyết định mượn, đặt giữ chỗ hoặc đọc file số.</div>
                            </div>
                        </div>

                        <div class="summary-list">
                            <div class="summary-row">
                                <span>Tình trạng kho</span>
                                <strong><c:out value="${availabilityStatusLabel}" /></strong>
                            </div>
                            <div class="summary-row">
                                <span>Bản sẵn sàng</span>
                                <strong>${book.available}</strong>
                            </div>
                            <div class="summary-row">
                                <span>Tổng số bản</span>
                                <strong>${book.quantity}</strong>
                            </div>
                            <div class="summary-row">
                                <span>File số</span>
                                <strong>${bookFileCount}</strong>
                            </div>
                            <div class="summary-row">
                                <span>Vị trí kệ</span>
                                <strong><c:out value="${bookLocationProvided ? bookLocationText : 'Chưa cập nhật'}" /></strong>
                            </div>
                        </div>
                    </article>

                    <article class="card-soft detail-info-card" id="book-files">
                        <div class="section-header-inline">
                            <div>
                                <h2>Tài nguyên số</h2>
                                <div class="note">Mở nhanh các file đi kèm đầu sách nếu thư viện đã phát hành bản số.</div>
                            </div>
                            <div class="student-head-badges">
                                <span class="student-chip neutral">${bookFileCount} tệp</span>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${empty bookFiles}">
                                <div class="book-file-empty">Hiện chưa có file số khả dụng cho đầu sách này.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="book-file-grid">
                                    <c:forEach var="bookFile" items="${bookFiles}">
                                        <c:url var="bookFileUrl" value="/home/book/file">
                                            <c:param name="id" value="${bookFile.bookFileID}" />
                                        </c:url>

                                        <article class="book-file-card">
                                            <div class="book-file-head">
                                                <div>
                                                    <h3><c:out value="${bookFile.fileName}" /></h3>
                                                    <p>
                                                        <c:out value="${empty bookFile.fileType ? 'Không rõ định dạng' : bookFile.fileType}" />
                                                        <c:if test="${not empty bookFileSizeLabels[bookFile.bookFileID]}">
                                                            • ${bookFileSizeLabels[bookFile.bookFileID]}
                                                        </c:if>
                                                    </p>
                                                </div>
                                                <span class="student-chip soft">#${bookFile.bookFileID}</span>
                                            </div>

                                            <div class="book-file-meta">
                                                <c:choose>
                                                    <c:when test="${not empty bookFile.uploadAt}">
                                                        Cập nhật lần cuối: <c:out value="${bookFile.uploadAt}" />
                                                    </c:when>
                                                    <c:otherwise>Chưa có thông tin ngày tải lên.</c:otherwise>
                                                </c:choose>
                                            </div>

                                            <div class="book-file-actions">
                                                <a href="${bookFileUrl}" class="btn btn-primary">Mở tài liệu</a>
                                            </div>
                                        </article>
                                    </c:forEach>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </article>
                </div>
            </section>

            <section class="card-soft related-books-panel">
                <div class="section-header-inline student-section-head">
                    <div>
                        <h2>Sách liên quan</h2>
                        <div class="note">Gợi ý theo cùng thể loại hoặc cùng nhà xuất bản để bạn tiếp tục khám phá nhanh hơn.</div>
                    </div>
                    <div class="student-head-badges">
                        <span class="student-chip neutral">${relatedBookCount} gợi ý</span>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty relatedBooks}">
                        <div class="empty-box">Hiện chưa có gợi ý liên quan cho đầu sách này.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="related-book-grid">
                            <c:forEach var="relatedBook" items="${relatedBooks}">
                                <c:url var="relatedBookUrl" value="/home/book">
                                    <c:param name="id" value="${relatedBook.bookID}" />
                                </c:url>

                                <a href="${relatedBookUrl}" class="related-book-card">
                                    <div class="related-book-cover">
                                        <c:choose>
                                            <c:when test="${not empty relatedBook.imageUrl}">
                                                <img src="${relatedBook.imageUrl}" alt="${relatedBook.bookName}" loading="lazy" decoding="async">
                                            </c:when>
                                            <c:otherwise>
                                                <span>
                                                    <c:choose>
                                                        <c:when test="${not empty relatedBook.bookName}">
                                                            ${fn:toUpperCase(fn:substring(relatedBook.bookName, 0, 1))}
                                                        </c:when>
                                                        <c:otherwise>?</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="related-book-copy">
                                        <h3><c:out value="${relatedBook.bookName}" /></h3>
                                        <p>${relatedBook.available} khả dụng • ${relatedBook.quantity} tổng</p>
                                        <c:choose>
                                            <c:when test="${relatedBook.available le 0}">
                                                <span class="pill out">Hết sách</span>
                                            </c:when>
                                            <c:when test="${relatedBook.available le 2 or (relatedBook.quantity gt 0 and relatedBook.available * 100 le relatedBook.quantity * 20)}">
                                                <span class="pill low">Sắp hết</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="pill ok">Còn sách</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
