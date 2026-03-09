<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly muon tra</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="borrows" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <h2>Quan ly muon tra sach</h2>

            <a class="btn btn-primary btn-inline-sm" href="${pageContext.request.contextPath}/admin/borrows?action=create">Tao phieu muon</a>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
            <div class="note">Tong ban ghi: ${totalItems}</div>

            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Sinh vien</th>
                        <th>Nhan vien</th>
                        <th>Ngay muon</th>
                        <th>Han tra</th>
                        <th>Ngay tra</th>
                        <th>Trang thai</th>
                        <th>Sach</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="b" items="${borrows}">
                        <tr>
                            <td>${b.borrowID}</td>
                            <td>${b.studentName}</td>
                            <td>${b.staffName}</td>
                            <td>${b.borrowDate}</td>
                            <td>${b.dueDate}</td>
                            <td><c:out value="${b.returnDate}" default="-"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status borrowing">Dang muon</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status returned">Da tra</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status overdue">Qua han</span></c:when>
                                    <c:otherwise><span class="status">${b.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="inline-form" onsubmit="return confirm('Xac nhan tra sach cho phieu nay?');">
                                        <input type="hidden" name="action" value="return">
                                        <input type="hidden" name="borrowID" value="${b.borrowID}">
                                        <button class="btn btn-success" type="submit">Xac nhan tra</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty borrows}">
                        <tr>
                            <td colspan="9" class="empty-row">Chua co phieu muon nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trang truoc</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage + 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
