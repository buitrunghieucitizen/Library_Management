<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach tac gia</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="authors" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <div class="section-header">
                <div>
                    <h2>Danh sach tac gia</h2>
                    <div class="note">Tong ban ghi: ${totalItems}</div>
                </div>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/authors?action=create">+ Them tac gia</a>
            </div>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error"><c:out value="${param.error}" /></div>
            </c:if>

            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Ten tac gia</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="a" items="${authors}">
                        <tr>
                            <td>${a.authorID}</td>
                            <td>${a.authorName}</td>
                            <td class="actions">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/authors?action=edit&id=${a.authorID}">Sua</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/authors?action=delete&id=${a.authorID}" onclick="return confirm('Xoa tac gia nay?')">Xoa</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty authors}">
                        <tr>
                            <td colspan="3" class="empty-row-lg">Chua co tac gia.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/authors">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${prevUrl}">Trang truoc</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/authors">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/authors">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage + 1}"/>
                        </c:url>
                        <a class="page-link" href="${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
