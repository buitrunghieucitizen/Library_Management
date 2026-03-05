<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8" />
    <title>Quản lý thư viện</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/book-theme.css">
    
</head>
<body>
    <c:set var="isAdmin" value="false" />
    <c:set var="isStaff" value="false" />
    <c:set var="isStudent" value="false" />
    <c:if test="${not empty sessionScope.roles}">
        <c:forEach var="roleId" items="${sessionScope.roles}">
            <c:if test="${roleId == 1}">
                <c:set var="isAdmin" value="true" />
            </c:if>
            <c:if test="${roleId == 2 || roleId == 4}">
                <c:set var="isStaff" value="true" />
            </c:if>
            <c:if test="${roleId == 8 || roleId == 9}">
                <c:set var="isStudent" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <div class="navbar">
      <h1>Quản lý thư viện</h1>
      <div class="nav-links">
          <c:choose>
              <c:when test="${isAdmin}">
                  <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
                  <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
                  <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
                  <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
                  <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
                  <a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a>
                  <a href="${pageContext.request.contextPath}/admin/categories">Thể loại</a>
                  <a href="${pageContext.request.contextPath}/admin/publishers">Nhà xuất bản</a>
                  <a href="${pageContext.request.contextPath}/admin/staffs?action=list">Nhân viên</a>
              </c:when>
              <c:when test="${isStaff}">
                  <a href="${pageContext.request.contextPath}/admin/borrows?action=list">Mượn trả</a>
                  <a href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a>
                  <a href="${pageContext.request.contextPath}/admin/bookfiles">Tệp sách</a>
                  <a href="${pageContext.request.contextPath}/admin/books">Sách</a>
                  <a href="${pageContext.request.contextPath}/admin/students">Sinh viên</a>
              </c:when>
              <c:when test="${isStudent}">
                  <a href="${pageContext.request.contextPath}/home">Cổng sinh viên</a>
                  <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn và mua sách</a>
              </c:when>
          </c:choose>
      </div>
      <div class="nav-right">
          <span class="nav-user">Xin chào, ${sessionScope.staff.staffName} (${isAdmin ? 'Quản trị' : (isStaff ? 'Nhân viên' : (isStudent ? 'Sinh viên' : 'Chưa phân quyền'))})</span>
          <a class="btn-logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
      </div>
    </div>

    <div class="container">
      <div class="card">
        <c:choose>
            <c:when test="${isAdmin}">
                <h2>Màn hình quản trị</h2>
                <p>Quản trị viên có thể quản lý toàn bộ hệ thống: sách, sinh viên, mượn trả, đơn hàng, tệp sách và tài khoản nhân viên.</p>
            </c:when>
            <c:when test="${isStaff}">
                <h2>Màn hình nhân viên</h2>
                <p>Nhân viên có thể quản lý mượn trả, duyệt đơn hàng, quản lý tệp sách và xem thông tin sách, sinh viên.</p>
            </c:when>
            <c:when test="${isStudent}">
                <h2>Màn hình sinh viên</h2>
                <p>Sinh viên có thể vào cổng sinh viên để tìm sách, sau đó mượn sách, gửi yêu cầu trả sách và đặt mua sách.</p>
            </c:when>
            <c:otherwise>
                <h2>Tài khoản chưa được gán quyền</h2>
                <p>Vui lòng gán vai trò trong bảng StaffRole rồi đăng nhập lại.</p>
            </c:otherwise>
        </c:choose>
      </div>

      <div class="grid">
        <c:choose>
            <c:when test="${isAdmin}">
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/books">
                  <h3>Sách</h3>
                  <span>Quản lý danh sách sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/students">
                  <h3>Sinh viên</h3>
                  <span>Quản lý sinh viên</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/borrows?action=list">
                  <h3>Mượn trả</h3>
                  <span>Quản lý mượn trả sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/orders">
                  <h3>Đơn hàng</h3>
                  <span>Quản lý đơn hàng sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/bookfiles">
                  <h3>Tệp sách</h3>
                  <span>Quản lý tệp đính kèm sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/authors">
                  <h3>Tác giả</h3>
                  <span>Quản lý tác giả</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/categories">
                  <h3>Thể loại</h3>
                  <span>Quản lý thể loại</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/publishers">
                  <h3>Nhà xuất bản</h3>
                  <span>Quản lý nhà xuất bản</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/staffs?action=list">
                  <h3>Nhân viên</h3>
                  <span>Quản lý tài khoản và vai trò</span>
                </a>
            </c:when>
            <c:when test="${isStaff}">
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/borrows?action=list">
                  <h3>Mượn trả</h3>
                  <span>Xác nhận mượn trả sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/orders">
                  <h3>Đơn hàng</h3>
                  <span>Duyệt và từ chối đơn mua sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/bookfiles">
                  <h3>Tệp sách</h3>
                  <span>Thêm, sửa, xóa tệp sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/books">
                  <h3>Sách</h3>
                  <span>Xem danh sách sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/admin/students">
                  <h3>Sinh viên</h3>
                  <span>Xem danh sách sinh viên</span>
                </a>
            </c:when>
            <c:when test="${isStudent}">
                <a class="grid-item" href="${pageContext.request.contextPath}/home">
                  <h3>Cổng sinh viên</h3>
                  <span>Tìm sách và mở giao diện sinh viên mới</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/borrows?action=list">
                  <h3>Mượn và mua</h3>
                  <span>Mượn sách, trả sách và đặt mua</span>
                </a>
            </c:when>
        </c:choose>
      </div>
    </div>
</body>
</html>


