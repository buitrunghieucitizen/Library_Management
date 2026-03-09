<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quan ly don hang</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
</head>
<body>
    <div class="navbar">
        <h1>Quan ly thu vien</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chu</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Muon tra</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Don hang</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">Tep sach</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/books">Sach</a>
            <a href="${pageContext.request.contextPath}/admin/students">Sinh vien</a>
            <a href="${pageContext.request.contextPath}/admin/authors">Tac gia</a>
            <a href="${pageContext.request.contextPath}/admin/categories">The loai</a>
            <a href="${pageContext.request.contextPath}/admin/publishers">Nha xuat ban</a>
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhan vien</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Dang xuat</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quan ly don hang</h2>

            <c:if test="${not empty param.msg}">
                <div class="msg">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error">${param.error}</div>
            </c:if>

            <form method="get" action="${pageContext.request.contextPath}/admin/orders" class="search-form">
                <input type="hidden" name="action" value="list">
                <div class="search-row search-row-orders">
                    <input type="text" name="search" value="${search}" placeholder="Tim theo ma don, sinh vien, ten sach">
                    <select name="status">
                        <option value="ALL" ${status eq 'ALL' ? 'selected' : ''}>Tat ca trang thai</option>
                        <option value="Pending" ${status eq 'Pending' ? 'selected' : ''}>Dang cho</option>
                        <option value="Approved" ${status eq 'Approved' ? 'selected' : ''}>Da duyet</option>
                        <option value="Rejected" ${status eq 'Rejected' ? 'selected' : ''}>Da tu choi</option>
                    </select>
                    <div class="search-actions">
                        <button class="btn-apply" type="submit">Loc</button>
                        <a class="btn-reset" href="${pageContext.request.contextPath}/admin/orders">Dat lai</a>
                    </div>
                </div>
                <div class="note">Tong don hang: ${totalItems}</div>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>Ma</th>
                        <th>Sinh vien</th>
                        <th>Xu ly boi</th>
                        <th>Ngay dat</th>
                        <th>Tong tien</th>
                        <th>Trang thai</th>
                        <th>Chi tiet</th>
                        <th>Hanh dong</th>
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
                                    ${order.status eq 'Pending' ? 'Dang cho' : (order.status eq 'Approved' ? 'Da duyet' : (order.status eq 'Rejected' ? 'Da tu choi' : order.status))}
                                </span>
                            </td>
                            <td>${empty order.items ? 'Khong co chi tiet' : order.items}</td>
                            <td>
                                <c:if test="${order.status eq 'Pending'}">
                                    <div class="actions">
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="approve">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="page" value="${currentPage}">
                                            <button class="btn btn-approve" type="submit">Duyet</button>
                                        </form>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/orders" class="inline-form">
                                            <input type="hidden" name="action" value="reject">
                                            <input type="hidden" name="orderID" value="${order.orderID}">
                                            <input type="hidden" name="search" value="${search}">
                                            <input type="hidden" name="status" value="${status}">
                                            <input type="hidden" name="page" value="${currentPage}">
                                            <button class="btn btn-reject" type="submit">Tu choi</button>
                                        </form>
                                    </div>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty orders}">
                        <tr>
                            <td colspan="8" class="empty-row">Chua co don hang nao.</td>
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
                        <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trang truoc</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="p">
                        <c:url var="pageUrl" value="/admin/orders">
                            <c:param name="action" value="list"/>
                            <c:param name="search" value="${search}"/>
                            <c:param name="status" value="${status}"/>
                            <c:param name="page" value="${p}"/>
                        </c:url>
                        <a class="page-link ${p eq currentPage ? 'active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${p}</a>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <c:url var="nextUrl" value="/admin/orders">
                            <c:param name="action" value="list"/>
                            <c:param name="search" value="${search}"/>
                            <c:param name="status" value="${status}"/>
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
