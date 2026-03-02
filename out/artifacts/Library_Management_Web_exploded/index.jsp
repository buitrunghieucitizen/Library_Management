<%@page contentType="text/html" pageEncoding="UTF-8"%> <%@taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Library Manager</title>
    <style>
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }
      body {
        font-family: "Segoe UI", Tahoma, sans-serif;
        background: #f0f2f5;
      }
      .navbar {
        background: linear-gradient(135deg, #1e3c72, #2a5298);
        padding: 15px 30px;
        display: flex;
        align-items: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      }
      .nav-links {
        display: flex;
        gap: 30px;
      }
      .nav-right {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 20px;
      }
      .nav-user {
        color: #dce6f5;
        font-size: 14px;
        font-weight: 500;
      }
      .btn-logout {
        background: rgba(255,255,255,0.2);
        color: #fff;
        padding: 6px 14px;
        border-radius: 6px;
        text-decoration: none;
        font-size: 14px;
        transition: 0.2s;
      }
      .btn-logout:hover {
        background: #d9534f;
      }
      .navbar h1 {
        color: #fff;
        font-size: 22px;
      }
      .navbar a {
        color: #dce6f5;
        text-decoration: none;
        font-size: 15px;
        padding: 8px 16px;
        border-radius: 6px;
        transition: all 0.2s;
      }
      .navbar a:hover {
        background: rgba(255, 255, 255, 0.15);
        color: #fff;
      }
      .container {
        max-width: 900px;
        margin: 40px auto;
        padding: 0 20px;
      }
      .card {
        background: #fff;
        border-radius: 12px;
        padding: 30px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
        margin-bottom: 20px;
      }
      .card h2 {
        color: #1e3c72;
        margin-bottom: 15px;
      }
      .card p {
        color: #555;
        line-height: 1.6;
      }
      .grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
        gap: 16px;
        margin-top: 20px;
      }
      .grid-item {
        background: linear-gradient(135deg, #667eea, #764ba2);
        color: #fff;
        padding: 24px;
        border-radius: 10px;
        text-decoration: none;
        transition:
          transform 0.2s,
          box-shadow 0.2s;
      }
      .grid-item:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
      }
      .grid-item h3 {
        font-size: 18px;
        margin-bottom: 6px;
      }
      .grid-item span {
        font-size: 13px;
        opacity: 0.85;
      }
    </style>
  </head>
  <body>
    <div class="navbar">
      <h1>📚 Library Manager</h1>
      <div class="nav-links">
          <a href="${pageContext.request.contextPath}/admin/books">Books</a>
          <a href="${pageContext.request.contextPath}/students">Students</a>
          <a href="${pageContext.request.contextPath}/borrows">Borrow</a>
          <a href="${pageContext.request.contextPath}/authors">Authors</a>
          <a href="${pageContext.request.contextPath}/categories">Categories</a>
          <a href="${pageContext.request.contextPath}/publishers">Publishers</a>
      </div>
      <div class="nav-right">
          <span class="nav-user">Xin chào, ${sessionScope.staff.staffName}!</span>
          <a class="btn-logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
      </div>
    </div>
    <div class="container">
      <div class="card">
        <h2>Chào mừng đến Library Manager V2</h2>
        <p>Hệ thống quản lý thư viện. Chọn một mục bên dưới để bắt đầu.</p>
      </div>
      <div class="grid">
        <a class="grid-item" href="${pageContext.request.contextPath}/books">
          <h3>📖 Sách</h3>
          <span>Quản lý danh sách sách</span>
        </a>
        <a class="grid-item" href="${pageContext.request.contextPath}/students">
          <h3>🎓 Sinh viên</h3>
          <span>Quản lý sinh viên</span>
        </a>
        <a class="grid-item" href="${pageContext.request.contextPath}/borrows">
          <h3>📚 Mượn trả</h3>
          <span>Tạo phiếu mượn và trả sách</span>
        </a>
        <a class="grid-item" href="${pageContext.request.contextPath}/authors">
          <h3>✍️ Tác giả</h3>
          <span>Quản lý tác giả</span>
        </a>
        <a
          class="grid-item"
          href="${pageContext.request.contextPath}/categories"
        >
          <h3>📂 Thể loại</h3>
          <span>Quản lý thể loại sách</span>
        </a>
        <a
          class="grid-item"
          href="${pageContext.request.contextPath}/publishers"
        >
          <h3>🏢 Nhà xuất bản</h3>
          <span>Quản lý nhà xuất bản</span>
        </a>
      </div>
    </div>
  </body>
</html>
