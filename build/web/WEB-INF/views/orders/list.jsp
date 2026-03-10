<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý đơn hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <c:set var="activeTab" value="orders" />
    <%@ include file="../admin/_header.jsp" %>


    <div class="container">
        <div class="panel">
            <h2>Quản lý đơn hàng</h2>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>

            <form method="get" action="${pageContext.request.contextPath}/admin/orders" class="search-form">
                <input type="hidden" name="action" value="list">
                <div class="search-row search-row-orders">
                    <input type="text" name="search" value="${search}" placeholder="Tìm theo mã đơn, sinh viên, tên sách">
                    <select name="status">
                        <option value="ALL" ${status eq 'ALL' ? 'selected' : ''}>Tất cả trạng thái</option>
                        <option value="Pending" ${status eq 'Pending' ? 'selected' : ''}>Đang chờ</option>
                        <option value="Approved" ${status eq 'Approved' ? 'selected' : ''}>Đã duyệt</option>
                        <option value="Rejected" ${status eq 'Rejected' ? 'selected' : ''}>Đã từ chối</option>
                    </select>
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Lọc</button>
                        <a class="btn-reset" href="${pageContext.request.contextPath}/admin/orders">Đặt lại</a>
                    </div>
                </div>
                <div class="note">Tổng đơn hàng: ${totalItems}</div>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Sinh viên</th>
                        <th>Xử lý bởi</th>
                        <th>Ngày đặt</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái</th>
                        <th>Chi tiết</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr>
                            <td>${order.orderID}</td>
                            <td>${order.studentName}</td>
                            <td>${order.staffName}</td>
                            <td>${order.orderDate}</td>
                            <td>${order.totalAmount}</td>
                            <td>
                                <span class="status ${order.status eq 'Pending' ? 'pending' : (order.status eq 'Approved' ? 'approved' : 'rejected')}">
                                    ${order.status eq 'Pending' ? 'Đang chờ' : (order.status eq 'Approved' ? 'Đã duyệt' : (order.status eq 'Rejected' ? 'Đã từ chối' : order.status))}
                                </span>
                            </td>
                            <td>${empty order.items ? 'Không có chi tiết' : order.items}</td>
                            <td>
                                <c:if test="${order.status eq 'Pending'}">
                                    <div class="actions">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="page" value="${currentPage}">
                                            <button class="btn btn-approve" type="submit">Duyệt</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="page" value="${currentPage}">
                                            <button class="btn btn-reject" type="submit">Từ chối</button>
                                        </form>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}">
                        <tr>
                            <td colspan="8" class="empty-row">Chưa có đơn hàng nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <c:url var="prevUrl" value="/admin/orders">
                            <c:param name="action" value="list"/>
                            <c:param name="search" value="${search}"/>
                            <c:param name="status" value="${status}"/>
                            <c:param name="page" value="${currentPage - 1}"/>
                        </c:url>
                        <a class="page-link" href="${prevUrl}">Trang trước</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/orders">
                            <c:param name="action" value="list"/>
                            <c:param name="search" value="${search}"/>
                            <c:param name="status" value="${status}"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/orders">
                            <c:param name="action" value="list"/>
                            <c:param name="search" value="${search}"/>
                            <c:param name="status" value="${status}"/>
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
