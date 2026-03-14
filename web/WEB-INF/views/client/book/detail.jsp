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
<body>
    <%@ include file="../_header.jsp" %>

    <c:url var="homeUrl" value="/home" />
    <c:url var="borrowCenterUrl" value="/borrows">
        <c:param name="action" value="list" />
    </c:url>

    <div class="layout">
        <%@ include file="../_sidebar.jsp" %>

        <main class="content">
            <a class="back-link" href="${homeUrl}">Quay ve trang sinh vien</a>

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
                            <span>The loai</span>
                            <strong>${empty category ? '-' : category.categoryName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>Nha xuat ban</span>
                            <strong>${empty publisher ? '-' : publisher.publisherName}</strong>
                        </div>
                        <div class="meta-card">
                            <span>So luong</span>
                            <strong>${book.quantity} cuon</strong>
                        </div>
                        <div class="meta-card">
                            <span>Co san</span>
                            <strong>${book.available} cuon</strong>
                        </div>
                    </div>

                    <span class="status-pill ${book.available gt 0 ? 'ok' : 'out'}">
                        <c:choose>
                            <c:when test="${book.available gt 0}">Co the muon</c:when>
                            <c:otherwise>Tam het sach</c:otherwise>
                        </c:choose>
                    </span>

                    <div class="action-row">
                        <c:if test="${book.available gt 0}">
                            <form method="post" action="${pageContext.request.contextPath}/borrows">
                                <input type="hidden" name="action" value="borrow">
                                <input type="hidden" name="bookID" value="${book.bookID}">
                                <button class="btn-primary" type="submit">Muon sach</button>
                            </form>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/borrows">
                            <input type="hidden" name="action" value="addBuyList">
                            <input type="hidden" name="bookID" value="${book.bookID}">
                            <button class="btn-secondary" type="submit">Them vao danh sach can mua</button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/borrows">
                            <input type="hidden" name="action" value="buy">
                            <input type="hidden" name="bookID" value="${book.bookID}">
                            <button class="btn-buy" type="submit">Mua nhanh</button>
                        </form>

                        <a class="btn-ghost" href="${borrowCenterUrl}">Mo trung tam muon tra</a>
                    </div>

                    <div class="detail-note">
                        Ban co the them sach vao danh sach can mua de gui duyet tung quyen hoac gui duyet tat ca
                        tai man hinh Trung tam muon va mua sach.
                    </div>
                </div>
            </section>
        </main>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
