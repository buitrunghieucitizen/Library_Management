<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Cổng sinh viên — Thư viện</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    </head>
    <body class="bg-body-tertiary">
        <%@ include file="../_header.jsp" %>

        <c:url var="homeUrl" value="/home" />
        <c:url var="borrowCenterUrl" value="/borrows"><c:param name="action" value="list" /></c:url>

            <div class="d-flex" style="min-height:calc(100vh - 56px);">
            <%@ include file="../_sidebar.jsp" %>

            <main class="flex-grow-1 p-3 p-md-4" style="max-width:1100px;">

                <%-- Flash messages --%>
                <c:if test="${not empty param.msg}">
                    <div class="alert alert-success alert-dismissible fade show py-2 px-3" role="alert" style="font-size:14px;">
                        ${param.msg}<button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show py-2 px-3" role="alert" style="font-size:14px;">
                        ${param.error}<button type="button" class="btn-close btn-sm" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <%-- Hero --%>
                <div class="rounded-4 text-white p-4 mb-3" style="background:linear-gradient(135deg,#1a2744 0%,#2a5298 100%);">
                    <h1 class="h4 fw-bold mb-1">Cổng thư viện sinh viên</h1>
                    <p class="mb-0 opacity-75" style="font-size:14px;">Tìm sách, lọc theo tác giả / thể loại / nhà xuất bản. Thêm vào giỏ mượn hoặc đặt giữ khi hết sách.</p>
                </div>

                <%-- Eligibility bar --%>
                <c:if test="${eligibility != null}">
                    <div class="d-flex flex-wrap align-items-center gap-3 bg-white border rounded-3 px-3 py-2 mb-3" style="font-size:13px;">
                        <div class="d-flex align-items-center gap-2">
                            <span class="text-muted">Phiếu mượn</span>
                            <span class="badge ${eligibility.remainingSlots gt 0 ? 'text-bg-success' : 'text-bg-danger'}">${eligibility.activeBorrows} / ${maxCartSize}</span>
                        </div>
                        <div class="d-flex align-items-center gap-2">
                            <span class="text-muted">Còn có thể mượn</span>
                            <span class="badge ${eligibility.remainingSlots gt 0 ? 'text-bg-success' : 'text-bg-danger'}">${eligibility.remainingSlots} quyển</span>
                        </div>
                        <c:if test="${eligibility.hasOverdue}"><span class="badge text-bg-danger">Có sách quá hạn!</span></c:if>
                        <c:if test="${eligibility.hasUnpaidFine}"><span class="badge text-bg-danger">Nợ phạt chưa trả!</span></c:if>
                        </div>
                </c:if>

                <%-- Search & Filters --%>
                <div class="bg-white border rounded-3 p-3 mb-3">
                    <form method="get" action="${homeUrl}">
                        <div class="row g-2 mb-2">
                            <div class="col-md-3"><input type="text" class="form-control form-control-sm" name="search" placeholder="Tìm theo tên sách..." value="${search}"></div>
                            <div class="col-md-2"><input type="text" class="form-control form-control-sm" name="author" placeholder="Tác giả..." value="${author}"></div>
                            <div class="col-md-3">
                                <select class="form-select form-select-sm" name="categoryId">
                                    <option value="">Tất cả thể loại</option>
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.categoryID}" <c:if test="${selectedCategoryId eq category.categoryID}">selected</c:if>>${category.categoryName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <select class="form-select form-select-sm" name="publisherId">
                                    <option value="">Tất cả NXB</option>
                                    <c:forEach var="publisher" items="${publishers}">
                                        <option value="${publisher.publisherID}" <c:if test="${selectedPublisherId eq publisher.publisherID}">selected</c:if>>${publisher.publisherName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2 d-flex gap-1">
                                <button class="btn btn-primary btn-sm flex-fill" type="submit">Lọc</button>
                                <a class="btn btn-outline-secondary btn-sm flex-fill" href="${homeUrl}">Đặt lại</a>
                            </div>
                        </div>
                        <div class="d-flex flex-wrap gap-1">
                            <c:url var="allLetterUrl" value="/home"><c:param name="letter" value="ALL"/><c:param name="search" value="${search}"/><c:param name="author" value="${author}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="publisherId" value="${publisherId}"/><c:param name="page" value="1"/></c:url>
                            <a href="${allLetterUrl}" class="btn btn-sm ${letter eq 'ALL' ? 'btn-primary' : 'btn-outline-secondary'}" style="min-width:38px;font-size:12px;padding:4px 6px;">Tất cả</a>
                            <c:forEach var="letterItem" items="${letters}">
                                <c:url var="letterUrl" value="/home"><c:param name="letter" value="${letterItem}"/><c:param name="search" value="${search}"/><c:param name="author" value="${author}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="publisherId" value="${publisherId}"/><c:param name="page" value="1"/></c:url>
                                <a href="${letterUrl}" class="btn btn-sm ${letter eq letterItem ? 'btn-primary' : 'btn-outline-secondary'}" style="width:32px;font-size:12px;padding:4px 0;">${letterItem}</a>
                            </c:forEach>
                        </div>
                    </form>
                </div>

                <%-- Section header --%>
                <div class="d-flex align-items-center justify-content-between mb-3">
                    <div>
                        <h2 class="h6 fw-bold mb-0">
                            <c:choose><c:when test="${letter eq 'ALL'}">Bộ sưu tập sách</c:when><c:otherwise>Sách bắt đầu bằng "${letter}"</c:otherwise></c:choose>
                                </h2>
                                    <small class="text-muted">${totalBooks} kết quả</small>
                    </div>
                    <a href="${borrowCenterUrl}" class="btn btn-sm btn-outline-primary">Trung tâm mượn trả &rarr;</a>
                </div>

                <%-- Book Grid --%>
                <c:choose>
                    <c:when test="${empty books}">
                        <div class="text-center text-muted border rounded-3 bg-white py-5">Không tìm thấy sách phù hợp.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="row row-cols-2 row-cols-sm-3 row-cols-md-4 row-cols-xl-5 g-3">
                            <c:forEach var="book" items="${books}">
                                <c:url var="bookDetailUrl" value="/home/book"><c:param name="id" value="${book.bookID}"/></c:url>

                                <c:set var="inCart" value="false" />
                                <c:forEach var="cartId" items="${borrowCartIds}">
                                    <c:if test="${cartId eq book.bookID}"><c:set var="inCart" value="true"/></c:if>
                                </c:forEach>

                                <div class="col">
                                    <div class="card h-100 border ${inCart eq 'true' ? 'border-primary border-2' : ''}" style="border-radius:12px;overflow:hidden;">
                                        <a href="${bookDetailUrl}" class="text-decoration-none text-dark">
                                            <div class="d-flex align-items-center justify-content-center text-white fw-bold"
                                                 style="aspect-ratio:3/4;background:linear-gradient(135deg,#1a2744,#2a5298);font-size:2rem;">
                                                <c:choose>
                                                    <c:when test="${not empty book.imageUrl}">
                                                        <img src="${book.imageUrl}" alt="${book.bookName}" class="w-100 h-100" style="object-fit:cover;" loading="lazy">
                                                    </c:when>
                                                    <c:otherwise>${not empty book.bookName ? fn:toUpperCase(fn:substring(book.bookName, 0, 1)) : '?'}</c:otherwise>
                                                </c:choose>
                                            </div>
                                            <div class="p-2">
                                                <div class="fw-semibold text-truncate" style="font-size:13px;">${book.bookName}</div>
                                                <span class="badge mt-1 ${book.available gt 0 ? 'text-bg-success' : 'text-bg-danger'}" style="font-size:10px;">
                                                    <c:choose>
                                                        <c:when test="${book.available gt 0}">${book.available} có sẵn</c:when>
                                                        <c:otherwise>Hết sách</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                        </a>

                                        <%-- ACTION BUTTONS --%>
                                        <div class="p-2 pt-0 mt-auto">
                                            <c:choose>
                                                <%-- Already in cart --%>
                                                <c:when test="${inCart eq 'true'}">
                                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                                        <input type="hidden" name="action" value="removeFromCart">
                                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                                        <button type="submit" class="btn btn-success btn-sm w-100 d-flex align-items-center justify-content-center gap-1" style="font-size:12px;">
                                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
                                                            Trong giỏ
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <%-- Available: add to cart --%>
                                                <c:when test="${book.available gt 0 && borrowCartSize lt maxCartSize}">
                                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                                        <input type="hidden" name="action" value="addToCart">
                                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                                        <button type="submit" class="btn btn-outline-primary btn-sm w-100 d-flex align-items-center justify-content-center gap-1" style="font-size:12px;">
                                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
                                                            Mượn
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <%-- OUT OF STOCK: show Hold button --%>
                                                <c:when test="${book.available le 0}">
                                                    <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                                        <input type="hidden" name="action" value="placeHold">
                                                        <input type="hidden" name="bookID" value="${book.bookID}">
                                                        <button type="submit" class="btn btn-outline-warning btn-sm w-100 d-flex align-items-center justify-content-center gap-1" style="font-size:12px;">
                                                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                                            Đặt giữ chỗ
                                                        </button>
                                                    </form>
                                                </c:when>

                                                <%-- Cart full --%>
                                                <c:otherwise>
                                                    <span class="btn btn-light btn-sm w-100 disabled" style="font-size:12px;">Giỏ đầy</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <%-- Pagination --%>
                <c:if test="${totalPages gt 1}">
                    <nav class="mt-3">
                        <ul class="pagination pagination-sm justify-content-center">
                            <c:if test="${currentPage gt 1}">
                                <c:url var="prevPageUrl" value="/home"><c:param name="letter" value="${letter}"/><c:param name="search" value="${search}"/><c:param name="author" value="${author}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="publisherId" value="${publisherId}"/><c:param name="page" value="${currentPage - 1}"/></c:url>
                                <li class="page-item"><a class="page-link" href="${prevPageUrl}">&laquo;</a></li>
                                </c:if>
                                <c:forEach var="p" begin="1" end="${totalPages}">
                                    <c:url var="pageUrl" value="/home"><c:param name="letter" value="${letter}"/><c:param name="search" value="${search}"/><c:param name="author" value="${author}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="publisherId" value="${publisherId}"/><c:param name="page" value="${p}"/></c:url>
                                <li class="page-item ${p eq currentPage ? 'active' : ''}"><a class="page-link" href="${pageUrl}">${p}</a></li>
                                </c:forEach>
                                <c:if test="${currentPage lt totalPages}">
                                    <c:url var="nextPageUrl" value="/home"><c:param name="letter" value="${letter}"/><c:param name="search" value="${search}"/><c:param name="author" value="${author}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="publisherId" value="${publisherId}"/><c:param name="page" value="${currentPage + 1}"/></c:url>
                                <li class="page-item"><a class="page-link" href="${nextPageUrl}">&raquo;</a></li>
                                </c:if>
                        </ul>
                    </nav>
                </c:if>
            </main>

            <%-- RIGHT SIDEBAR --%>
            <aside class="d-none d-xl-block bg-white border-start p-3" style="width:300px;flex-shrink:0;">
                <div class="text-uppercase text-muted fw-bold mb-2" style="font-size:11px;letter-spacing:.06em;">Phiếu mượn hiện tại</div>
                <c:choose>
                    <c:when test="${empty holds}">
                        <div class="text-center text-muted border rounded-3 py-4 mb-3" style="font-size:13px;border-style:dashed !important;">Không có sách đang mượn.</div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="hold" items="${holds}">
                            <div class="border rounded-3 p-3 mb-2">
                                <div class="fw-semibold mb-1" style="font-size:14px;">Phiếu #${hold.borrowID}</div>
                                <div class="text-muted" style="font-size:12px;">Ngày mượn: ${hold.borrowDate}<br>Hạn trả: ${hold.dueDate}</div>
                                <span class="badge mt-1 <c:choose><c:when test="${fn:toLowerCase(hold.status) eq 'overdue'}">text-bg-danger</c:when><c:when test="${fn:toLowerCase(hold.status) eq 'pending'}">text-bg-warning</c:when><c:otherwise>text-bg-info</c:otherwise></c:choose>" style="font-size:11px;">
                                    <c:choose>
                                        <c:when test="${fn:toLowerCase(hold.status) eq 'overdue'}">Quá hạn</c:when>
                                        <c:when test="${fn:toLowerCase(hold.status) eq 'borrowing'}">Đang mượn</c:when>
                                        <c:when test="${fn:toLowerCase(hold.status) eq 'pending'}">Chờ duyệt</c:when>
                                        <c:otherwise>${hold.status}</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>

                <%-- Student's active holds --%>
                <c:if test="${not empty studentHolds}">
                    <div class="text-uppercase text-muted fw-bold mb-2 mt-3" style="font-size:11px;letter-spacing:.06em;">Sách đang giữ chỗ</div>
                    <c:forEach var="sh" items="${studentHolds}">
                        <div class="border rounded-3 p-3 mb-2">
                            <div class="fw-semibold text-truncate" style="font-size:13px;">${sh.bookName}</div>
                            <div class="text-muted" style="font-size:11px;">Đặt lúc: ${sh.holdDate}</div>
                            <div class="d-flex align-items-center justify-content-between mt-1">
                                <span class="badge ${sh.status eq 'Notified' ? 'text-bg-success' : 'text-bg-warning'}" style="font-size:10px;">
                                    <c:choose>
                                        <c:when test="${sh.status eq 'Notified'}">Sách đã có!</c:when>
                                        <c:otherwise>Đang chờ</c:otherwise>
                                    </c:choose>
                                </span>
                                <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                    <input type="hidden" name="action" value="cancelHold">
                                    <input type="hidden" name="holdID" value="${sh.holdID}">
                                    <button type="submit" class="btn btn-link btn-sm text-danger p-0" style="font-size:11px;">Hủy</button>
                                </form>
                            </div>
                        </div>
                    </c:forEach>
                </c:if>
            </aside>
        </div>

        <%@ include file="../_footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
