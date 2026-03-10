<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách thể loại</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="categories" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <h2>Danh sách thể loại</h2>
        <c:if test="${not empty param.error}">
            <div class="error"><c:out value="${param.error}" /></div>
        </c:if>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/categories?action=create">+ Thêm thể loại</a>
        <div class="note">Tổng bản ghi: ${totalItems}</div>

        <table>
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Tên thể loại</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${categories}">
                    <tr>
                        <td>${c.categoryID}</td>
                        <td>${c.categoryName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${c.categoryID}">Sửa</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/categories?action=delete&id=${c.categoryID}" onclick="return confirm('Xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty categories}">
                    <tr><td colspan="3" class="empty-row-lg">Chưa có thể loại.</td></tr>
                </c:if>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <c:url var="prevUrl" value="/admin/categories">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${currentPage - 1}"/>
                    </c:url>
                    <a class="page-link" href="${prevUrl}">Trang trước</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:url var="pageUrl" value="/admin/categories">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${p}"/>
                    </c:url>
                    <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <c:url var="nextUrl" value="/admin/categories">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${currentPage + 1}"/>
                    </c:url>
                    <a class="page-link" href="${nextUrl}">Trang sau</a>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
