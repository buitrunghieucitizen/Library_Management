<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.bookName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
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
            <a class="back-link" href="${homeUrl}">Quay về trang sinh viên</a>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <section class="detail-card">
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

                <div>
                    <h1 class="detail-title">${book.bookName}</h1>
                    <p class="detail-author">${authorsText}</p>

                    <div class="meta-grid">
                        <div class="meta-card">
                            <span>Thể loại</span>
                            <strong>${empty category ? '-' : category.categoryName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>Nhà xuất bản</span>
                            <strong>${empty publisher ? '-' : publisher.publisherName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>Số lượng</span>
                            <strong>${book.quantity} cuốn</strong>
                        </div>
                        <div class="meta-card">
                            <span>Có sẵn</span>
                            <strong>${book.available} cuốn</strong>
                        </div>
                    </div>

                    <span class="status-pill ${book.available gt 0 ? 'ok' : 'out'}">
                        <c:choose>
                            <c:when test="${book.available gt 0}">Có thể mượn</c:when>
                            <c:otherwise>Đã hết sách</c:otherwise>
                        </c:choose>
                    </span>

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

                    <div class="detail-note">
                        Bạn có thể thêm sách vào danh sách cần mua để gửi duyệt từng quyển hoặc gửi duyệt tất cả
                        tại màn hình Trung tâm mượn và mua sách.
                    </div>
                </div>
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
