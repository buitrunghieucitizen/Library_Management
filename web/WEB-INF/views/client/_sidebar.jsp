<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 03/03/2026
  Time: 1:34 SA
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%
    List<Integer> _sideRoles = (List<Integer>) session.getAttribute("roles");
    boolean _isStudentSide   = _sideRoles != null && _sideRoles.contains(8);
    String  _currentPath     = request.getRequestURI();
%>
<aside class="sidebar-left">
    <div class="section-title">Collections</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/home"
           class="<%= _currentPath.contains("/home") ? "active" : "" %>">
            <i class="fa-solid fa-books"></i>
            My Books
        </a>
    </div>

    <div class="divider"></div>
    <div class="section-title">Tài khoản</div>
    <div class="nav-item">
        <a href="<%=request.getContextPath()%>/LogoutURL">
            <i class="fa-solid fa-right-from-bracket"></i>
            Đăng xuất
        </a>
    </div>
</aside>