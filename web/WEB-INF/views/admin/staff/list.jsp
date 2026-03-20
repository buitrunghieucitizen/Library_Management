<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="pageTitle" value="Quản lý nhân viên" />
    <c:set var="activeTab" value="staffs" />
    <%@ include file="../layout/_admin_header.jsp" %>

    <div class="container">
        <div class="panel">
            <div class="section-header">
                <div>
                    <h2>Quản lý nhân viên</h2>
                    <div class="note">Tổng bản ghi: ${totalItems}</div>
                </div>
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/staffs?action=create">+ Thêm nhân viên</a>
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
                        <th>Mã</th><th>Tên</th><th>Username</th><th>Email</th><th>Password</th><th>Vai trò</th><th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${staffRows}">
                        <tr>
                            <td>${row.staff.staffID}</td>
                            <td>${row.staff.staffName}</td>
                            <td>${row.staff.username}</td>
                            <td>${row.staff.email}</td>
                            <td><span class="secret-mask">********</span></td>
                            <td>${row.roleNames}</td>
                            <td class="actions">
                                <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/staffs?action=edit&id=${row.staff.staffID}">Sửa</a>
                                <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/staffs?action=delete&id=${row.staff.staffID}" onclick="return confirm('Xóa nhân viên này?')">Xóa</a>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty staffRows}">
                        <tr><td colspan="7" class="empty-row">Chưa có tài khoản nhân viên nào.</td></tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/staffs"><c:param name="action" value="list"/><c:param name="page" value="${currentPage - 1}"/></c:url>
                        <a class="page-link" href="${prevUrl}">Trang trước</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/staffs"><c:param name="action" value="list"/><c:param name="page" value="${p}"/></c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>
                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/staffs"><c:param name="action" value="list"/><c:param name="page" value="${currentPage + 1}"/></c:url>
                        <a class="page-link" href="${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
<%@ include file="../layout/_admin_footer.jsp" %>
