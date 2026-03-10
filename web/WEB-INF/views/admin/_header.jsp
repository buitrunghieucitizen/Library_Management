<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%@taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="isAdminNav" value="false" />
<c:if test="${not empty sessionScope.roles}">
  <c:forEach var="roleId" items="${sessionScope.roles}">
    <c:if test="${roleId == 1}">
      <c:set var="isAdminNav" value="true" />
    </c:if>
  </c:forEach>
</c:if>

<div class="navbar admin-fixed">
  <h1>Quản lý thư viện</h1>
  <a
    class="${activeTab eq 'home' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/index.jsp"
    >Trang chủ</a
  >
  <a
    class="${activeTab eq 'books' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/admin/books?action=list"
    >Sách</a
  >
  <a
    class="${activeTab eq 'students' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/admin/students?action=list"
    >Sinh viên</a
  >
  <a
    class="${activeTab eq 'borrows' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/admin/borrows?action=list"
    >Mượn trả</a
  >
  <a
    class="${activeTab eq 'orders' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/admin/orders?action=list"
    >Đơn hàng</a
  >
  <a
    class="${activeTab eq 'bookfiles' ? 'active' : ''}"
    href="${pageContext.request.contextPath}/admin/bookfiles?action=list"
    >Tệp sách</a
  >
  <c:if test="${isAdminNav}">
    <a
      class="${activeTab eq 'authors' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/authors?action=list"
      >Tác giả</a
    >
    <a
      class="${activeTab eq 'categories' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/categories?action=list"
      >Thể loại</a
    >
    <a
      class="${activeTab eq 'publishers' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/publishers?action=list"
      >Nhà xuất bản</a
    >
    <a
      class="${activeTab eq 'staffs' ? 'active' : ''}"
      href="${pageContext.request.contextPath}/admin/staffs?action=list"
      >Nhân viên</a
    >
  </c:if>
  <div class="nav-right">
    <span><c:out value="${sessionScope.staff.staffName}" default="" /></span>
    <a href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
  </div>
</div>
