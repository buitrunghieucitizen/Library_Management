<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="isAdmin" value="false" />
    <c:set var="isAdminSection" value="${requestScope.adminSection}" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <c:choose>
        <c:when test="${isAdminSection}">
            <c:set var="pageTitle" value="Danh sách sách" />
            <c:set var="activeTab" value="books" />
            <%@ include file="../admin/layout/_admin_header.jsp" %>
            <c:set var="listPath" value="/admin/books" />
        </c:when>
        <c:otherwise>
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Danh sách sách</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
            </head>
            <body class="student-body">
            <%@ include file="../client/_header.jsp" %>
            <c:url var="borrowCenterUrl" value="/borrows">
                <c:param name="action" value="list" />
            </c:url>
            <div class="layout student-layout layout-two-column">
                <%@ include file="../client/_sidebar.jsp" %>
                <main class="content student-content content-wide">
                    <section class="page-hero">
                        <div>
                            <span class="page-hero-kicker">Library Catalog</span>
                            <h1>Danh mục sách</h1>
                            <p>Duyệt toàn bộ kho sách hiện có và chuyển nhanh sang trung tâm mượn trả để thao tác với từng đầu sách.</p>
                        </div>
                        <div class="page-hero-actions">
                            <a class="hero-action primary" href="${borrowCenterUrl}">Mở trung tâm mượn trả</a>
                            <a class="hero-action secondary" href="${pageContext.request.contextPath}/home">Về trang sinh viên</a>
                        </div>
                    </section>

                    <section class="student-kpi-grid">
                        <article class="student-kpi-card">
                            <span>Tổng bản ghi</span>
                            <strong>${totalItems}</strong>
                            <p>Số lượng sách đang có trong danh mục hiện tại.</p>
                        </article>
                        <article class="student-kpi-card">
                            <span>Trang hiện tại</span>
                            <strong>${currentPage}/${totalPages}</strong>
                            <p>Điều hướng danh mục theo phân trang.</p>
                        </article>
                        <article class="student-kpi-card">
                            <span>Trạng thái truy cập</span>
                            <strong>Student</strong>
                            <p>Bạn đang xem ở chế độ chỉ đọc dành cho sinh viên.</p>
                        </article>
                        <article class="student-kpi-card">
                            <span>Tác vụ nhanh</span>
                            <strong>Mượn / Mua</strong>
                            <p>Chuyển sang trung tâm giao dịch để thao tác với sách.</p>
                        </article>
                    </section>
            <c:set var="listPath" value="/books" />
        </c:otherwise>
    </c:choose>

    <div class="container">
        <div class="panel">
            <div class="section-header">
                <div>
                    <h2>Danh sách sách</h2>
                    <div class="note">Tổng bản ghi: ${totalItems}</div>
                </div>
                <c:if test="${isAdminSection && isAdmin}">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/books?action=create">+ Thêm sách mới</a>
                </c:if>
            </div>

            <c:if test="${not empty param.msg or not empty msg}">
                <div class="msg"><c:out value="${not empty param.msg ? param.msg : msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error or not empty error}">
                <div class="error"><c:out value="${not empty param.error ? param.error : error}" /></div>
            </c:if>

            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Tên sách</th>
                        <th>Số lượng</th>
                        <th>Còn lại</th>
                        <th>Mã thể loại</th>
                        <th>Mã nhà xuất bản</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${books}">
                        <tr>
                            <td>${b.bookID}</td>
                            <td>${b.bookName}</td>
                            <td>${b.quantity}</td>
                            <td>${b.available}</td>
                            <td>${b.categoryID}</td>
                            <td>${b.publisherID}</td>
                            <td class="actions">
                                <c:choose>
                                    <c:when test="${isAdminSection && isAdmin}">
                                        <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/books?action=edit&id=${b.bookID}">Sửa</a>
                                        <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/books?action=delete&id=${b.bookID}" onclick="return confirm('Bạn có chắc muốn xóa sách này?')">Xóa</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-subtle">Chỉ xem</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty books}">
                        <tr>
                            <td colspan="7" class="empty-row-lg">Chưa có sách nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="${listPath}">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${prevUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="${listPath}">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="${listPath}">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage + 1}"/>
                        </c:url>
                        <a class="page-link" href="${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
    <c:choose>
        <c:when test="${isAdminSection}">
            <%@ include file="../admin/layout/_admin_footer.jsp" %>
        </c:when>
        <c:otherwise>
            </main>
            </div>
            <%@ include file="../client/_footer.jsp" %>
            </body>
            </html>
        </c:otherwise>
    </c:choose>
