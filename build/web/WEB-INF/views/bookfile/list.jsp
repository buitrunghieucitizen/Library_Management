<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý tệp sách</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <div class="navbar">
        <h1>Quản lý thư viện</h1>
        <a href="${pageContext.request.contextPath}/index.jsp">Trang chủ</a>
        <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
        <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
        <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
        <c:if test="${isAdmin}">
            <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
        </c:if>
        <div class="nav-right">
            <span>${sessionScope.staff.staffName}</span>
            <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
        </div>
    </div>

    <div class="container">
        <div class="panel">
            <h2>Quản lý tệp sách</h2>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/bookfiles?action=create">Thêm tệp sách</a>

            <c:if test="${not empty param.msg}">
                <div class="msg mb-3">${param.msg}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="error mb-3">${param.error}</div>
            </c:if>

            <table>
                <thead>
                    <tr>
                        <th>Mã</th>
                        <th>Sách</th>
                        <th>Nhân viên</th>
                        <th>Tệp</th>
                        <th>Liên kết</th>
                        <th>Loại</th>
                        <th>Kích thước</th>
                        <th>Ngày tải</th>
                        <th>Kích hoạt</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="row" items="${bookFiles}">
                        <tr>
                            <td>${row.bookFileID}</td>
                            <td>${row.bookName}</td>
                            <td>${row.staffName}</td>
                            <td>${row.fileName}</td>
                            <td><a href="${row.fileUrl}" target="_blank">${row.fileUrl}</a></td>
                            <td>${row.fileType}</td>
                            <td>${row.fileSize}</td>
                            <td>${row.uploadAt}</td>
                            <td>${row.active ? 'Có' : 'Không'}</td>
                            <td>
                                <div class="actions">
                                    <a class="btn btn-warning" href="${pageContext.request.contextPath}/admin/bookfiles?action=edit&id=${row.bookFileID}">Sửa</a>
                                    <a class="btn btn-danger" href="${pageContext.request.contextPath}/admin/bookfiles?action=delete&id=${row.bookFileID}" onclick="return confirm('Xóa tệp sách này?')">Xóa</a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty bookFiles}">
                        <tr>
                            <td colspan="10" class="empty-row">Chưa có tệp sách nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>


