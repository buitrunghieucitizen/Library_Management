<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String currentPath = request.getRequestURI();
%>
<aside class="sidebar-left">
    <div class="section-title">Cổng</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/home" class="<%= currentPath.contains("/home") ? "active" : "" %>">
            Trang sinh viên
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/books">
            Danh mục sách
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/borrows?action=list">
            Trung tâm mượn trả
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Tài khoản</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/index.jsp">
            Bảng điều khiển
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/logout">
            Đăng xuất
        </a>
    </div>
</aside>


