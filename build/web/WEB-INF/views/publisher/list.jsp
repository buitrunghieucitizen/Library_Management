<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách nhà xuất bản</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="publishers" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <h2>Danh sách nhà xuất bản</h2>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/publishers?action=create">+ Thêm nhà xuất bản</a>
        <div class="note">Tổng bản ghi: ${totalItems}</div>

        <table>
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Tên nhà xuất bản</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${publishers}">
                    <tr>
                        <td>${p.publisherID}</td>
                        <td>${p.publisherName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/publishers?action=edit&id=${p.publisherID}">Sửa</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${p.publisherID}" onclick="return confirm('Xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty publishers}">
                    <tr><td colspan="3" class="empty-row-lg">Chưa có nhà xuất bản.</td></tr>
                </c:if>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <c:url var="prevUrl" value="/admin/publishers">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${currentPage - 1}"/>
                    </c:url>
                    <a class="page-link" href="${prevUrl}">Trang trước</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:url var="pageUrl" value="/admin/publishers">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${p}"/>
                    </c:url>
                    <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <c:url var="nextUrl" value="/admin/publishers">
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
