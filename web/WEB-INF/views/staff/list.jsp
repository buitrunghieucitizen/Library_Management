<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly nhan vien</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="staffs" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <h2>Quan ly nhan vien</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/staffs?action=create">Them nhan vien</a>

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
                        <th>Ten</th>
                        <th>Username</th>
                        <th>Password</th>
                        <th>Vai tro</th>
                        <th>Hanh dong</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${staffRows}">
                        <tr>
                            <td>${row.staff.staffID}</td>
                            <td>${row.staff.staffName}</td>
                            <td>${row.staff.username}</td>
                            <td>${row.staff.password}</td>
                            <td>${row.roleNames}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/staffs?action=edit&id=${row.staff.staffID}">Sua</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/staffs?action=delete&id=${row.staff.staffID}" onclick="return confirm('Xoa nhan vien nay?')">Xoa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty staffRows}">
                        <tr>
                            <td colspan="6" class="empty-row">Chua co tai khoan nhan vien nao.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/staffs">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trang truoc</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/staffs">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/staffs">
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
