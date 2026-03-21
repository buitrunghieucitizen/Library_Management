<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.bookName}</title>
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

    <div class="layout student-layout layout-two-column">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content student-content content-wide">
            <a class="back-link" href="${homeUrl}">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
                Quay về trang sinh viên
            </a>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <section class="detail-card detail-card-extended">
                <div class="book-cover">
                    <c:choose>
                        <c:when test="${not empty book.imageUrl}">
                            <img src="${book.imageUrl}" alt="${book.bookName}" loading="lazy" decoding="async">
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

                <div class="detail-main-copy">
                    <span class="page-hero-kicker">Book Detail</span>
                    <h1 class="detail-title">${book.bookName}</h1>
                    <p class="detail-author">${authorsText}</p>

                    <div class="meta-grid detail-meta-grid">
                        <div class="meta-card">
                            <span>Thể loại</span>
                            <strong>${empty category ? '-' : category.categoryName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>Nhà xuất bản</span>
                            <strong>${empty publisher ? '-' : publisher.publisherName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>Tổng số lượng</span>
                            <strong>${book.quantity} cuốn</strong>
                        </div>
                        <div class="meta-card">
                            <span>Khả dụng</span>
                            <strong>${book.available} cuốn</strong>
                        </div>
                        <div class="meta-card">
                            <span>Vị trí kệ</span>
                            <strong><c:out value="${bookLocationText}" /></strong>
                        </div>
                        <div class="meta-card">
                            <span>Trạng thái</span>
                            <strong><c:out value="${availabilityStatusLabel}" /></strong>
                        </div>
                    </div>

                    <span class="status-pill ${availabilityStatusKey}">
                        <c:out value="${availabilityStatusLabel}" />
                    </span>

                    <div class="detail-callout ${availabilityStatusKey}">
                        <c:out value="${availabilityStatusNote}" />
                    </div>

                    <div class="action-row">
                        <c:if test="${bookFileCount gt 0}">
                            <a class="btn-ghost" href="#book-files">Xem file sách</a>
                        </c:if>

                        <c:if test="${book.available gt 0}">
                            <form method="post" action="${pageContext.request.contextPath}/borrows">
                                <input type="hidden" name="action" value="borrow">
                                <input type="hidden" name="bookID" value="${book.bookID}">
                                <button class="btn-primary" type="submit">Mượn sách</button>
                            </form>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/borrows">
                            <input type="hidden" name="action" value="addBuyList">
                            <input type="hidden" name="bookID" value="${book.bookID}">
                            <button class="btn-secondary" type="submit">Thêm vào danh sách cần mua</button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/borrows">
                            <input type="hidden" name="action" value="buy">
                            <input type="hidden" name="bookID" value="${book.bookID}">
                            <button class="btn-buy" type="submit">Mua nhanh</button>
                        </form>

                        <a class="btn-ghost" href="${borrowCenterUrl}">Mở trung tâm mượn trả</a>
                    </div>
                </div>
            </section>

            <section class="detail-info-grid">
                <article class="card-soft detail-info-card">
                    <div class="section-header-inline">
                        <div>
                            <h2>Mô tả sách</h2>
                            <div class="note">
                                <c:out value="${bookHasManualDescription ? 'Thông tin mô tả đã được thư viện nhập cho đầu sách này.' : 'Mô tả đang dùng bản tóm tắt tự động từ dữ liệu hiện có của thư viện.'}" />
                            </div>
                        </div>
                    </div>
                    <p class="detail-description"><c:out value="${bookDescriptionText}" /></p>
                </article>

                <article class="card-soft detail-info-card">
                    <div class="section-header-inline">
                        <div>
                            <h2>Truy cập nhanh</h2>
                            <div class="note">Mở nhanh các thao tác thường dùng khi bạn đang xem chi tiết sách.</div>
                        </div>
                    </div>
                    <div class="summary-list">
                        <div class="summary-row">
                            <span>Tình trạng kho</span>
                            <strong><c:out value="${availabilityStatusLabel}" /></strong>
                        </div>
                        <div class="summary-row">
                            <span>File số khả dụng</span>
                            <strong>${bookFileCount}</strong>
                        </div>
                        <div class="summary-row">
                            <span>Vị trí kệ</span>
                            <strong><c:out value="${bookLocationText}" /></strong>
                        </div>
                    </div>
                </article>
            </section>

            <section class="card-soft related-books-panel">
                <div class="section-header-inline student-section-head">
                    <div>
                        <h2>Sách liên quan</h2>
                        <div class="note">Gợi ý theo cùng thể loại hoặc cùng nhà xuất bản để bạn chuyển tiếp nhanh.</div>
                    </div>
                    <div class="student-head-badges">
                        <span class="student-chip neutral">${relatedBookCount} gợi ý</span>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty relatedBooks}">
                        <div class="book-file-empty">Chưa có đầu sách liên quan phù hợp để gợi ý thêm.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="related-book-grid">
                            <c:forEach var="relatedBook" items="${relatedBooks}">
                                <c:url var="relatedBookUrl" value="/home/book">
                                    <c:param name="id" value="${relatedBook.bookID}" />
                                </c:url>

                                <a class="related-book-card" href="${relatedBookUrl}">
                                    <div class="related-book-cover">
                                        <c:choose>
                                            <c:when test="${not empty relatedBook.imageUrl}">
                                                <img src="${relatedBook.imageUrl}" alt="${relatedBook.bookName}" loading="lazy" decoding="async">
                                            </c:when>
                                            <c:otherwise>
                                                <span>${fn:toUpperCase(fn:substring(relatedBook.bookName, 0, 1))}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="related-book-copy">
                                        <h3>${relatedBook.bookName}</h3>
                                        <p>${relatedBook.available} sẵn • ${relatedBook.quantity} tổng</p>
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

            <section class="card-soft book-files-panel" id="book-files">
                <div class="section-header-inline student-section-head">
                    <div>
                        <h2>Tài liệu số của sách</h2>
                        <div class="note">Học sinh chỉ nhìn thấy file đang active. Bấm mở để lấy file sách từ liên kết đã được thư viện cấu hình.</div>
                    </div>
                    <div class="student-head-badges">
                        <span class="student-chip neutral">${bookFileCount} file khả dụng</span>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty bookFiles}">
                        <div class="book-file-empty">
                            Sách này chưa có tài liệu số để tải. Bạn vẫn có thể mượn bản in hoặc quay lại sau khi thư viện cập nhật file.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="book-file-grid">
                            <c:forEach var="file" items="${bookFiles}">
                                <c:url var="bookFileOpenUrl" value="/home/book/file">
                                    <c:param name="id" value="${file.bookFileID}" />
                                </c:url>

                                <article class="book-file-card">
                                    <div class="book-file-head">
                                        <div>
                                            <h3><c:out value="${file.fileName}" /></h3>
                                            <p>
                                                <c:out value="${empty file.fileType ? 'Tài liệu số' : file.fileType}" />
                                                <c:if test="${not empty bookFileSizeLabels[file.bookFileID]}">
                                                    • ${bookFileSizeLabels[file.bookFileID]}
                                                </c:if>
                                            </p>
                                        </div>
                                        <span class="student-chip soft">Active</span>
                                    </div>

                                    <div class="book-file-meta">
                                        <span>Cập nhật: <c:out value="${empty file.uploadAt ? 'N/A' : file.uploadAt}" /></span>
                                    </div>

                                    <div class="book-file-actions">
                                        <a class="btn btn-primary" href="${bookFileOpenUrl}" target="_blank" rel="noopener">Mở file</a>
                                    </div>
                                </article>
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
