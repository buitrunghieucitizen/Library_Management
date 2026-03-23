<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sach sach</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
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
            <c:set var="activeTab" value="books" />
            <%@ include file="../admin/_header.jsp" %>
            <c:set var="listPath" value="/admin/books" />
        </c:when>
        <c:otherwise>
            <div class="top-nav">
                <a class="brand" href="${pageContext.request.contextPath}/index.jsp">Quan ly thu vien</a>
                <a class="active" href="${pageContext.request.contextPath}/books?action=list">Sach</a>
                <a href="${pageContext.request.contextPath}/borrows?action=list">Muon va mua sach</a>
                <div class="nav-right">
                    <span><c:out value="${sessionScope.staff.staffName}" default=""/></span>
                    <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
                </div>
            </div>
            <c:set var="listPath" value="/books" />
        </c:otherwise>
    </c:choose>

    <div class="container">
        <div class="panel">
            <div class="section-header">
                <div>
                    <h2>Danh sach sach</h2>
                    <div class="note">Tong ban ghi: ${totalItems}</div>
                </div>
                <c:if test="${isAdminSection && isAdmin}">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/books?action=create">+ Them sach moi</a>
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
                        <th>Ma</th>
                        <th>Ten sach</th>
                        <th>So luong</th>
                        <th>Con lai</th>
                        <th>Ma the loai</th>
                        <th>Ma nha xuat ban</th>
                        <th>Hanh dong</th>
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
                                        <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/books?action=edit&id=${b.bookID}">Sua</a>
                                        <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/books?action=delete&id=${b.bookID}" onclick="return confirm('Ban co chac muon xoa sach nay?')">Xoa</a>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-subtle">Chi xem</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty books}">
                        <tr>
                            <td colspan="7" class="empty-row-lg">Chua co sach nao.</td>
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
                        <a class="page-link" href="${prevUrl}">Trang truoc</a>
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
</body>
</html>
