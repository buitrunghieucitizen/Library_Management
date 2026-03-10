<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách sinh viên</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <c:set var="activeTab" value="students" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <h2>Danh sách sinh viên</h2>
        <c:if test="${isAdmin}">
            <a class="btn btn-primary btn-inline" href="${pageContext.request.contextPath}/admin/students?action=create">+ Thêm sinh viên</a>
        </c:if>
        <div class="note">Tổng bản ghi: ${totalItems}</div>

        <table>
            <thead>
                <tr>
                    <th>Mã</th>
                    <th>Tên</th>
                    <th>Email</th>
                    <th>Số điện thoại</th>
                    <th>Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${students}">
                    <tr>
                        <td>${s.studentID}</td>
                        <td>${s.studentName}</td>
                        <td>${s.email}</td>
                        <td>${s.phone}</td>
                        <td class="actions">
                            <c:if test="${isAdmin}">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/students?action=edit&id=${s.studentID}">Sửa</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/students?action=delete&id=${s.studentID}" onclick="return confirm('Xóa?')">Xóa</a>
                            </c:if>
                            <c:if test="${not isAdmin}">
                                <span class="text-subtle">Chỉ xem</span>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty students}">
                    <tr><td colspan="5" class="empty-row-lg">Chưa có sinh viên.</td></tr>
                </c:if>
            </tbody>
        </table>

        <c:if test="${totalPages > 1}">
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <c:url var="prevUrl" value="/admin/students">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${currentPage - 1}"/>
                    </c:url>
                    <a class="page-link" href="${prevUrl}">Trang trước</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="p">
                    <c:url var="pageUrl" value="/admin/students">
                        <c:param name="action" value="list"/>
                        <c:param name="page" value="${p}"/>
                    </c:url>
                    <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <c:url var="nextUrl" value="/admin/students">
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
