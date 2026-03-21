<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>${book.bookName} — Thư viện</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    </head>
    <body class="bg-body-tertiary">
        <%@ include file="../_header.jsp" %>

        <c:url var="homeUrl" value="/home" />
        <c:url var="borrowCenterUrl" value="/borrows"><c:param name="action" value="list"/></c:url>

            <div class="d-flex" style="min-height:calc(100vh - 56px);">
            <%@ include file="../_sidebar.jsp" %>

            <main class="flex-grow-1 p-3 p-md-4" style="max-width:960px;">
                <a href="${homeUrl}" class="text-decoration-none fw-semibold mb-3 d-inline-block" style="font-size:14px;">&larr; Quay về trang sinh viên</a>

                <div class="bg-white border rounded-4 p-4">
                    <div class="row g-4">
                        <div class="col-md-4">
                            <div class="d-flex align-items-center justify-content-center text-white fw-bold rounded-3"
                                 style="aspect-ratio:3/4;background:linear-gradient(135deg,#1a2744,#2a5298);font-size:4rem;overflow:hidden;">
                                <c:choose>
                                    <c:when test="${not empty book.imageUrl}">
                                        <img src="${book.imageUrl}" alt="${book.bookName}" class="w-100 h-100" style="object-fit:cover;" loading="lazy">
                                    </c:when>
                                    <c:otherwise>${not empty book.bookName ? fn:toUpperCase(fn:substring(book.bookName, 0, 1)) : '?'}</c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <div class="col-md-8">
                            <h1 class="h3 fw-bold mb-1">${book.bookName}</h1>
                            <p class="text-primary fw-semibold mb-3">${authorsText}</p>

                            <div class="row g-2 mb-3">
                                <div class="col-6">
                                    <div class="border rounded-3 p-3">
                                        <div class="text-uppercase text-muted fw-bold mb-1" style="font-size:11px;letter-spacing:.05em;">Thể loại</div>
                                        <div class="fw-semibold" style="font-size:14px;">${empty category ? '-' : category.categoryName}</div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="border rounded-3 p-3">
                                        <div class="text-uppercase text-muted fw-bold mb-1" style="font-size:11px;letter-spacing:.05em;">Nhà xuất bản</div>
                                        <div class="fw-semibold" style="font-size:14px;">${empty publisher ? '-' : publisher.publisherName}</div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="border rounded-3 p-3">
                                        <div class="text-uppercase text-muted fw-bold mb-1" style="font-size:11px;letter-spacing:.05em;">Số lượng</div>
                                        <div class="fw-semibold" style="font-size:14px;">${book.quantity} cuốn</div>
                                    </div>
                                </div>
                                <div class="col-6">
                                    <div class="border rounded-3 p-3">
                                        <div class="text-uppercase text-muted fw-bold mb-1" style="font-size:11px;letter-spacing:.05em;">Có sẵn</div>
                                        <div class="fw-semibold" style="font-size:14px;">${book.available} cuốn</div>
                                    </div>
                                </div>
                            </div>

                            <span class="badge ${book.available gt 0 ? 'text-bg-success' : 'text-bg-danger'} mb-3" style="font-size:12px;">
                                <c:choose>
                                    <c:when test="${book.available gt 0}">Có thể mượn</c:when>
                                    <c:otherwise>Tạm hết sách</c:otherwise>
                                </c:choose>
                            </span>

                            <div class="d-flex flex-wrap gap-2 mb-3">
                                <c:choose>
                                    <%-- Book available: show borrow + buy buttons --%>
                                    <c:when test="${book.available gt 0}">
                                        <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                            <input type="hidden" name="action" value="addToCart">
                                            <input type="hidden" name="bookID" value="${book.bookID}">
                                            <button class="btn btn-primary d-flex align-items-center gap-2" type="submit">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                                                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                                                </svg>
                                                Thêm vào giỏ mượn
                                            </button>
                                        </form>
                                    </c:when>

                                    <%-- Book OUT OF STOCK: show hold button --%>
                                    <c:otherwise>
                                        <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                            <input type="hidden" name="action" value="placeHold">
                                            <input type="hidden" name="bookID" value="${book.bookID}">
                                            <button class="btn btn-warning d-flex align-items-center gap-2 text-white" type="submit">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                                                </svg>
                                                Đặt giữ chỗ — Nhận email khi có sách
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>

                                <form method="post" action="${pageContext.request.contextPath}/borrows" class="m-0">
                                    <input type="hidden" name="action" value="addBuyList">
                                    <input type="hidden" name="bookID" value="${book.bookID}">
                                    <button class="btn btn-outline-secondary" type="submit">Thêm vào danh sách mua</button>
                                </form>

                                <a class="btn btn-outline-secondary" href="${borrowCenterUrl}">Trung tâm mượn trả</a>
                            </div>

                            <div class="bg-body-tertiary rounded-3 p-3" style="font-size:14px;line-height:1.7;color:#64748b;">
                                <c:choose>
                                    <c:when test="${book.available gt 0}">
                                        Sách sẽ được thêm vào giỏ mượn (tối đa 3 quyển). Sau khi chọn xong, gửi yêu cầu mượn
                                        và chờ admin duyệt.
                                    </c:when>
                                    <c:otherwise>
                                        Sách hiện đã hết. Bạn có thể <strong>đặt giữ chỗ</strong> — khi có người trả sách,
                                        hệ thống sẽ gửi email thông báo cho bạn. Bạn có <strong>24 giờ</strong> để đến mượn,
                                        sau đó quyền ưu tiên sẽ chuyển cho người tiếp theo.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <%@ include file="../_footer.jsp" %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
