<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach nha xuat ban</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="publishers" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <h2>Danh sach nha xuat ban</h2>
        <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/publishers?action=create">+ Them nha xuat ban</a>
        <div class="note">Tong ban ghi: ${totalItems}</div>

        <table>
            <thead>
                <tr>
                    <th>Ma</th>
                    <th>Ten nha xuat ban</th>
                    <th>Hanh dong</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${publishers}">
                    <tr>
                        <td>${p.publisherID}</td>
                        <td>${p.publisherName}</td>
                        <td class="actions">
                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/publishers?action=edit&id=${p.publisherID}">Sua</a>
                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/publishers?action=delete&id=${p.publisherID}" onclick="return confirm('Xoa?')">Xoa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty publishers}">
                    <tr><td colspan="3" class="empty-row-lg">Chua co nha xuat ban.</td></tr>
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
                    <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trang truoc</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:url var="pageUrl" value="/admin/publishers">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${p}"/>
                    </c:url>
                    <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <c:url var="nextUrl" value="/admin/publishers">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${currentPage + 1}"/>
                    </c:url>
                    <a class="page-link" href="${pageContext.request.contextPath}${nextUrl}">Trang sau</a>
                </c:if>
            </div>
        </c:if>
    </div>
</body>
</html>
