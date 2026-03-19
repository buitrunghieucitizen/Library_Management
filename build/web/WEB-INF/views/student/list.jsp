<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="pageTitle" value="Danh sách sinh viên" />
    <c:set var="activeTab" value="students" />
    <%@ include file="../admin/layout/_admin_header.jsp" %>

    <div class="container">
        <div class="panel">
            <div class="section-header">
                <div>
                    <h2>Danh sách sinh viên</h2>
                    <div class="note">Tổng bản ghi: ${totalItems}</div>
                </div>
                <c:if test="${isAdmin}">
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/students?action=create">+ Thêm sinh viên</a>
                </c:if>
            </div>

            <c:if test="${not empty param.msg}">
                <div class="msg"><c:out value="${param.msg}" /></div>
            </c:if>
            <c:if test="${not empty param.error or not empty error}">
                <div class="error"><c:out value="${not empty param.error ? param.error : error}" /></div>
            </c:if>

            <form method="get" action="${pageContext.request.contextPath}/admin/students" class="student-search-form">
                <input type="hidden" name="action" value="list">
                <input type="text" name="search" value="${param.search}" placeholder="Tìm theo mã, tên, email hoặc SĐT sinh viên...">
                <button class="btn-apply" type="submit">🔍 Tìm kiếm</button>
            </form>

            <div class="student-table-wrap">
                <div class="table-scroll">
                    <table>
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Mã</th>
                                <th>Tên sinh viên</th>
                                <th>Email</th>
                                <th>Số điện thoại</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${students}" varStatus="loop">
                                <tr>
                                    <td>${loop.index + 1}</td>
                                    <td>${s.studentID}</td>
                                    <td>
                                        <div class="student-name-cell">
                                            <span class="student-avatar-badge">${fn:substring(s.studentName, 0, 1)}</span>
                                            <strong>${s.studentName}</strong>
                                        </div>
                                    </td>
                                    <td>${s.email}</td>
                                    <td>${s.phone}</td>
                                    <td class="actions">
                                        <c:if test="${isAdmin}">
                                            <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/students?action=edit&id=${s.studentID}">✏️ Sửa</a>
                                            <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/students?action=delete&id=${s.studentID}" onclick="return confirm('Bạn có chắc chắn muốn xóa sinh viên này?')">🗑️ Xóa</a>
                                        </c:if>
                                        <c:if test="${not isAdmin}">
                                            <span class="text-subtle">Chỉ xem</span>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty students}">
                                <tr>
                                    <td colspan="6">
                                        <div class="student-empty-state">
                                            <div class="empty-icon">📋</div>
                                            <p>Chưa có sinh viên nào.</p>
                                        </div>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/students">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                            <c:if test="${not empty param.search}">
                                <c:param name="search" value="${param.search}"/>
                            </c:if>
                        </c:url>
                        <a class="page-link" href="${prevUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/students">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                            <c:if test="${not empty param.search}">
                                <c:param name="search" value="${param.search}"/>
                            </c:if>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/students">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${currentPage + 1}"/>
                            <c:if test="${not empty param.search}">
                                <c:param name="search" value="${param.search}"/>
                            </c:if>
                        </c:url>
                        <a class="page-link" href="${nextUrl}">Trang sau</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
<%@ include file="../admin/layout/_admin_footer.jsp" %>
