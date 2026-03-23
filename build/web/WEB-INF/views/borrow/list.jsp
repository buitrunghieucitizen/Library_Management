<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý mượn trả</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="borrows" />
    <%@ include file="../admin/_header.jsp" %>

    <div class="container">
        <div class="panel">
            <h2>Quản lý mượn trả sách</h2>

            <a class="btn btn-primary btn-inline-sm" href="${pageContext.request.contextPath}/admin/borrows?action=create">Tạo phiếu mượn</a>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>
            <div class="note">Tổng bản ghi: ${totalItems}</div>

            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Sinh viên</th>
                        <th>Nhân viên</th>
                        <th>Ngày mượn</th>
                        <th>Hạn trả</th>
                        <th>Ngày trả</th>
                        <th>Trạng thái</th>
                        <th>Sách</th>
                        <th>Hành động</th>
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
                                    <c:when test="${b.status eq 'Borrowing'}"><span class="status borrowing">Đang mượn</span></c:when>
                                    <c:when test="${b.status eq 'Returned'}"><span class="status returned">Đã trả</span></c:when>
                                    <c:when test="${b.status eq 'Overdue'}"><span class="status overdue">Quá hạn</span></c:when>
                                    <c:otherwise><span class="status">${b.status}</span></c:otherwise>
                                </c:choose>
                            </td>
                            <td>${b.items}</td>
                            <td>
                                <c:if test="${b.status ne 'Returned'}">
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/borrows" class="inline-form" onsubmit="return confirm('Xác nhận trả sách cho phiếu này?');">
                                        <input type="hidden" name="action" value="return">
                                        <input type="hidden" name="borrowID" value="${b.borrowID}">
                                        <button class="btn btn-success" type="submit">Xác nhận trả</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty borrows}">
                        <tr>
                            <td colspan="9" class="empty-row">Chưa có phiếu mượn nào.</td>
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
                        <a class="page-link" href="${prevUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/borrows">
                            <c:param name="action" value="list"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/borrows">
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
