<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cong sinh vien</title>
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
            <section class="hero">
                <h1>Cong thu vien sinh vien</h1>
                <p>Tim sach, loc theo tac gia/the loai/nha xuat ban, va mo nhanh trung tam muon tra.</p>
            </section>

            <form class="search-form" method="get" action="${homeUrl}">
                <div class="search-row">
                    <input type="text" name="search" placeholder="Tim theo ten sach" value="${search}">
                    <input type="text" name="author" placeholder="Tac gia" value="${author}">
                    <select name="categoryId">
                        <option value="">Tat ca the loai</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryID}" <c:if test="${selectedCategoryId eq category.categoryID}">selected</c:if>>
                                ${category.categoryName}
                            </option>
                        </c:forEach>
                    </select>
                    <select name="publisherId">
                        <option value="">Tat ca nha xuat ban</option>
                        <c:forEach var="publisher" items="${publishers}">
                            <option value="${publisher.publisherID}" <c:if test="${selectedPublisherId eq publisher.publisherID}">selected</c:if>>
                                ${publisher.publisherName}
                            </option>
                        </c:forEach>
                    </select>
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Loc</button>
                        <a class="btn-reset" href="${homeUrl}">Dat lai</a>
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
                    <a href="${allLetterUrl}" class="${letter eq 'ALL' ? 'active' : ''}">Tat ca</a>

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
                        <c:when test="${letter eq 'ALL'}">Bo suu tap sach</c:when>
                        <c:otherwise>Sach bat dau bang ${letter}</c:otherwise>
                    </c:choose>
                </h2>
                <span class="note">Tong ket qua: ${totalBooks}</span>
                <a href="${borrowCenterUrl}" class="section-link">Mo trung tam muon tra</a>
            </div>

            <c:choose>
                <c:when test="${empty books}">
                    <div class="empty-box">Khong tim thay sach phu hop voi bo loc hien tai.</div>
                </c:when>
                <c:otherwise>
                    <div class="book-grid">
                        <c:forEach var="book" items="${books}">
                            <c:url var="bookDetailUrl" value="/home/book">
                                <c:param name="id" value="${book.bookID}" />
                            </c:url>

                            <a class="book-card" href="${bookDetailUrl}">
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
                                    <span class="pill ${book.available gt 0 ? 'ok' : 'out'}">
                                        <c:choose>
                                            <c:when test="${book.available gt 0}">${book.available} co san</c:when>
                                            <c:otherwise>Het sach</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </a>
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
                        <a class="page-link" href="${prevPageUrl}">Trang truoc</a>
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
            <div class="section-title">Phieu muon hien tai</div>
            <c:choose>
                <c:when test="${empty holds}">
                    <div class="empty-box">Khong co sach dang muon.</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="hold" items="${holds}">
                        <div class="hold-card">
                            <h3>Phieu muon #${hold.borrowID}</h3>
                            <div class="hold-meta">
                                Ngay muon: ${hold.borrowDate}<br>
                                Han tra: ${hold.dueDate}
                            </div>
                            <span class="hold-status ${fn:toLowerCase(hold.status) eq 'overdue' ? 'overdue' : 'borrowing'}">
                                <c:choose>
                                    <c:when test="${fn:toLowerCase(hold.status) eq 'overdue'}">Qua han</c:when>
                                    <c:when test="${fn:toLowerCase(hold.status) eq 'borrowing'}">Dang muon</c:when>
                                    <c:otherwise>${hold.status}</c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </aside>
    </div>

    <%@ include file="../_footer.jsp" %>
</body>
</html>
