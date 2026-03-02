<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Library Manager</title>
    <style>
      * { margin: 0; padding: 0; box-sizing: border-box; }
      body { font-family: 'Segoe UI', Tahoma, sans-serif; background: #f0f2f5; }
      .navbar {
        background: linear-gradient(135deg, #1e3c72, #2a5298);
        padding: 15px 30px;
        display: flex;
        align-items: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      }
      .nav-links { display: flex; gap: 20px; }
      .nav-right { margin-left: auto; display: flex; align-items: center; gap: 18px; }
      .nav-user { color: #dce6f5; font-size: 14px; font-weight: 500; }
      .btn-logout {
        background: rgba(255,255,255,0.2);
        color: #fff;
        padding: 6px 14px;
        border-radius: 6px;
        text-decoration: none;
        font-size: 14px;
        transition: 0.2s;
      }
      .btn-logout:hover { background: #d9534f; }
      .navbar h1 { color: #fff; font-size: 22px; margin-right: 24px; }
      .navbar a {
        color: #dce6f5;
        text-decoration: none;
        font-size: 15px;
        padding: 8px 14px;
        border-radius: 6px;
        transition: all 0.2s;
      }
      .navbar a:hover { background: rgba(255, 255, 255, 0.15); color: #fff; }
      .container { max-width: 950px; margin: 40px auto; padding: 0 20px; }
      .card {
        background: #fff;
        border-radius: 12px;
        padding: 28px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
        margin-bottom: 20px;
      }
      .card h2 { color: #1e3c72; margin-bottom: 12px; }
      .card p { color: #475569; line-height: 1.6; }
      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
        gap: 14px;
        margin-top: 18px;
      }
      .grid-item {
        background: linear-gradient(135deg, #2563eb, #1d4ed8);
        color: #fff;
        padding: 22px;
        border-radius: 10px;
        text-decoration: none;
        transition: transform 0.2s, box-shadow 0.2s;
      }
      .grid-item:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(37, 99, 235, 0.35);
      }
      .grid-item h3 { font-size: 17px; margin-bottom: 5px; }
      .grid-item span { font-size: 13px; opacity: 0.9; }
    </style>
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
            <c:if test="${roleId == 8}">
                <c:set var="isStudent" value="true" />
            </c:if>
        </c:forEach>
    </c:if>

    <div class="navbar">
      <h1>Library Manager</h1>
      <div class="nav-links">
          <c:choose>
              <c:when test="${isAdmin}">
                  <a href="${pageContext.request.contextPath}/books">Books</a>
                  <a href="${pageContext.request.contextPath}/students">Students</a>
                  <a href="${pageContext.request.contextPath}/borrows?action=list">Borrow</a>
                  <a href="${pageContext.request.contextPath}/authors">Authors</a>
                  <a href="${pageContext.request.contextPath}/categories">Categories</a>
                  <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
              </c:when>
              <c:when test="${isStaff}">
                  <a href="${pageContext.request.contextPath}/borrows?action=list">Xác nhận trả & Đơn hàng</a>
                  <a href="${pageContext.request.contextPath}/books">Books</a>
                  <a href="${pageContext.request.contextPath}/students">Students</a>
              </c:when>
              <c:when test="${isStudent}">
                  <a href="${pageContext.request.contextPath}/borrows?action=list">Mượn & Mua sách</a>
              </c:when>
          </c:choose>
      </div>
      <div class="nav-right">
          <span class="nav-user">Xin chào, ${sessionScope.staff.staffName} (${isAdmin ? 'Admin' : (isStaff ? 'Staff' : 'Student')})</span>
          <a class="btn-logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
      </div>
    </div>

    <div class="container">
      <div class="card">
        <c:choose>
            <c:when test="${isAdmin}">
                <h2>Màn hình Admin</h2>
                <p>Quản trị viên có toàn quyền quản lý hệ thống: sách, sinh viên, tác giả, thể loại, nhà xuất bản và các giao dịch mượn/trả.</p>
            </c:when>
            <c:when test="${isStaff}">
                <h2>Màn hình Staff</h2>
                <p>Nhân viên có quyền xác nhận trả sách và quản lý các đơn hàng mua sách.</p>
            </c:when>
            <c:when test="${isStudent}">
                <h2>Màn hình Học sinh</h2>
                <p>Bạn có thể xem danh sách sách còn sẵn, mượn sách và gửi yêu cầu trả sách hoặc mua sách.</p>
            </c:when>
        </c:choose>
      </div>

      <div class="grid">
        <c:choose>
            <c:when test="${isAdmin}">
                <a class="grid-item" href="${pageContext.request.contextPath}/books">
                  <h3>Books</h3>
                  <span>Quản lý danh sách sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/students">
                  <h3>Students</h3>
                  <span>Quản lý sinh viên</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/borrows?action=list">
                  <h3>Borrow</h3>
                  <span>Xác nhận trả sách & quản lý</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/authors">
                  <h3>Authors</h3>
                  <span>Quản lý tác gia</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/categories">
                  <h3>Categories</h3>
                  <span>Quản lý thể loại</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/publishers">
                  <h3>Publishers</h3>
                  <span>Quản lý nhà xuất bản</span>
                </a>
            </c:when>
            <c:when test="${isStaff}">
                <a class="grid-item" href="${pageContext.request.contextPath}/borrows?action=list">
                  <h3>Xác nhận trả</h3>
                  <span>Xác nhận các yêu cầu trả sách từ học sinh</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/books">
                  <h3>Books</h3>
                  <span>Xem danh sách sách</span>
                </a>
                <a class="grid-item" href="${pageContext.request.contextPath}/students">
                  <h3>Students</h3>
                  <span>Xem danh sách sinh viên</span>
                </a>
            </c:when>
            <c:when test="${isStudent}">
                <a class="grid-item" href="${pageContext.request.contextPath}/borrows?action=list">
                  <h3>Mượn & Mua</h3>
                  <span>Xem sách, mượn hoặc mua sách</span>
                </a>
            </c:when>
        </c:choose>
      </div>
    </div>
</body>
</html>
