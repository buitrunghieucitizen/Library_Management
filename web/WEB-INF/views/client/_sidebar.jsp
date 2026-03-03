<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String currentPath = request.getRequestURI();
%>
<aside class="sidebar-left">
    <div class="section-title">Portal</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/home" class="<%= currentPath.contains("/home") ? "active" : "" %>">
            Student Home
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/books">
            Book Catalog
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/borrows?action=list">
            Borrow Center
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Account</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/index.jsp">
            Dashboard
        </a>
    </div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/logout">
            Dang xuat
        </a>
    </div>
</aside>
