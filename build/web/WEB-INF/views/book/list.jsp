<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:set var="isStaff" value="false" />
    <c:set var="isStudent" value="false" />
    <c:set var="isAdminSection" value="${requestScope.adminSection}" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
            <c:if test="${roleId == 2 || roleId == 4}">
                <c:set var="isStaff" value="true" />
            </c:if>
            <c:if test="${roleId == 8 || roleId == 9}">
                <c:set var="isStudent" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <c:choose>
        <c:when test="${isAdminSection}">
            <c:set var="activeTab" value="books" />
            <%@ include file="../admin/_header.jsp" %>
        </c:when>
        <c:otherwise>
            <div class="navbar">
                <h1>Quản lý thư viện</h1>
                <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
                <a class="active" href="${pageContext.request.contextPath}/books?action=list">Sách</a>
                <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
                <div class="nav-right">
                    <span><c:out value="${sessionScope.staff.staffName}" default=""/></span>
                    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <div class="container">
        <h2>Danh sách sách</h2>
        <c:if test="${isAdminSection && isAdmin}">
            <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/books?action=create">+ Thêm sách mới</a>
        </c:if>
        <div class="note">Tổng bản ghi: ${totalItems}</div>

        <c:if test="${not empty msg}">
            <div class="msg">${msg}</div>
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
                            <c:if test="${isAdminSection && isAdmin}">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/books?action=edit&id=${b.bookID}">Sửa</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/books?action=delete&id=${b.bookID}" onclick="return confirm('Bạn có chắc muốn xóa?')">Xóa</a>
                            </c:if>
                            <c:if test="${not isAdminSection || not isAdmin}">
                                <span class="text-subtle">Chỉ xem</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty books}">
                    <tr><td colspan="7" class="empty-row-lg">Chưa có sách nào.</td></tr>
                </c:if>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:choose>
                    <c:when test="${isAdminSection}">
                        <c:if test="${currentPage > 1}">
                            <c:url var="prevUrl" value="/admin/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${currentPage - 1}"/>
                            </c:url>
                            <a class="page-link" href="${prevUrl}">Trang trước</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <c:url var="pageUrl" value="/admin/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${p}"/>
                            </c:url>
                            <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url var="nextUrl" value="/admin/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${currentPage + 1}"/>
                            </c:url>
                            <a class="page-link" href="${nextUrl}">Trang sau</a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${currentPage > 1}">
                            <c:url var="prevUrl" value="/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${currentPage - 1}"/>
                            </c:url>
                            <a class="page-link" href="${prevUrl}">Trang trước</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <c:url var="pageUrl" value="/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${p}"/>
                            </c:url>
                            <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url var="nextUrl" value="/books">
                                <c:param name="action" value="list"/>
                                <c:param name="page" value="${currentPage + 1}"/>
                            </c:url>
                            <a class="page-link" href="${nextUrl}">Trang sau</a>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>
</body>
</html>
